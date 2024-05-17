package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.message.AnimationMessage;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import com.github.alexthe666.citadel.web.WebHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry.ChannelBuilder;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("citadel")
public class Citadel {
   public static final Logger LOGGER = LogManager.getLogger("citadel");
   public static final boolean REMAPREFS = true;
   public static final boolean DEBUG = false;
   private static final String PROTOCOL_VERSION = Integer.toString(1);
   private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation("citadel:main_channel");
   public static final SimpleChannel NETWORK_WRAPPER = ChannelBuilder.named(PACKET_NETWORK_NAME)
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions(PROTOCOL_VERSION::equals)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();
   public static ServerProxy PROXY = (ServerProxy)DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
   public static List<String> PATREONS = new ArrayList<>();
   public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, "citadel");
   public static final RegistryObject<Item> DEBUG_ITEM = ITEM_DEFERRED_REGISTER.register("debug", () -> new Item(new Properties()));
   public static final RegistryObject<Item> CITADEL_BOOK = ITEM_DEFERRED_REGISTER.register(
      "citadel_book", () -> new ItemCitadelBook(new Properties().stacksTo(1))
   );
   public static final RegistryObject<Item> EFFECT_ITEM = ITEM_DEFERRED_REGISTER.register(
      "effect_item", () -> new ItemCustomRender(new Properties().stacksTo(1))
   );
   public static final RegistryObject<Item> FANCY_ITEM = ITEM_DEFERRED_REGISTER.register("fancy_item", () -> new ItemCustomRender(new Properties().stacksTo(1)));

   public Citadel() {
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfigEvent);
      ITEM_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(PROXY);
      ModLoadingContext modLoadingContext = ModLoadingContext.get();
      modLoadingContext.registerConfig(Type.COMMON, ConfigHolder.SERVER_SPEC);
      MinecraftForge.EVENT_BUS.register(new CitadelEvents());
      MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoadFromJSON);
   }

   public static <MSG> void sendMSGToServer(MSG message) {
      NETWORK_WRAPPER.sendToServer(message);
   }

   public static <MSG> void sendMSGToAll(MSG message) {
      for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
         sendNonLocal(message, player);
      }
   }

   public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
      if (player.server.isDedicatedServer() || !player.getName().equals(player.server.getSingleplayerName())) {
         NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   private void setup(FMLCommonSetupEvent event) {
      PROXY.onPreInit();
      int packetsRegistered = 0;
      NETWORK_WRAPPER.registerMessage(
         packetsRegistered++, PropertiesMessage.class, PropertiesMessage::write, PropertiesMessage::read, PropertiesMessage.Handler::handle
      );
      NETWORK_WRAPPER.registerMessage(
         packetsRegistered++, AnimationMessage.class, AnimationMessage::write, AnimationMessage::read, AnimationMessage.Handler::handle
      );
      BufferedReader urlContents = WebHelper.getURLContents(
         "https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt"
      );
      if (urlContents != null) {
         String line;
         try {
            while ((line = urlContents.readLine()) != null) {
               PATREONS.add(line);
            }
         } catch (IOException var5) {
            LOGGER.warn("Failed to load patreon contributor perks");
         }
      } else {
         LOGGER.warn("Failed to load patreon contributor perks");
      }
   }

   @SubscribeEvent
   public void onModConfigEvent(ModConfigEvent event) {
      ModConfig config = event.getConfig();
      if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
         ServerConfig.citadelEntityTrack = (Boolean)ConfigHolder.SERVER.citadelEntityTracker.get();
         ServerConfig.chunkGenSpawnModifierVal = (Double)ConfigHolder.SERVER.chunkGenSpawnModifier.get();
      }
   }

   private void doClientStuff(FMLClientSetupEvent event) {
   }

   private void enqueueIMC(InterModEnqueueEvent event) {
   }

   private void processIMC(InterModProcessEvent event) {
   }

   @SubscribeEvent
   public void onBiomeLoadFromJSON(BiomeLoadingEvent event) {
   }

   @SubscribeEvent
   public void onDatapackReload(OnDatapackSyncEvent event) {
      VillageHouseManager.addAllHouses(event.getPlayerList().getServer().registryAccess());
   }
}
