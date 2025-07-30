package thelm.packageddraconic;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import thelm.packageddraconic.event.CommonEventHandler;

@Mod(PackagedDraconic.MOD_ID)
public class PackagedDraconic {

	public static final String MOD_ID = "packageddraconic";

	public PackagedDraconic(IEventBus modEventBus, ModContainer modContainer) {
		CommonEventHandler.getInstance().onConstruct(modEventBus, modContainer);
	}
}
