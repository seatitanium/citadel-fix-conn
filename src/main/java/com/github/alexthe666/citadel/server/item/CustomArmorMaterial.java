package com.github.alexthe666.citadel.server.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class CustomArmorMaterial implements ArmorMaterial {
   private String name;
   private int durability;
   private int[] damageReduction;
   private int encantability;
   private SoundEvent sound;
   private float toughness;
   private Ingredient ingredient = null;
   public float knockbackResistance = 0.0F;

   public CustomArmorMaterial(
      String name, int durability, int[] damageReduction, int encantability, SoundEvent sound, float toughness, float knockbackResistance
   ) {
      this.name = name;
      this.durability = durability;
      this.damageReduction = damageReduction;
      this.encantability = encantability;
      this.sound = sound;
      this.toughness = toughness;
      this.knockbackResistance = knockbackResistance;
   }

   public int getDurabilityForSlot(EquipmentSlot slotIn) {
      return this.durability;
   }

   public int getDefenseForSlot(EquipmentSlot slotIn) {
      return this.damageReduction[slotIn.getIndex()];
   }

   public int getEnchantmentValue() {
      return this.encantability;
   }

   public SoundEvent getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return this.ingredient == null ? Ingredient.EMPTY : this.ingredient;
   }

   public void setRepairMaterial(Ingredient ingredient) {
      this.ingredient = ingredient;
   }

   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }

   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }
}
