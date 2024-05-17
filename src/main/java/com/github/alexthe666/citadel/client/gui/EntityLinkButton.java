package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityLinkButton extends Button {
   private static final Map<String, Entity> renderedEntites = new HashMap<>();
   private final EntityLinkData data;
   private final GuiBasicBook bookGUI;
   private final EntityLinkButton.EnttyRenderWindow window = new EntityLinkButton.EnttyRenderWindow();

   public EntityLinkButton(GuiBasicBook bookGUI, EntityLinkData linkData, int k, int l, OnPress o) {
      super(k + linkData.getX() - 12, l + linkData.getY(), (int)(24.0 * linkData.getScale()), (int)(24.0 * linkData.getScale()), new TextComponent(""), o);
      this.data = linkData;
      this.bookGUI = bookGUI;
   }

   public void renderButton(PoseStack posestack, int mouseX, int mouseY, float partialTicks) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int lvt_5_1_ = 0;
      int lvt_6_1_ = 30;
      float f = (float)this.data.getScale();
      RenderSystem.setShaderTexture(0, this.bookGUI.getBookWidgetTexture());
      posestack.pushPose();
      posestack.translate((double)this.x, (double)this.y, 0.0);
      posestack.scale(f, f, 1.0F);
      this.drawBtn(false, posestack, 0, 0, lvt_5_1_, lvt_6_1_, 24, 24);
      Entity model = null;
      EntityType type = (EntityType)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(this.data.getEntity()));
      if (type != null) {
         model = renderedEntites.putIfAbsent(this.data.getEntity(), type.create(Minecraft.getInstance().level));
      }

      posestack.pushPose();
      if (model != null) {
         this.window
            .renderEntityWindow(
               posestack,
               (float)this.x,
               (float)this.y,
               model,
               (float)this.data.getEntityScale() * f,
               this.data.getOffset_x() * f,
               this.data.getOffset_y() * f,
               2,
               2,
               22,
               22
            );
      }

      posestack.popPose();
      RenderSystem.depthFunc(515);
      RenderSystem.disableDepthTest();
      byte var14;
      if (this.isHovered) {
         this.bookGUI.setEntityTooltip(this.data.getHoverText());
         var14 = 48;
      } else {
         var14 = 24;
      }

      int color = this.bookGUI.getWidgetColor();
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      BookBlit.setRGB(r, g, b, 255);
      RenderSystem.setShaderTexture(0, this.bookGUI.getBookWidgetTexture());
      this.drawBtn(!this.isHovered, posestack, 0, 0, var14, lvt_6_1_, 24, 24);
      posestack.popPose();
   }

   public void renderToolTip(PoseStack matrixStack, int mouseX, int mouseY) {
   }

   public void drawBtn(
      boolean color, PoseStack p_238474_1_, int p_238474_2_, int p_238474_3_, int p_238474_4_, int p_238474_5_, int p_238474_6_, int p_238474_7_
   ) {
      if (color) {
         BookBlit.blit(p_238474_1_, p_238474_2_, p_238474_3_, this.getBlitOffset(), (float)p_238474_4_, (float)p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
      } else {
         blit(p_238474_1_, p_238474_2_, p_238474_3_, this.getBlitOffset(), (float)p_238474_4_, (float)p_238474_5_, p_238474_6_, p_238474_7_, 256, 256);
      }
   }

   private class EnttyRenderWindow extends GuiComponent {
      public void renderEntityWindow(
         PoseStack matrixStack, float x, float y, Entity toRender, float renderScale, float offsetX, float offsetY, int minX, int minY, int maxX, int maxY
      ) {
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, -1.0);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.pushPose();
         RenderSystem.enableDepthTest();
         matrixStack.translate(0.0, 0.0, 950.0);
         RenderSystem.colorMask(false, false, false, false);
         fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
         RenderSystem.colorMask(true, true, true, true);
         matrixStack.translate(0.0, 0.0, -950.0);
         RenderSystem.depthFunc(518);
         fill(matrixStack, 22, 22, 2, 2, -16777216);
         RenderSystem.depthFunc(515);
         RenderSystem.setShaderTexture(0, EntityLinkButton.this.bookGUI.getBookWidgetTexture());
         blit(matrixStack, 0, 0, 0.0F, 30.0F, 24, 24, 256, 256);
         if (toRender != null) {
            toRender.tickCount = Minecraft.getInstance().player.tickCount;
            float transitional = Math.max(0.0F, renderScale - 1.0F) * 8.0F;
            GuiBasicBook.drawEntityOnScreen(
               matrixStack,
               (int)(12.0F * renderScale + transitional + x + offsetX),
               (int)(24.0F * renderScale - transitional + y + offsetY),
               10.0F * renderScale,
               false,
               30.0,
               -130.0,
               0.0,
               0.0F,
               0.0F,
               toRender
            );
            RenderSystem.applyModelViewMatrix();
         }

         RenderSystem.depthFunc(518);
         matrixStack.translate(0.0, 0.0, -950.0);
         RenderSystem.colorMask(false, false, false, false);
         fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
         RenderSystem.colorMask(true, true, true, true);
         matrixStack.translate(0.0, 0.0, 950.0);
         RenderSystem.depthFunc(515);
         matrixStack.popPose();
         matrixStack.popPose();
      }
   }
}
