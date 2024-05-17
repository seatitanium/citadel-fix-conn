package com.github.alexthe666.citadel.server;

import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CitadelEvents {
   private int updateTimer;

   @SubscribeEvent
   public void onEntityUpdateDebug(LivingUpdateEvent event) {
   }

   @SubscribeEvent
   public void onLoadBiome(BiomeLoadingEvent event) {
      float probability = (float)ServerConfig.chunkGenSpawnModifierVal * event.getSpawns().getProbability();
      event.getSpawns().creatureGenerationProbability(probability);
   }

   @SubscribeEvent
   public void onPlayerClone(Clone event) {
      if (event.getOriginal() != null && CitadelEntityData.getCitadelTag(event.getOriginal()) != null) {
         CitadelEntityData.setCitadelTag(event.getEntityLiving(), CitadelEntityData.getCitadelTag(event.getOriginal()));
      }
   }
}
