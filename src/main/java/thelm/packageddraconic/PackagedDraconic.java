package thelm.packageddraconic;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
	public static final ItemGroup ITEM_GROUP = new ItemGroup("packageddraconic") {
		@OnlyIn(Dist.CLIENT)
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(FusionCrafterBlock.INSTANCE);
		}
	};

	public PackagedDraconic() {
		CommonEventHandler.getInstance().onConstruct();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()->{
			ClientEventHandler.getInstance().onConstruct();
		});
	}
}
