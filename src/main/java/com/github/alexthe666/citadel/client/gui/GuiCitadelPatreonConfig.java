package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.Slider;
import net.minecraftforge.client.gui.widget.Slider.ISlider;

@OnlyIn(Dist.CLIENT)
public class GuiCitadelPatreonConfig extends OptionsSubScreen {
   private Slider distSlider;
   private Slider speedSlider;
   private Slider heightSlider;
   private Button changeButton;
   private final ISlider distSliderResponder;
   private final ISlider speedSliderResponder;
   private final ISlider heightSliderResponder;
   private float rotateDist;
   private float rotateSpeed;
   private float rotateHeight;
   private String followType = "citadel";

   public GuiCitadelPatreonConfig(Screen parentScreenIn, Options gameSettingsIn) {
      super(parentScreenIn, gameSettingsIn, new TranslatableComponent("citadel.gui.patreon_customization"));
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
      float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2.0F;
      float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1.0F;
      float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1.0F;
      this.rotateDist = roundTo(distance, 3);
      this.rotateSpeed = roundTo(speed, 3);
      this.rotateHeight = roundTo(height, 3);
      this.followType = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
      this.distSliderResponder = new ISlider() {
         public void onChangeSliderValue(Slider slider) {
            GuiCitadelPatreonConfig.this.setSliderValue(0, (float)slider.sliderValue);
         }
      };
      this.speedSliderResponder = new ISlider() {
         public void onChangeSliderValue(Slider slider) {
            GuiCitadelPatreonConfig.this.setSliderValue(1, (float)slider.sliderValue);
         }
      };
      this.heightSliderResponder = new ISlider() {
         public void onChangeSliderValue(Slider slider) {
            GuiCitadelPatreonConfig.this.setSliderValue(2, (float)slider.sliderValue);
         }
      };
   }

   private void setSliderValue(int i, float sliderValue) {
      boolean flag = false;
      CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
      if (i == 0) {
         this.rotateDist = roundTo(sliderValue * 5.0F, 3);
         tag.putFloat("CitadelRotateDistance", this.rotateDist);
         this.distSlider.dragging = false;
      } else if (i == 1) {
         this.rotateSpeed = roundTo(sliderValue * 5.0F, 3);
         tag.putFloat("CitadelRotateSpeed", this.rotateSpeed);
         this.speedSlider.dragging = false;
      } else {
         this.rotateHeight = roundTo(sliderValue * 2.0F, 3);
         tag.putFloat("CitadelRotateHeight", this.rotateHeight);
         this.heightSlider.dragging = false;
      }

      CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
      Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
   }

   public static float roundTo(float value, int places) {
      return value;
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(matrixStack);
      drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, 16777215);
      super.render(matrixStack, mouseX, mouseY, partialTicks);
   }

   protected void init() {
      super.init();
      int i = this.width / 2;
      int j = this.height / 6;
      this.addRenderableWidget(new Button(i - 100, j + 120, 200, 20, CommonComponents.GUI_DONE, p_213079_1_ -> this.minecraft.setScreen(this.lastScreen)));
      this.addRenderableWidget(
         this.distSlider = new Slider(
            i - 75 - 25,
            j + 30,
            150,
            20,
            new TranslatableComponent("citadel.gui.orbit_dist").append(new TextComponent(": ")),
            new TextComponent(""),
            0.125,
            5.0,
            (double)this.rotateDist,
            true,
            true,
            p_214132_1_ -> {
            },
            this.distSliderResponder
         ) {
         }
      );
      this.addRenderableWidget(new Button(i - 75 + 135, j + 30, 40, 20, new TranslatableComponent("citadel.gui.reset"), p_213079_1_ -> {
         this.setSliderValue(0, 0.4F);
         this.distSlider.sliderValue = 0.4F;
         this.distSlider.updateSlider();
      }));
      this.addRenderableWidget(
         this.speedSlider = new Slider(
            i - 75 - 25,
            j + 60,
            150,
            20,
            new TranslatableComponent("citadel.gui.orbit_speed").append(new TextComponent(": ")),
            new TextComponent(""),
            0.0,
            5.0,
            (double)this.rotateSpeed,
            true,
            true,
            p_214132_1_ -> {
            },
            this.speedSliderResponder
         ) {
         }
      );
      this.addRenderableWidget(new Button(i - 75 + 135, j + 60, 40, 20, new TranslatableComponent("citadel.gui.reset"), p_213079_1_ -> {
         this.setSliderValue(1, 0.2F);
         this.speedSlider.sliderValue = 0.2F;
         this.speedSlider.updateSlider();
      }));
      this.addRenderableWidget(
         this.heightSlider = new Slider(
            i - 75 - 25,
            j + 90,
            150,
            20,
            new TranslatableComponent("citadel.gui.orbit_height").append(new TextComponent(": ")),
            new TextComponent(""),
            0.0,
            2.0,
            (double)this.rotateHeight,
            true,
            true,
            p_214132_1_ -> {
            },
            this.heightSliderResponder
         ) {
         }
      );
      this.addRenderableWidget(new Button(i - 75 + 135, j + 90, 40, 20, new TranslatableComponent("citadel.gui.reset"), p_213079_1_ -> {
         this.setSliderValue(2, 0.5F);
         this.heightSlider.sliderValue = 0.5;
         this.heightSlider.updateSlider();
      }));
      this.distSlider.precision = 1;
      this.heightSlider.precision = 2;
      this.speedSlider.precision = 2;
      this.distSlider.updateSlider();
      this.heightSlider.updateSlider();
      this.speedSlider.updateSlider();
      this.addRenderableWidget(this.changeButton = new Button(i - 100, j, 200, 20, this.getTypeText(), p_213079_1_ -> {
         this.followType = CitadelPatreonRenderer.getIdOfNext(this.followType);
         CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
         if (tag != null) {
            tag.putString("CitadelFollowerType", this.followType);
            CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
         }

         Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", tag, Minecraft.getInstance().player.getId()));
         this.changeButton.setMessage(this.getTypeText());
      }));
   }

   private Component getTypeText() {
      return new TranslatableComponent("citadel.gui.follower_type").append(new TranslatableComponent("citadel.follower." + this.followType));
   }
}
