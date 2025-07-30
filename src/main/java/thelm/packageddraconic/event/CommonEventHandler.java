package thelm.packageddraconic.event;

import com.brandon3055.brandonscore.capability.CapabilityOP;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedauto.util.ApiImpl;
import thelm.packagedauto.util.MiscHelper;
import thelm.packageddraconic.block.PackagedDraconicBlocks;
import thelm.packageddraconic.block.entity.PackagedDraconicBlockEntities;
import thelm.packageddraconic.config.PackagedDraconicConfig;
import thelm.packageddraconic.creativetab.PackagedDraconicCreativeTabs;
import thelm.packageddraconic.integration.appeng.AppEngEventHandler;
import thelm.packageddraconic.item.PackagedDraconicItems;
import thelm.packageddraconic.menu.PackagedDraconicMenus;
import thelm.packageddraconic.packet.FinishCraftEffectsPacket;
import thelm.packageddraconic.packet.SyncCrafterPacket;
import thelm.packageddraconic.packet.SyncInjectorPacket;
import thelm.packageddraconic.recipe.FusionPackageRecipeType;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.register(this);
		MiscHelper.INSTANCE.conditionalRunnable(()->ModList.get().isLoaded("ae2"), ()->()->{
			modEventBus.register(AppEngEventHandler.getInstance());
		}, ()->()->{}).run();
		PackagedDraconicConfig.registerConfig(modContainer);

		PackagedDraconicBlocks.BLOCKS.register(modEventBus);
		PackagedDraconicItems.ITEMS.register(modEventBus);
		PackagedDraconicBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		PackagedDraconicMenus.MENUS.register(modEventBus);
		PackagedDraconicCreativeTabs.CREATIVE_TABS.register(modEventBus);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerRecipeType(FusionPackageRecipeType.INSTANCE);
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PackagedDraconicBlockEntities.FUSION_CRAFTER.get(), BaseBlockEntity::getItemHandler);

		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PackagedDraconicBlockEntities.FUSION_CRAFTER.get(), BaseBlockEntity::getEnergyStorage);
		event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, PackagedDraconicBlockEntities.MARKED_INJECTOR.get(), (be, dir)->be.opStorage);

		event.registerBlockEntity(CapabilityOP.BLOCK, PackagedDraconicBlockEntities.MARKED_INJECTOR.get(), (be, dir)->be.opStorage);
	}

	@SubscribeEvent
	public void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("packageddraconic");
		registrar.playToClient(SyncInjectorPacket.TYPE, SyncInjectorPacket.STREAM_CODEC, SyncInjectorPacket::handle);
		registrar.playToClient(SyncCrafterPacket.TYPE, SyncCrafterPacket.STREAM_CODEC, SyncCrafterPacket::handle);
		registrar.playToClient(FinishCraftEffectsPacket.TYPE, FinishCraftEffectsPacket.STREAM_CODEC, FinishCraftEffectsPacket::handle);
	}

	@SubscribeEvent
	public void onModConfigLoading(ModConfigEvent.Loading event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedDraconicConfig.reloadServerConfig();
		default -> {}
		}
	}

	@SubscribeEvent
	public void onModConfigReloading(ModConfigEvent.Reloading event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedDraconicConfig.reloadServerConfig();
		default -> {}
		}
	}
}
