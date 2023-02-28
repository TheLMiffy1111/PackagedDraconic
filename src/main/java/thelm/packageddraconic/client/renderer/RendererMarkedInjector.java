package thelm.packageddraconic.client.renderer;

import com.brandon3055.draconicevolution.api.fusioncrafting.IFusionCraftingInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import thelm.packageddraconic.tile.TileMarkedInjector;

// Code modified from RenderTileCraftingInjector
public class RendererMarkedInjector extends TileEntitySpecialRenderer<TileMarkedInjector> {

	@Override
	public void render(TileMarkedInjector te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IFusionCraftingInventory inv = te.getCraftingInventory();
		if(inv != null && inv.getCraftingStage() > 1000) {
			return;
		}
		ItemStack stack = te.getStackInPedestal();
		if(!te.getStackInPedestal().isEmpty()) {
			GlStateManager.pushMatrix();
			EnumFacing facing = te.getDirection();
			GlStateManager.translate(x+0.5+facing.getXOffset()*0.45, y+0.5+facing.getYOffset()*0.45, z+0.5+facing.getZOffset()*0.45);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			if(facing.getAxis() == EnumFacing.Axis.Y && facing == EnumFacing.DOWN) {
				GlStateManager.rotate(180, 1, 0, 0);
			}
			else {
				GlStateManager.rotate(90, facing.getZOffset(), 0, -facing.getXOffset());
			}
			GlStateManager.rotate((te.getWorld().getTotalWorldTime()+partialTicks)*0.8F, 0, -1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popMatrix();
		}
	}
}
