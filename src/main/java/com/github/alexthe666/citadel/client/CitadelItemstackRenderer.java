package com.github.alexthe666.citadel.client;

import com.github.alexthe666.citadel.Citadel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class CitadelItemstackRenderer extends BlockEntityWithoutLevelRenderer {
   public CitadelItemstackRenderer() {
      super(null, null);
   }

   public void renderByItem(
      ItemStack stack, TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      float partialTicks = Minecraft.getInstance().getFrameTime();
      float ticksExisted = (float)Util.getMillis() / 50.0F + partialTicks;
      int id = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.getId();
      if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
         Random random = new Random();
         boolean animateAnyways = false;
         ItemStack toRender = null;
         if (stack.getTag() != null && stack.getTag().contains("DisplayItem")) {
            String displayID = stack.getTag().getString("DisplayItem");
            toRender = new ItemStack((ItemLike)Registry.ITEM.get(new ResourceLocation(displayID)));
            if (stack.getTag().contains("DisplayItemNBT")) {
               try {
                  toRender.setTag(stack.getTag().getCompound("DisplayItemNBT"));
               } catch (Exception var17) {
                  toRender = new ItemStack(Items.BARRIER);
               }
            }
         }

         if (toRender == null) {
            animateAnyways = true;
            toRender = new ItemStack(Items.BARRIER);
         }

         matrixStack.pushPose();
         matrixStack.translate(0.5, 0.5, 0.5);
         if (stack.getTag() != null && stack.getTag().contains("DisplayShake") && stack.getTag().getBoolean("DisplayShake")) {
            matrixStack.translate(
               (double)((random.nextFloat() - 0.5F) * 0.1F), (double)((random.nextFloat() - 0.5F) * 0.1F), (double)((random.nextFloat() - 0.5F) * 0.1F)
            );
         }

         if (animateAnyways || stack.getTag() != null && stack.getTag().contains("DisplayBob") && stack.getTag().getBoolean("DisplayBob")) {
            matrixStack.translate(0.0, (double)(0.05F + 0.1F * Mth.sin(0.3F * ticksExisted)), 0.0);
         }

         if (stack.getTag() != null && stack.getTag().contains("DisplaySpin") && stack.getTag().getBoolean("DisplaySpin")) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(6.0F * ticksExisted));
         }

         if (animateAnyways || stack.getTag() != null && stack.getTag().contains("DisplayZoom") && stack.getTag().getBoolean("DisplayZoom")) {
            float scale = (float)(1.0 + 0.15F * (Math.sin((double)(ticksExisted * 0.3F)) + 1.0));
            matrixStack.scale(scale, scale, scale);
         }

         if (stack.getTag() != null && stack.getTag().contains("DisplayScale") && stack.getTag().getFloat("DisplayScale") != 1.0F) {
            float scale = stack.getTag().getFloat("DisplayScale");
            matrixStack.scale(scale, scale, scale);
         }

         Minecraft.getInstance().getItemRenderer().renderStatic(toRender, transformType, combinedLight, combinedOverlay, matrixStack, buffer, id);
         matrixStack.popPose();
      }

      if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         RenderSystem.enableDepthTest();
         MobEffect effect;
         if (stack.getTag() != null && stack.getTag().contains("DisplayEffect")) {
            String displayID = stack.getTag().getString("DisplayEffect");
            effect = (MobEffect)Registry.MOB_EFFECT.get(new ResourceLocation(displayID));
         } else {
            int size = Registry.MOB_EFFECT.keySet().size();
            int time = (int)(Util.getMillis() / 500L);
            effect = (MobEffect)Registry.MOB_EFFECT.byId(time % size);
            if (effect == null) {
               effect = MobEffects.MOVEMENT_SPEED;
            }
         }

         if (effect == null) {
            effect = MobEffects.MOVEMENT_SPEED;
         }

         MobEffectTextureManager potionspriteuploader = Minecraft.getInstance().getMobEffectTextures();
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 0.5);
         TextureAtlasSprite sprite = potionspriteuploader.get(effect);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, sprite.atlas().location());
         Tesselator tessellator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuilder();
         bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.PARTICLE);
         Matrix4f mx = matrixStack.last().pose();
         int br = 255;
         bufferbuilder.vertex(mx, 1.0F, 1.0F, 0.0F).uv(sprite.getU1(), sprite.getV0()).color(br, br, br, 255).uv2(combinedLight).endVertex();
         bufferbuilder.vertex(mx, 0.0F, 1.0F, 0.0F).uv(sprite.getU0(), sprite.getV0()).color(br, br, br, 255).uv2(combinedLight).endVertex();
         bufferbuilder.vertex(mx, 0.0F, 0.0F, 0.0F).uv(sprite.getU0(), sprite.getV1()).color(br, br, br, 255).uv2(combinedLight).endVertex();
         bufferbuilder.vertex(mx, 1.0F, 0.0F, 0.0F).uv(sprite.getU1(), sprite.getV1()).color(br, br, br, 255).uv2(combinedLight).endVertex();
         tessellator.end();
         matrixStack.popPose();
      }
   }
}