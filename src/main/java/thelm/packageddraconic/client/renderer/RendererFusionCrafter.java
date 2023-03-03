package thelm.packageddraconic.client.renderer;

import org.lwjgl.opengl.GL11;

import com.brandon3055.draconicevolution.client.render.effect.EffectTrackerFusionCrafting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import thelm.packagedauto.client.RenderTimer;
import thelm.packageddraconic.tile.TileFusionCrafter;

// Code modified from RenderTileFusionCraftingCore
public class RendererFusionCrafter extends TileEntitySpecialRenderer<TileFusionCrafter> {

	public static final ResourceLocation PARTICLE = new ResourceLocation("draconicevolution:textures/blocks/fusion_crafting/fusion_particle.png");

	@Override
	public void render(TileFusionCrafter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(MinecraftForgeClient.getRenderPass() == 0) {
			ItemStack stack = !te.getStackInCore(1).isEmpty() ? te.getStackInCore(1) : te.getStackInCore(0);
			if(!stack.isEmpty()) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(x+0.5, y+0.5, z+0.5);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				GlStateManager.rotate((RenderTimer.INSTANCE.getTicks()+partialTicks)*0.8F, 0F, -1F, 0F);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
				GlStateManager.popMatrix();
			}
		}
		else {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			if(player != null && te.effects != null) {
				EffectTrackerFusionCrafting.interpPosX = player.lastTickPosX+(player.posX-player.lastTickPosX)*partialTicks;
				EffectTrackerFusionCrafting.interpPosY = player.lastTickPosY+(player.posY-player.lastTickPosY)*partialTicks;
				EffectTrackerFusionCrafting.interpPosZ = player.lastTickPosZ+(player.posZ-player.lastTickPosZ)*partialTicks;
				Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE);
				Tessellator tessellator = Tessellator.getInstance();
				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				GlStateManager.depthMask(true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
				for(EffectTrackerFusionCrafting effect : te.effects) {
					effect.renderEffect(tessellator, partialTicks);
				}
				GlStateManager.disableBlend();
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}
	}
}
