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
		registerBlock(BlockMarkedInjector.INSTANCE);
	}

	protected void registerItems() {
		registerItem(BlockFusionCrafter.ITEM_INSTANCE);
		registerItem(BlockMarkedInjector.ITEM_INSTANCE);
	}

	protected void registerModels() {}

	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileFusionCrafter.class, new ResourceLocation("packageddraconic:conbination_crafter"));
		GameRegistry.registerTileEntity(TileMarkedInjector.class, new ResourceLocation("packageddraconic:marked_pedestal"));
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
				50000000, 2, new Object[] {
						"netherStar", "netherStar",
						"ingotDraconiumAwakened", "ingotDraconiumAwakened",
						DEFeatures.chaoticCore, "dragonEgg",
						component, Items.ENDER_EYE,
						Items.ENDER_PEARL, Items.ENDER_PEARL,
						Items.ENDER_EYE, Items.ENDER_EYE,
		});
		RecipeManager.addFusion(RecipeManager.RecipeDifficulty.HARD,
				new ItemStack(BlockFusionCrafter.INSTANCE), new ItemStack(DEFeatures.fusionCraftingCore),
				100000000, 2, new Object[] {
						"netherStar", "netherStar",
						DEFeatures.chaoticCore, "dragonEgg",
						"ingotDraconiumAwakened", "ingotDraconiumAwakened",
						DEFeatures.chaoticCore, DEFeatures.chaosShard,
						component, Items.ENDER_EYE,
						Items.ENDER_PEARL, Items.ENDER_PEARL,
						Items.ENDER_EYE, Items.ENDER_EYE,
		});
	}
}
