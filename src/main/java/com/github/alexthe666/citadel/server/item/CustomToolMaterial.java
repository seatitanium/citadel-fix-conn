package com.github.alexthe666.citadel.server.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class CustomToolMaterial implements Tier {
   private String name;
   private int harvestLevel;
   private int durability;
   private float damage;
   private float speed;
   private int enchantability;
   private Ingredient ingredient = null;

   public CustomToolMaterial(String name, int harvestLevel, int durability, float damage, float speed, int enchantability) {
      this.name = name;
      this.harvestLevel = harvestLevel;
      this.durability = durability;
      this.damage = damage;
      this.speed = speed;
      this.enchantability = enchantability;
   }

   public String getName() {
      return this.name;
   }

   public int getUses() {
      return this.durability;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   public int getLevel() {
      return this.harvestLevel;
   }

   public int getEnchantmentValue() {
      return this.enchantability;
   }

   public Ingredient getRepairIngredient() {
      return this.ingredient == null ? Ingredient.EMPTY : this.ingredient;
   }

   public void setRepairMaterial(Ingredient ingredient) {
      this.ingredient = ingredient;
   }
}
