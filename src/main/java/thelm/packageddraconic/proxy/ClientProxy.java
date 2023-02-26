package thelm.packageddraconic.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import thelm.packagedauto.client.IModelRegister;
import thelm.packageddraconic.client.renderer.RendererFusionCrafter;
import thelm.packageddraconic.client.renderer.RendererMarkedInjector;
import thelm.packageddraconic.tile.TileFusionCrafter;
import thelm.packageddraconic.tile.TileMarkedInjector;

public class ClientProxy extends CommonProxy {

	private static List<IModelRegister> modelRegisterList = new ArrayList<>();

	@Override
	public void registerBlock(Block block) {
		super.registerBlock(block);
		if(block instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)block);
		}
	}

	@Override
	public void registerItem(Item item) {
		super.registerItem(item);
		if(item instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)item);
		}
	}

	@Override
	protected void registerModels() {
		for(IModelRegister model : modelRegisterList) {
			model.registerModels();
		}
	}

	@Override
	protected void registerTileEntities() {
		super.registerTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileFusionCrafter.class, new RendererFusionCrafter());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMarkedInjector.class, new RendererMarkedInjector());
	}
}
