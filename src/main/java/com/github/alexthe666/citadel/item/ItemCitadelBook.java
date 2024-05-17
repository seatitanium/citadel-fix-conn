package com.github.alexthe666.citadel.item;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ItemCitadelBook extends Item {
   public ItemCitadelBook(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemStackIn = playerIn.getItemInHand(handIn);
      if (worldIn.isClientSide) {
         Citadel.PROXY.openBookGUI(itemStackIn);
      }

      return new InteractionResultHolder(InteractionResult.PASS, itemStackIn);
   }
}
