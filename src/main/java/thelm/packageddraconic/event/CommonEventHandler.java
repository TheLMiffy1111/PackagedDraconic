package thelm.packageddraconic.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import thelm.packagedauto.util.ApiImpl;
import thelm.packageddraconic.block.FusionCrafterBlock;
import thelm.packageddraconic.block.MarkedInjectorBlock;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;
import thelm.packageddraconic.config.PackagedDraconicConfig;
import thelm.packageddraconic.menu.FusionCrafterMenu;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.recipe.FusionPackageRecipeType;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(this);
		PackagedDraconicConfig.registerConfig();

		DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, "packageddraconic");
		blockRegister.register(modEventBus);
		blockRegister.register("fusion_crafter", ()->FusionCrafterBlock.INSTANCE);
		blockRegister.register("marked_draconium_injector", ()->MarkedInjectorBlock.BASIC);
		blockRegister.register("marked_wyvern_injector", ()->MarkedInjectorBlock.WYVERN);
		blockRegister.register("marked_draconic_injector", ()->MarkedInjectorBlock.DRACONIC);
		blockRegister.register("marked_chaotic_injector", ()->MarkedInjectorBlock.CHAOTIC);

		DeferredRegister<Item> itemRegister = DeferredRegister.create(Registries.ITEM, "packageddraconic");
		itemRegister.register(modEventBus);
		itemRegister.register("fusion_crafter", ()->FusionCrafterBlock.ITEM_INSTANCE);
		itemRegister.register("marked_draconium_injector", ()->MarkedInjectorBlock.BASIC_ITEM);
		itemRegister.register("marked_wyvern_injector", ()->MarkedInjectorBlock.WYVERN_ITEM);
		itemRegister.register("marked_draconic_injector", ()->MarkedInjectorBlock.DRACONIC_ITEM);
		itemRegister.register("marked_chaotic_injector", ()->MarkedInjectorBlock.CHAOTIC_ITEM);

		DeferredRegister<BlockEntityType<?>> blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "packageddraconic");
		blockEntityRegister.register(modEventBus);
		blockEntityRegister.register("fusion_crafter", ()->FusionCrafterBlockEntity.TYPE_INSTANCE);
		blockEntityRegister.register("marked_injector", ()->MarkedInjectorBlockEntity.TYPE_INSTANCE);

		DeferredRegister<MenuType<?>> menuRegister = DeferredRegister.create(Registries.MENU, "packageddraconic");
		menuRegister.register(modEventBus);
		menuRegister.register("fusion_crafter", ()->FusionCrafterMenu.TYPE_INSTANCE);

		DeferredRegister<CreativeModeTab> creativeTabRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "packageddraconic");
		creativeTabRegister.register(modEventBus);
		creativeTabRegister.register("tab",
				()->CreativeModeTab.builder().
				title(Component.translatable("itemGroup.packageddraconic")).
				icon(()->new ItemStack(FusionCrafterBlock.ITEM_INSTANCE)).
				displayItems((parameters, output)->{
					output.accept(FusionCrafterBlock.ITEM_INSTANCE);
					output.accept(MarkedInjectorBlock.BASIC_ITEM);
					output.accept(MarkedInjectorBlock.WYVERN_ITEM);
					output.accept(MarkedInjectorBlock.DRACONIC_ITEM);
					output.accept(MarkedInjectorBlock.CHAOTIC_ITEM);
				}).
				build());
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerRecipeType(FusionPackageRecipeType.INSTANCE);
		PacketHandler.registerPackets();
	}

	@SubscribeEvent
	public void onModConfig(ModConfigEvent event) {
		switch(event.getConfig().getType()) {
		case SERVER -> PackagedDraconicConfig.reloadServerConfig();
		default -> {}
		}
	}
}
