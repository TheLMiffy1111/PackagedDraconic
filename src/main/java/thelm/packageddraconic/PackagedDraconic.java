package thelm.packageddraconic;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import thelm.packageddraconic.block.FusionCrafterBlock;
import thelm.packageddraconic.client.event.ClientEventHandler;
import thelm.packageddraconic.event.CommonEventHandler;

@Mod(PackagedDraconic.MOD_ID)
public class PackagedDraconic {

	public static final String MOD_ID = "packageddraconic";
	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("packageddraconic") {
		@OnlyIn(Dist.CLIENT)
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(FusionCrafterBlock.INSTANCE);
		}
	};
	public static PackagedDraconic core;

	public PackagedDraconic() {
		core = this;
		CommonEventHandler.getInstance().onConstruct();
		DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
			ClientEventHandler.getInstance().onConstruct();
		});
	}
}
