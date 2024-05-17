package com.github.alexthe666.citadel.config.biome;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraftforge.common.BiomeDictionary;

public class SpawnBiomeData {
   private List<List<SpawnBiomeData.SpawnBiomeEntry>> biomes = new ArrayList<>();

   public SpawnBiomeData() {
   }

   private SpawnBiomeData(SpawnBiomeData.SpawnBiomeEntry[][] biomesRead) {
      this.biomes = new ArrayList<>();

      for (SpawnBiomeData.SpawnBiomeEntry[] innerArray : biomesRead) {
         this.biomes.add(Arrays.asList(innerArray));
      }
   }

   public SpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
      if (this.biomes.isEmpty() || this.biomes.size() < pool + 1) {
         this.biomes.add(new ArrayList<>());
      }

      this.biomes.get(pool).add(new SpawnBiomeData.SpawnBiomeEntry(type, negate, value));
      return this;
   }

   @Deprecated
   public boolean matches(Biome biomeIn) {
      return this.matches(BiomeCategory.NONE, biomeIn.getRegistryName());
   }

   public boolean matches(BiomeCategory category, ResourceLocation registryName) {
      for (List<SpawnBiomeData.SpawnBiomeEntry> all : this.biomes) {
         boolean overall = true;

         for (SpawnBiomeData.SpawnBiomeEntry cond : all) {
            if (!cond.matches(category, registryName)) {
               overall = false;
            }
         }

         if (overall) {
            return true;
         }
      }

      return false;
   }

   public static class Deserializer implements JsonDeserializer<SpawnBiomeData>, JsonSerializer<SpawnBiomeData> {
      public SpawnBiomeData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonobject = json.getAsJsonObject();
         SpawnBiomeData.SpawnBiomeEntry[][] biomesRead = (SpawnBiomeData.SpawnBiomeEntry[][])GsonHelper.getAsObject(
            jsonobject, "biomes", new SpawnBiomeData.SpawnBiomeEntry[0][0], context, SpawnBiomeData.SpawnBiomeEntry[][].class
         );
         return new SpawnBiomeData(biomesRead);
      }

      public JsonElement serialize(SpawnBiomeData src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("biomes", context.serialize(src.biomes));
         return jsonobject;
      }
   }

   private class SpawnBiomeEntry {
      BiomeEntryType type;
      boolean negate;
      String value;

      public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
         this.type = type;
         this.negate = remove;
         this.value = value;
      }

      public boolean matches(BiomeCategory category, ResourceLocation registryName) {
         if (this.type == BiomeEntryType.BIOME_DICT) {
            ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, registryName);
            List<? extends String> biomeTypes = BiomeDictionary.getTypes(biomeKey)
               .stream()
               .map(t -> t.toString().toLowerCase(Locale.ROOT))
               .collect(Collectors.toList());
            return biomeTypes.contains(this.value) ? !this.negate : this.negate;
         } else if (this.type == BiomeEntryType.BIOME_CATEGORY) {
            return category.getName().toLowerCase(Locale.ROOT).equals(this.value) ? !this.negate : this.negate;
         } else {
            return registryName.toString().equals(this.value) ? !this.negate : this.negate;
         }
      }
   }
}
