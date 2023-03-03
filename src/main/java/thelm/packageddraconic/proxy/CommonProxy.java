package thelm.packageddraconic.proxy;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.lib.RecipeManager;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thelm.packagedauto.api.RecipeTypeRegistry;
import thelm.packagedauto.item.ItemMisc;
import thelm.packageddraconic.block.BlockFusionCrafter;
import thelm.packageddraconic.block.BlockMarkedInjector;
import thelm.packageddraconic.config.PackagedDraconicConfig;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.recipe.RecipeTypeFusion;
import thelm.packageddraconic.tile.TileFusionCrafter;
import thelm.packageddraconic.tile.TileMarkedInjector;

public class CommonProxy {

	public void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
	}

	public void registerItem(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	public void register(FMLPreInitializationEvent event) {
		registerConfig(event);
		registerBlocks();
		registerItems();
		registerModels();
		registerTileEntities();
		registerRecipeTypes();
		registerNetwork();
	}

	public void register(FMLInitializationEvent event) {
		registerRecipes();
	}

	protected void registerConfig(FMLPreInitializationEvent event) {
		PackagedDraconicConfig.init(event.getSuggestedConfigurationFile());
	}

	protected void registerBlocks() {
		registerBlock(BlockFusionCrafter.INSTANCE);
		registerBlock(BlockMarkedInjector.BASIC);
		registerBlock(BlockMarkedInjector.WYVERN);
		registerBlock(BlockMarkedInjector.DRACONIC);
		registerBlock(BlockMarkedInjector.CHAOTIC);
	}

	protected void registerItems() {
		registerItem(BlockFusionCrafter.ITEM_INSTANCE);
		registerItem(BlockMarkedInjector.BASIC_ITEM);
		registerItem(BlockMarkedInjector.WYVERN_ITEM);
		registerItem(BlockMarkedInjector.DRACONIC_ITEM);
		registerItem(BlockMarkedInjector.CHAOTIC_ITEM);
	}

	protected void registerModels() {}

	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileFusionCrafter.class, new ResourceLocation("packageddraconic:conbination_crafter"));
		GameRegistry.registerTileEntity(TileMarkedInjector.class, new ResourceLocation("packageddraconic:marked_injector"));
	}

	protected void registerRecipeTypes() {
		RecipeTypeRegistry.registerRecipeType(RecipeTypeFusion.INSTANCE);
	}

	protected void registerNetwork() {
		PacketHandler.registerPackets();
	}

	protected void registerRecipes() {
		Item component = Loader.isModLoaded("appliedenergistics2") ? ItemMisc.ME_PACKAGE_COMPONENT : ItemMisc.PACKAGE_COMPONENT;
		RecipeManager.addFusion(RecipeManager.RecipeDifficulty.NORMAL,
				new ItemStack(BlockFusionCrafter.INSTANCE), new ItemStack(DEFeatures.fusionCraftingCore),
				50000000, 1, new Object[] {
						"ingotDraconiumAwakened", "ingotDraconiumAwakened",
						DEFeatures.awakenedCore, "dragonEgg",
						component, "netherStar",
						Items.ENDER_PEARL, Items.ENDER_PEARL,
						Items.ENDER_EYE, Items.ENDER_EYE,
		});
		RecipeManager.addFusion(RecipeManager.RecipeDifficulty.HARD,
				new ItemStack(BlockFusionCrafter.INSTANCE), new ItemStack(DEFeatures.fusionCraftingCore),
				100000000, 1, new Object[] {
						"ingotDraconiumAwakened", "ingotDraconiumAwakened",
						DEFeatures.awakenedCore, "dragonEgg",
						DEFeatures.awakenedCore, DEFeatures.chaosShard,
						component, "netherStar",
						Items.ENDER_PEARL, Items.ENDER_PEARL,
						Items.ENDER_EYE, Items.ENDER_EYE,
		});
	}
}
