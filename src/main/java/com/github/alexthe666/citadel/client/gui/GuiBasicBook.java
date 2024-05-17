package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import com.github.alexthe666.citadel.client.gui.data.EntityRenderData;
import com.github.alexthe666.citadel.client.gui.data.ImageData;
import com.github.alexthe666.citadel.client.gui.data.ItemRenderData;
import com.github.alexthe666.citadel.client.gui.data.LineData;
import com.github.alexthe666.citadel.client.gui.data.LinkData;
import com.github.alexthe666.citadel.client.gui.data.RecipeData;
import com.github.alexthe666.citadel.client.gui.data.TabulaRenderData;
import com.github.alexthe666.citadel.client.gui.data.Whitespace;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.StringConcatFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.IOUtils;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBasicBook extends Screen {
   private static final ResourceLocation BOOK_PAGE_TEXTURE = new ResourceLocation("citadel:textures/gui/book/book_pages.png");
   private static final ResourceLocation BOOK_BINDING_TEXTURE = new ResourceLocation("citadel:textures/gui/book/book_binding.png");
   private static final ResourceLocation BOOK_WIDGET_TEXTURE = new ResourceLocation("citadel:textures/gui/book/widgets.png");
   private final List<LineData> lines = new ArrayList<>();
   private final List<LinkData> links = new ArrayList<>();
   private final List<ItemRenderData> itemRenders = new ArrayList<>();
   private final List<RecipeData> recipes = new ArrayList<>();
   private final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
   private final List<EntityRenderData> entityRenders = new ArrayList<>();
   private final List<EntityLinkData> entityLinks = new ArrayList<>();
   private final List<ImageData> images = new ArrayList<>();
   private final List<Whitespace> yIndexesToSkip = new ArrayList<>();
   private final Map<String, TabulaModel> renderedTabulaModels = new HashMap<>();
   private final Map<String, Entity> renderedEntites = new HashMap<>();
   private final Map<String, ResourceLocation> textureMap = new HashMap<>();
   protected ItemStack bookStack;
   protected int xSize = 390;
   protected int ySize = 320;
   protected int currentPageCounter = 0;
   protected int maxPagesFromPrinting = 0;
   protected int linesFromJSON = 0;
   protected int linesFromPrinting = 0;
   protected ResourceLocation prevPageJSON;
   protected ResourceLocation currentPageJSON;
   protected ResourceLocation currentPageText = null;
   private BookPageButton buttonNextPage;
   private BookPageButton buttonPreviousPage;
   private BookPage internalPage = null;
   private String writtenTitle = "";
   private int preservedPageIndex = 0;
   private String entityTooltip;
   private int mouseX;
   private int mouseY;

   public GuiBasicBook(ItemStack bookStack, Component title) {
      super(title);
      this.bookStack = bookStack;
      this.currentPageJSON = this.getRootPage();
   }

   public static void drawEntityOnScreen(
      PoseStack stackIn, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity
   ) {
      float customYaw = (float)posX - mouseX;
      float customPitch = (float)posY - mouseY;
      float f = (float)Math.atan((double)(customYaw / 40.0F));
      float f1 = (float)Math.atan((double)(customPitch / 40.0F));
      RenderSystem.applyModelViewMatrix();
      PoseStack posestack1 = new PoseStack();
      posestack1.translate((double)posX, (double)posY, 120.0);
      posestack1.scale(scale, scale, scale);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(follow ? -f1 * 20.0F : 0.0F);
      quaternion.mul(quaternion1);
      posestack1.mulPose(quaternion);
      posestack1.mulPose(Vector3f.XP.rotationDegrees((float)xRot));
      posestack1.mulPose(Vector3f.YP.rotationDegrees((float)yRot - 270.0F));
      posestack1.mulPose(Vector3f.ZP.rotationDegrees((float)zRot));
      if (follow) {
         float yaw = -f * 20.0F - (float)yRot;
         entity.setYRot(yaw);
         entity.setXRot(f1 * 20.0F);
         if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).yBodyRot = yaw;
            ((LivingEntity)entity).yBodyRotO = yaw;
            ((LivingEntity)entity).yHeadRot = yaw;
            ((LivingEntity)entity).yHeadRotO = yaw;
         }

         quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
         quaternion.mul(quaternion1);
      }

      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderdispatcher.overrideCameraOrientation(quaternion1);
      entityrenderdispatcher.setRenderShadow(false);
      BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, posestack1, multibuffersource$buffersource, 15728880));
      multibuffersource$buffersource.endBatch();
      entityrenderdispatcher.setRenderShadow(true);
      entity.setYRot(0.0F);
      entity.setXRot(0.0F);
      if (entity instanceof LivingEntity) {
         ((LivingEntity)entity).yBodyRot = 0.0F;
         ((LivingEntity)entity).yHeadRotO = 0.0F;
         ((LivingEntity)entity).yHeadRot = 0.0F;
      }

      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   public static void drawTabulaModelOnScreen(
      PoseStack stack,
      TabulaModel model,
      ResourceLocation tex,
      int posX,
      int posY,
      float scale,
      boolean follow,
      double xRot,
      double yRot,
      double zRot,
      float mouseX,
      float mouseY
   ) {
      float f = (float)Math.atan((double)(mouseX / 40.0F));
      float f1 = (float)Math.atan((double)(mouseY / 40.0F));
      RenderSystem.applyModelViewMatrix();
      PoseStack matrixstack = new PoseStack();
      matrixstack.translate((double)((float)posX), (double)((float)posY), 120.0);
      matrixstack.scale(scale, scale, scale);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(0.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
      if (follow) {
         quaternion.mul(quaternion1);
      }

      matrixstack.mulPose(quaternion);
      if (follow) {
         matrixstack.mulPose(Vector3f.YP.rotationDegrees(180.0F + f * 40.0F));
      }

      matrixstack.mulPose(Vector3f.XP.rotationDegrees((float)(-xRot)));
      matrixstack.mulPose(Vector3f.YP.rotationDegrees((float)yRot));
      matrixstack.mulPose(Vector3f.ZP.rotationDegrees((float)zRot));
      EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderermanager.overrideCameraOrientation(quaternion1);
      entityrenderermanager.setRenderShadow(false);
      BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> {
         VertexConsumer ivertexbuilder = irendertypebuffer$impl.getBuffer(RenderType.entityCutoutNoCull(tex));
         model.resetToDefaultPose();
         model.renderToBuffer(matrixstack, ivertexbuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   protected void init() {
      super.init();
      this.playBookOpeningSound();
      this.addNextPreviousButtons();
      this.addLinkButtons();
   }

   private void addNextPreviousButtons() {
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;
      this.buttonPreviousPage = (BookPageButton)this.addRenderableWidget(
         new BookPageButton(this, k + 10, l + 180, false, p_214208_1_ -> this.onSwitchPage(false), true)
      );
      this.buttonNextPage = (BookPageButton)this.addRenderableWidget(
         new BookPageButton(this, k + 365, l + 180, true, p_214205_1_ -> this.onSwitchPage(true), true)
      );
   }

   private void addLinkButtons() {
      this.renderables.clear();
      this.clearWidgets();
      this.addNextPreviousButtons();
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;

      for (LinkData linkData : this.links) {
         if (linkData.getPage() == this.currentPageCounter) {
            int maxLength = Math.max(100, Minecraft.getInstance().font.width(linkData.getTitleText()) + 20);
            this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - maxLength / 2, linkData.getY(), 100, 20));
            this.addRenderableWidget(
               new Button(k + linkData.getX() - maxLength / 2, l + linkData.getY(), maxLength, 20, new TextComponent(linkData.getTitleText()), p_213021_1_ -> {
                  this.prevPageJSON = this.currentPageJSON;
                  this.currentPageJSON = new ResourceLocation(this.getTextFileDirectory() + linkData.getLinkedPage());
                  this.preservedPageIndex = this.currentPageCounter;
                  this.currentPageCounter = 0;
                  this.addNextPreviousButtons();
               })
            );
         }

         if (linkData.getPage() > this.maxPagesFromPrinting) {
            this.maxPagesFromPrinting = linkData.getPage();
         }
      }

      for (EntityLinkData linkData : this.entityLinks) {
         if (linkData.getPage() == this.currentPageCounter) {
            this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - 12, linkData.getY(), 100, 20));
            this.addRenderableWidget(new EntityLinkButton(this, linkData, k, l, p_213021_1_ -> {
               this.prevPageJSON = this.currentPageJSON;
               this.currentPageJSON = new ResourceLocation(this.getTextFileDirectory() + linkData.getLinkedPage());
               this.preservedPageIndex = this.currentPageCounter;
               this.currentPageCounter = 0;
               this.addNextPreviousButtons();
            }));
         }

         if (linkData.getPage() > this.maxPagesFromPrinting) {
            this.maxPagesFromPrinting = linkData.getPage();
         }
      }
   }

   private void onSwitchPage(boolean next) {
      if (next) {
         if (this.currentPageCounter < this.maxPagesFromPrinting) {
            this.currentPageCounter++;
         }
      } else if (this.currentPageCounter > 0) {
         this.currentPageCounter--;
      } else if (this.internalPage != null && !this.internalPage.getParent().isEmpty()) {
         this.prevPageJSON = this.currentPageJSON;
         this.currentPageJSON = new ResourceLocation(this.getTextFileDirectory() + this.internalPage.getParent());
         this.currentPageCounter = this.preservedPageIndex;
         this.preservedPageIndex = 0;
      }

      this.refreshSpacing();
   }

   public void render(PoseStack matrixStack, int x, int y, float partialTicks) {
      this.mouseX = x;
      this.mouseY = y;
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int color = this.getBindingColor();
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      this.renderBackground(matrixStack);
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;
      RenderSystem.enableBlend();
      RenderSystem.setShaderTexture(0, this.getBookBindingTexture());
      BookBlit.setRGB(r, g, b, 255);
      BookBlit.blit(matrixStack, k, l, 0.0F, 0.0F, this.xSize, this.ySize, this.xSize, this.ySize);
      RenderSystem.setShaderTexture(0, this.getBookPageTexture());
      BookBlit.setRGB(255, 255, 255, 255);
      BookBlit.blit(matrixStack, k, l, 0.0F, 0.0F, this.xSize, this.ySize, this.xSize, this.ySize);
      if (this.internalPage == null || this.currentPageJSON != this.prevPageJSON || this.prevPageJSON == null) {
         this.internalPage = this.generatePage(this.currentPageJSON);
         if (this.internalPage != null) {
            this.refreshSpacing();
         }
      }

      if (this.internalPage != null) {
         this.writePageText(matrixStack, x, y);
      }

      this.prevPageJSON = this.currentPageJSON;
      super.render(matrixStack, x, y, partialTicks);
      if (this.internalPage != null) {
         matrixStack.pushPose();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         this.renderOtherWidgets(matrixStack, x, y, this.internalPage);
         matrixStack.popPose();
      }

      if (this.entityTooltip != null) {
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, 550.0);
         this.renderTooltip(
            matrixStack, Minecraft.getInstance().font.split(new TranslatableComponent(this.entityTooltip), Math.max(this.width / 2 - 43, 170)), x, y
         );
         this.entityTooltip = null;
         matrixStack.popPose();
      }
   }

   private void refreshSpacing() {
      if (this.internalPage != null) {
         String lang = Minecraft.getInstance().getLanguageManager().getSelected().getCode().toLowerCase();
         this.currentPageText = new ResourceLocation(this.getTextFileDirectory() + lang + "/" + this.internalPage.getTextFileToReadFrom());
         boolean invalid = false;

         try {
            Resource e = Minecraft.getInstance().getResourceManager().getResource(this.currentPageText);
         } catch (Exception var4) {
            invalid = true;
            Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
            this.currentPageText = new ResourceLocation(this.getTextFileDirectory() + "en_us/" + this.internalPage.getTextFileToReadFrom());
         }

         this.readInPageWidgets(this.internalPage);
         this.addWidgetSpacing();
         this.addLinkButtons();
         this.readInPageText(this.currentPageText);
      }
   }

   private Item getItemByRegistryName(String registryName) {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
   }

   private Recipe getRecipeByName(String registryName) {
      try {
         RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
         if (manager.byKey(new ResourceLocation(registryName)).isPresent()) {
            return (Recipe)manager.byKey(new ResourceLocation(registryName)).get();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return null;
   }

   private void addWidgetSpacing() {
      this.yIndexesToSkip.clear();

      for (ItemRenderData itemRenderData : this.itemRenders) {
         Item item = this.getItemByRegistryName(itemRenderData.getItem());
         if (item != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     itemRenderData.getPage(),
                     itemRenderData.getX(),
                     itemRenderData.getY(),
                     (int)(itemRenderData.getScale() * 17.0),
                     (int)(itemRenderData.getScale() * 15.0)
                  )
               );
         }
      }

      for (RecipeData recipeData : this.recipes) {
         Recipe recipe = this.getRecipeByName(recipeData.getRecipe());
         if (recipe != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     recipeData.getPage(),
                     recipeData.getX(),
                     recipeData.getY() - (int)(recipeData.getScale() * 15.0),
                     (int)(recipeData.getScale() * 35.0),
                     (int)(recipeData.getScale() * 60.0),
                     true
                  )
               );
         }
      }

      for (ImageData imageData : this.images) {
         if (imageData != null) {
            this.yIndexesToSkip
               .add(
                  new Whitespace(
                     imageData.getPage(),
                     imageData.getX(),
                     imageData.getY(),
                     (int)(imageData.getScale() * (double)imageData.getWidth()),
                     (int)(imageData.getScale() * (double)imageData.getHeight() * 0.8F)
                  )
               );
         }
      }

      if (!this.writtenTitle.isEmpty()) {
         this.yIndexesToSkip.add(new Whitespace(0, 20, 5, 70, 15));
      }
   }

   private void renderOtherWidgets(PoseStack matrixStack, int x, int y, BookPage page) {
      int color = this.getBindingColor();
      int r = (color & 0xFF0000) >> 16;
      int g = (color & 0xFF00) >> 8;
      int b = color & 0xFF;
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;

      for (ImageData imageData : this.images) {
         if (imageData.getPage() == this.currentPageCounter && imageData != null) {
            ResourceLocation tex = this.textureMap.get(imageData.getTexture());
            if (tex == null) {
               tex = new ResourceLocation(imageData.getTexture());
               this.textureMap.put(imageData.getTexture(), tex);
            }

            float scale = (float)imageData.getScale();
            matrixStack.pushPose();
            matrixStack.translate((double)(k + imageData.getX()), (double)(l + imageData.getY()), 0.0);
            matrixStack.scale(scale, scale, scale);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, tex);
            this.blit(matrixStack, 0, 0, imageData.getU(), imageData.getV(), imageData.getWidth(), imageData.getHeight());
            matrixStack.popPose();
         }
      }

      for (RecipeData recipeData : this.recipes) {
         if (recipeData.getPage() == this.currentPageCounter) {
            matrixStack.pushPose();
            matrixStack.translate((double)(k + recipeData.getX()), (double)(l + recipeData.getY()), 0.0);
            float scale = (float)recipeData.getScale();
            matrixStack.scale(scale, scale, scale);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, this.getBookWidgetTexture());
            this.blit(matrixStack, 0, 0, 0, 88, 116, 53);
            matrixStack.popPose();
         }
      }

      for (RecipeData recipeDatax : this.recipes) {
         if (recipeDatax.getPage() == this.currentPageCounter) {
            Recipe recipe = this.getRecipeByName(recipeDatax.getRecipe());
            int playerTicks = Minecraft.getInstance().player.tickCount;
            if (recipe != null) {
               float scale = (float)recipeDatax.getScale();
               PoseStack poseStack = RenderSystem.getModelViewStack();

               for (int i = 0; i < recipe.getIngredients().size(); i++) {
                  Ingredient ing = (Ingredient)recipe.getIngredients().get(i);
                  ItemStack stack = ItemStack.EMPTY;
                  if (!ing.isEmpty()) {
                     if (ing.getItems().length > 1) {
                        int currentIndex = (int)((float)playerTicks / 20.0F % (float)ing.getItems().length);
                        stack = ing.getItems()[currentIndex];
                     } else {
                        stack = ing.getItems()[0];
                     }
                  }

                  if (!stack.isEmpty()) {
                     poseStack.pushPose();
                     poseStack.translate((double)k, (double)l, 32.0);
                     poseStack.translate(
                        (double)((int)((float)recipeDatax.getX() + (float)(i % 3 * 20) * scale)),
                        (double)((int)((float)recipeDatax.getY() + (float)(i / 3 * 20) * scale)),
                        0.0
                     );
                     poseStack.scale(scale, scale, scale);
                     this.itemRenderer.blitOffset = 100.0F;
                     this.itemRenderer.renderAndDecorateItem(stack, 0, 0);
                     this.itemRenderer.blitOffset = 0.0F;
                     poseStack.popPose();
                  }
               }

               poseStack.pushPose();
               poseStack.translate((double)k, (double)l, 32.0);
               float finScale = scale * 1.5F;
               poseStack.translate((double)((float)recipeDatax.getX() + 70.0F * finScale), (double)((float)recipeDatax.getY() + 10.0F * finScale), 0.0);
               poseStack.scale(finScale, finScale, finScale);
               this.itemRenderer.blitOffset = 100.0F;
               this.itemRenderer.renderAndDecorateItem(recipe.getResultItem(), 0, 0);
               this.itemRenderer.blitOffset = 0.0F;
               poseStack.popPose();
            }
         }
      }

      for (TabulaRenderData tabulaRenderData : this.tabulaRenders) {
         if (tabulaRenderData.getPage() == this.currentPageCounter) {
            TabulaModel model = null;
            ResourceLocation texture;
            if (this.textureMap.get(tabulaRenderData.getTexture()) != null) {
               texture = this.textureMap.get(tabulaRenderData.getTexture());
            } else {
               texture = this.textureMap.put(tabulaRenderData.getTexture(), new ResourceLocation(tabulaRenderData.getTexture()));
            }

            if (this.renderedTabulaModels.get(tabulaRenderData.getModel()) != null) {
               model = this.renderedTabulaModels.get(tabulaRenderData.getModel());
            } else {
               try {
                  model = new TabulaModel(
                     TabulaModelHandler.INSTANCE
                        .loadTabulaModel("/assets/" + tabulaRenderData.getModel().split(":")[0] + "/" + tabulaRenderData.getModel().split(":")[1])
                  );
               } catch (Exception var23) {
                  Citadel.LOGGER.warn("Could not load in tabula model for book at " + tabulaRenderData.getModel());
               }

               this.renderedTabulaModels.put(tabulaRenderData.getModel(), model);
            }

            if (model != null && texture != null) {
               float scale = (float)tabulaRenderData.getScale();
               drawTabulaModelOnScreen(
                  matrixStack,
                  model,
                  texture,
                  k + tabulaRenderData.getX(),
                  l + tabulaRenderData.getY(),
                  30.0F * scale,
                  tabulaRenderData.isFollow_cursor(),
                  tabulaRenderData.getRot_x(),
                  tabulaRenderData.getRot_y(),
                  tabulaRenderData.getRot_z(),
                  (float)this.mouseX,
                  (float)this.mouseY
               );
            }
         }
      }

      for (EntityRenderData data : this.entityRenders) {
         if (data.getPage() == this.currentPageCounter) {
            Entity modelx = null;
            EntityType type = (EntityType)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(data.getEntity()));
            if (type != null) {
               modelx = this.renderedEntites.putIfAbsent(data.getEntity(), type.create(Minecraft.getInstance().level));
            }

            if (modelx != null) {
               float scale = (float)data.getScale();
               modelx.tickCount = Minecraft.getInstance().player.tickCount;
               if (data.getEntityData() != null) {
                  try {
                     CompoundTag tag = TagParser.parseTag(data.getEntityData());
                     modelx.load(tag);
                  } catch (CommandSyntaxException var22) {
                     var22.printStackTrace();
                  }
               }

               drawEntityOnScreen(
                  matrixStack,
                  k + data.getX(),
                  l + data.getY(),
                  30.0F * scale,
                  data.isFollow_cursor(),
                  data.getRot_x(),
                  data.getRot_y(),
                  data.getRot_z(),
                  (float)this.mouseX,
                  (float)this.mouseY,
                  modelx
               );
            }
         }
      }

      for (ItemRenderData itemRenderData : this.itemRenders) {
         if (itemRenderData.getPage() == this.currentPageCounter) {
            Item item = this.getItemByRegistryName(itemRenderData.getItem());
            if (item != null) {
               float scale = (float)itemRenderData.getScale();
               ItemStack stackx = new ItemStack(item);
               if (itemRenderData.getItemTag() != null && !itemRenderData.getItemTag().isEmpty()) {
                  CompoundTag tag = null;

                  try {
                     tag = TagParser.parseTag(itemRenderData.getItemTag());
                  } catch (CommandSyntaxException var21) {
                     var21.printStackTrace();
                  }

                  stackx.setTag(tag);
               }

               this.itemRenderer.blitOffset = 100.0F;
               matrixStack.pushPose();
               PoseStack poseStack = RenderSystem.getModelViewStack();
               poseStack.pushPose();
               poseStack.translate((double)k, (double)l, 0.0);
               poseStack.scale(scale, scale, scale);
               this.itemRenderer.renderAndDecorateItem(stackx, itemRenderData.getX(), itemRenderData.getY());
               this.itemRenderer.blitOffset = 0.0F;
               poseStack.popPose();
               matrixStack.popPose();
               RenderSystem.applyModelViewMatrix();
            }
         }
      }
   }

   private void writePageText(PoseStack matrixStack, int x, int y) {
      Font font = this.font;
      int k = (this.width - this.xSize) / 2;
      int l = (this.height - this.ySize + 128) / 2;
      if (this.currentPageCounter == 0 && !this.writtenTitle.isEmpty()) {
         String actualTitle = I18n.get(this.writtenTitle, new Object[0]);
         matrixStack.pushPose();
         float scale = 2.0F;
         if (font.width(actualTitle) > 80) {
            scale = 2.0F - Mth.clamp((float)(font.width(actualTitle) - 80) * 0.011F, 0.0F, 1.95F);
         }

         matrixStack.translate((double)(k + 10), (double)(l + 10), 0.0);
         matrixStack.scale(scale, scale, 1.0F);
         font.draw(matrixStack, actualTitle, 0.0F, 0.0F, this.getTitleColor());
         matrixStack.popPose();
      }

      this.buttonNextPage.visible = this.currentPageCounter < this.maxPagesFromPrinting;
      boolean rootPage = this.currentPageJSON.equals(this.getRootPage());
      this.buttonPreviousPage.visible = this.currentPageCounter > 0 || !rootPage;

      for (LineData line : this.lines) {
         if (line.getPage() == this.currentPageCounter) {
            font.draw(matrixStack, line.getText(), (float)(k + 10 + line.getxIndex()), (float)(l + 10 + line.getyIndex() * 12), this.getTextColor());
         }
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   protected void playBookOpeningSound() {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
   }

   protected void playBookClosingSound() {
   }

   protected abstract int getBindingColor();

   protected int getWidgetColor() {
      return this.getBindingColor();
   }

   protected int getTextColor() {
      return 3158064;
   }

   protected int getTitleColor() {
      return 12233880;
   }

   public abstract ResourceLocation getRootPage();

   public abstract String getTextFileDirectory();

   protected ResourceLocation getBookPageTexture() {
      return BOOK_PAGE_TEXTURE;
   }

   protected ResourceLocation getBookBindingTexture() {
      return BOOK_BINDING_TEXTURE;
   }

   protected ResourceLocation getBookWidgetTexture() {
      return BOOK_WIDGET_TEXTURE;
   }

   protected void playPageFlipSound() {
   }

   @Nullable
   protected BookPage generatePage(ResourceLocation res) {
      Resource resource = null;
      BookPage page = null;

      try {
         resource = Minecraft.getInstance().getResourceManager().getResource(res);

         try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            InputStream inputstream = resource.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
            page = BookPage.deserialize(reader);
         } catch (IOException var6) {
            var6.printStackTrace();
         }

         return page;
      } catch (IOException var7) {
         return null;
      }
   }

   protected void readInPageWidgets(BookPage page) {
      this.links.clear();
      this.itemRenders.clear();
      this.recipes.clear();
      this.tabulaRenders.clear();
      this.entityRenders.clear();
      this.images.clear();
      this.entityLinks.clear();
      this.links.addAll(page.getLinkedButtons());
      this.entityLinks.addAll(page.getLinkedEntities());
      this.itemRenders.addAll(page.getItemRenders());
      this.recipes.addAll(page.getRecipes());
      this.tabulaRenders.addAll(page.getTabulaRenders());
      this.entityRenders.addAll(page.getEntityRenders());
      this.images.addAll(page.getImages());
      this.writtenTitle = page.generateTitle();
   }

   protected void readInPageText(ResourceLocation res) {
      Resource resource = null;
      int xIndex = 0;
      int actualTextX = 0;
      int yIndex = 0;

      try {
         resource = Minecraft.getInstance().getResourceManager().getResource(res);

         try {
            List<String> readStrings = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
            this.linesFromJSON = readStrings.size();
            this.lines.clear();
            List<String> splitBySpaces = new ArrayList<>();

            for (String line : readStrings) {
               splitBySpaces.addAll(Arrays.asList(line.split(" ")));
            }

            String lineToPrint = "";
            this.linesFromPrinting = 0;
            int page = 0;

            for (int i = 0; i < splitBySpaces.size(); i++) {
               String word = splitBySpaces.get(i);
               int cutoffPoint = xIndex > 100 ? 30 : 35;
               boolean newline = word.equals("<NEWLINE>");

               for (Whitespace indexes : this.yIndexesToSkip) {
                  int indexPage = indexes.getPage();
                  if (indexPage == page) {
                     int buttonX = indexes.getX();
                     int buttonY = indexes.getY();
                     int width = indexes.getWidth();
                     int height = indexes.getHeight();
                     if (indexes.isDown()) {
                        if ((float)yIndex >= (float)buttonY / 12.0F
                           && (float)yIndex <= (float)(buttonY + height) / 12.0F
                           && (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90)) {
                           yIndex += 2;
                        }
                     } else if ((float)yIndex >= (float)(buttonY - height) / 12.0F
                        && (float)yIndex <= (float)(buttonY + height) / 12.0F
                        && (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90)) {
                        yIndex++;
                     }
                  }
               }

               boolean last = i == splitBySpaces.size() - 1;
               actualTextX += word.length() + 1;
               if (lineToPrint.length() + word.length() + 1 < cutoffPoint && !newline) {
                  lineToPrint = lineToPrint + " " + word;
                  if (last) {
                     this.linesFromPrinting++;
                     this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                     yIndex++;
                     actualTextX = 0;
                     if (newline) {
                        yIndex++;
                     }
                  }
               } else {
                  this.linesFromPrinting++;
                  if (yIndex > 13) {
                     if (xIndex > 0) {
                        page++;
                        xIndex = 0;
                        yIndex = 0;
                     } else {
                        xIndex = 200;
                        yIndex = 0;
                     }
                  }

                  if (last) {
                     lineToPrint = lineToPrint + " " + word;
                  }

                  this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                  yIndex++;
                  actualTextX = 0;
                  if (newline) {
                     yIndex++;
                  }

                  lineToPrint = word.equals("<NEWLINE>") ? "" : word;
               }
            }

            this.maxPagesFromPrinting = page;
         } catch (Exception var21) {
            var21.printStackTrace();
         }
      } catch (IOException var22) {
         Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: " + res);
      }
   }

   public void setEntityTooltip(String hoverText) {
      this.entityTooltip = hoverText;
   }
}
