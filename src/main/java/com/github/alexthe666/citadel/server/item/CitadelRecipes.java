package com.github.alexthe666.citadel.server.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.crafting.UpgradeRecipe;

public class CitadelRecipes {
   private static List<UpgradeRecipe> smithingRecipes = new ArrayList<>();

   public static void registerSmithingRecipe(UpgradeRecipe recipe) {
      smithingRecipes.add(recipe);
   }

   public static List<UpgradeRecipe> getSmithingRecipes() {
      return smithingRecipes;
   }
}
