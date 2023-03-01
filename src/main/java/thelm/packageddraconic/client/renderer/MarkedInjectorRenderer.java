package thelm.packageddraconic.client.renderer;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import thelm.packageddraconic.tile.MarkedInjectorTile;

// Code modified from RenderTileCraftingInjector
public class MarkedInjectorRenderer extends TileEntityRenderer<MarkedInjectorTile> {

	public MarkedInjectorRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(MarkedInjectorTile te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if(te.getCrafter() != null && te.getCrafter().isWorking && te.getCrafter().getFusionState().ordinal() > 1) {
			return;
		}
		if(!te.getItemHandler().getStackInSlot(0).isEmpty()) {
			BlockState state = te.getLevel().getBlockState(te.getBlockPos());
			if(state.isAir()) {
				return;
			}
			Direction facing = state.getValue(DirectionalBlock.FACING);
			matrixStack.translate(0.5+(facing.getStepX()*0.45), 0.5+(facing.getStepY()*0.45), 0.5+(facing.getStepZ()*0.45));
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			if(facing.getAxis() == Direction.Axis.Y) {
				if(facing == Direction.DOWN) {
					matrixStack.mulPose(new Quaternion(180, 0, 0, true));
				}
			}
			else {
				matrixStack.mulPose(new Quaternion(facing.getStepZ() * 90, 0, facing.getStepX() * -90, true));
			}
			matrixStack.mulPose(new Quaternion(0, (TimeKeeper.getClientTick()+partialTicks) * -0.8F, 0, true));
			ItemStack stack = te.getItemHandler().getStackInSlot(0);
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
		}
	}
}
