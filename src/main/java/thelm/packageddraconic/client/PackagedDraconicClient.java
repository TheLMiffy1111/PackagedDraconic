package thelm.packageddraconic.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import thelm.packageddraconic.PackagedDraconic;
import thelm.packageddraconic.client.event.ClientEventHandler;

@Mod(value = PackagedDraconic.MOD_ID, dist = Dist.CLIENT)
public class PackagedDraconicClient {

	public PackagedDraconicClient(IEventBus modEventBus) {
		ClientEventHandler.getInstance().onConstruct(modEventBus);
	}
}
