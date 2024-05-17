package com.github.alexthe666.citadel.client.model.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VanillaTabulaModel implements UnbakedModel {
   private final TabulaModelContainer model;
   private final Material particle;
   private final Collection<Material> textures;
   private final ImmutableMap<TransformType, Transformation> transforms;

   public VanillaTabulaModel(
      TabulaModelContainer model, Material particle, ImmutableList<Material> textures, ImmutableMap<TransformType, Transformation> transforms
   ) {
      this.model = model;
      this.particle = particle;
      this.textures = textures;
      this.transforms = transforms;
   }

   public Collection<ResourceLocation> getDependencies() {
      return ImmutableList.of();
   }

   public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      return this.textures;
   }

   @Nullable
   public BakedModel bake(
      ModelBakery modelBakery, Function<Material, TextureAtlasSprite> function, ModelState iModelTransform, ResourceLocation resourceLocation
   ) {
      return null;
   }
}
