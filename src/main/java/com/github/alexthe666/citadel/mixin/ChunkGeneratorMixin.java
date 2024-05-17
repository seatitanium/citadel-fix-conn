package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.server.generation.GenerationSettingsManager;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChunkGenerator.class})
public class ChunkGeneratorMixin {
   @Shadow
   @Final
   protected BiomeSource biomeSource;

   @Inject(
      at = {@At(
         value = "INVOKE",
         shift = Shift.BEFORE,
         target = "Lnet/minecraft/world/level/levelgen/WorldgenRandom;setFeatureSeed(JII)V"
      )},
      remap = true,
      method = {"Lnet/minecraft/world/level/chunk/ChunkGenerator;applyBiomeDecoration(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/StructureFeatureManager;)V"}
   )
   private void citadel_applyBiomeDecoration(WorldGenLevel level, ChunkAccess access, StructureFeatureManager featureManager, CallbackInfo ci) {
      GenerationSettingsManager.onChunkPopulate((ChunkGenerator) ((BiomeManager.NoiseBiomeSource) this), level, access, featureManager, this.biomeSource);
   }
}
