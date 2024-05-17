package com.github.alexthe666.citadel.client.model.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BakedTabulaModel implements BakedModel {
   private final ImmutableList<BakedQuad> quads;
   private final TextureAtlasSprite particle;
   private final ImmutableMap<TransformType, Transformation> transforms;

   public BakedTabulaModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, ImmutableMap<TransformType, Transformation> transforms) {
      this.quads = quads;
      this.particle = particle;
      this.transforms = transforms;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return this.quads;
   }

   public boolean useAmbientOcclusion() {
      return true;
   }

   public boolean isGui3d() {
      return false;
   }

   public boolean usesBlockLight() {
      return false;
   }

   public boolean isCustomRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleIcon() {
      return this.particle;
   }

   public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
   }

   public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
   }
}
