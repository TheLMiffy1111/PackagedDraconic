package thelm.packageddraconic.tile;

import com.brandon3055.draconicevolution.api.fusioncrafting.ICraftingInjector;

public interface IMarkedInjector extends ICraftingInjector {

	void spawnItem();

	@Override
	default int getPedestalTier() {
		return 3;
	}
}
