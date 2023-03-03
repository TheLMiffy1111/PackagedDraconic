package thelm.packageddraconic;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packageddraconic.block.BlockFusionCrafter;
import thelm.packageddraconic.proxy.CommonProxy;

@Mod(
		modid = PackagedDraconic.MOD_ID,
		name = PackagedDraconic.NAME,
		version = PackagedDraconic.VERSION,
		dependencies = PackagedDraconic.DEPENDENCIES,
		guiFactory = PackagedDraconic.GUI_FACTORY
		)
public class PackagedDraconic {

	public static final String MOD_ID = "packageddraconic";
	public static final String NAME = "PackagedDraconic";
	public static final String VERSION = "1.12.2-1.0.0.0";
	public static final String DEPENDENCIES = "required-after:packagedauto@[1.12.2-1.0.6.27,);required-after:draconicevolution;";
	public static final String GUI_FACTORY = "thelm.packageddraconic.client.gui.GuiPackagedDraconicConfigFactory";
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs("packageddraconic") {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlockFusionCrafter.INSTANCE);
		}
	};
	@Instance
	public static PackagedDraconic instance;
	@SidedProxy(clientSide = "thelm.packageddraconic.proxy.ClientProxy", serverSide = "thelm.packageddraconic.proxy.CommonProxy", modId = PackagedDraconic.MOD_ID)
	public static CommonProxy proxy;
	public static ModMetadata metadata;

	@EventHandler
	public void firstMovement(FMLPreInitializationEvent event) {
		metadata = event.getModMetadata();
		metadata.autogenerated = false;
		metadata.version = VERSION;
		metadata.authorList.add("TheLMiffy1111");
		metadata.description = "A PackagedAuto addon that adds Draconic Evolution fusion autocrafting.";

		proxy.register(event);
	}

	@EventHandler
	public void secondMovement(FMLInitializationEvent event) {
		proxy.register(event);
	}
}
