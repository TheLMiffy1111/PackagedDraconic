package thelm.packageddraconic.event;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import thelm.packagedauto.util.ApiImpl;
import thelm.packageddraconic.block.FusionCrafterBlock;
import thelm.packageddraconic.block.MarkedInjectorBlock;
import thelm.packageddraconic.config.PackagedDraconicConfig;
import thelm.packageddraconic.container.FusionCrafterContainer;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.recipe.FusionPackageRecipeType;
import thelm.packageddraconic.tile.FusionCrafterTile;
import thelm.packageddraconic.tile.MarkedInjectorTile;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		PackagedDraconicConfig.registerConfig();
	}

	@SubscribeEvent
	public void onBlockRegister(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		registry.register(FusionCrafterBlock.INSTANCE);
		registry.register(MarkedInjectorBlock.BASIC);
		registry.register(MarkedInjectorBlock.WYVERN);
		registry.register(MarkedInjectorBlock.DRACONIC);
		registry.register(MarkedInjectorBlock.CHAOTIC);
	}

	@SubscribeEvent
	public void onItemRegister(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(FusionCrafterBlock.ITEM_INSTANCE);
		registry.register(MarkedInjectorBlock.BASIC_ITEM);
		registry.register(MarkedInjectorBlock.WYVERN_ITEM);
		registry.register(MarkedInjectorBlock.DRACONIC_ITEM);
		registry.register(MarkedInjectorBlock.CHAOTIC_ITEM);
	}

	@SubscribeEvent
	public void onTileRegister(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
		registry.register(FusionCrafterTile.TYPE_INSTANCE);
		registry.register(MarkedInjectorTile.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onContainerRegister(RegistryEvent.Register<ContainerType<?>> event) {
		IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
		registry.register(FusionCrafterContainer.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerRecipeType(FusionPackageRecipeType.INSTANCE);
		PacketHandler.registerPackets();
	}

	@SubscribeEvent
	public void onModConfig(ModConfig.ModConfigEvent event) {
		switch(event.getConfig().getType()) {
		case SERVER:
			PackagedDraconicConfig.reloadServerConfig();
			break;
		default:
			break;
		}
	}
}
