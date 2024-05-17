package com.github.alexthe666.citadel.server.generation;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public class SurfaceRulesManager {
   private static final List<RuleSource> OVERWORLD_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> NETHER_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> END_REGISTRY = new ArrayList<>();
   private static final List<RuleSource> CAVE_REGISTRY = new ArrayList<>();
   private static RuleSource overworldRules;
   private static RuleSource netherRules;
   private static RuleSource endRules;
   private static RuleSource caveRules;
   private static boolean mergedOverworld;
   private static boolean mergedNether;
   private static boolean mergedEnd;
   private static boolean mergedCaves;

   public static void registerOverworldSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerOverworldSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerOverworldSurfaceRule(RuleSource rule) {
      OVERWORLD_REGISTRY.add(rule);
   }

   public static void registerNetherSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerNetherSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerNetherSurfaceRule(RuleSource rule) {
      NETHER_REGISTRY.add(rule);
   }

   public static void registerEndSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerEndSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerEndSurfaceRule(RuleSource rule) {
      END_REGISTRY.add(rule);
   }

   public static void registerCaveSurfaceRule(ConditionSource condition, RuleSource rule) {
      registerCaveSurfaceRule(SurfaceRules.ifTrue(condition, rule));
   }

   public static void registerCaveSurfaceRule(RuleSource rule) {
      CAVE_REGISTRY.add(rule);
   }

   public static RuleSource process(NoiseGeneratorSettings settings, RuleSource prev) {
      return prev;
   }
}
