package thelm.packageddraconic.client.renderer;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.client.DETextures;
import com.brandon3055.draconicevolution.client.render.EffectLib;
import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import codechicken.lib.render.buffer.TransformingVertexBuilder;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import thelm.packageddraconic.client.fx.FusionCrafterFXHandler;
import thelm.packageddraconic.tile.FusionCrafterTile;

// Code modified from RenderTileFusionCraftingCore
public class FusionCrafterRenderer extends TileEntityRenderer<FusionCrafterTile> {

	private static Random rand = new Random();

	private RenderType particleType = RenderType.create("particle_type", DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, GL11.GL_QUADS, 256,
			RenderType.State.builder().
			setTextureState(new RenderState.TextureState(AtlasTexture.LOCATION_BLOCKS, false, false)).
			setAlphaState(new RenderState.AlphaState(0.004F)).
			setWriteMaskState(new RenderState.WriteMaskState(true, true)).
			setTexturingState(new RenderState.TexturingState("lighting", RenderSystem::disableLighting, Runnables.doNothing())).
			createCompositeState(false));

	public FusionCrafterRenderer(TileEntityRendererDispatcher rendererDispatcher) {
		super(rendererDispatcher);
	}

	@Override
	public void render(FusionCrafterTile te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		renderContent(te, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		FusionCrafterFXHandler handler = (FusionCrafterFXHandler)te.fxHandler;
		if(handler.renderActive()) {
			renderEffects(te, handler, partialTicks, matrixStack, buffer, combinedLight, combinedOverlay);
		}
	}

	private void renderContent(FusionCrafterTile te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int packetOverlay) {
		ItemStack stack = !te.getOutputStack().isEmpty() && !te.isWorking ? te.getOutputStack() : te.getCatalystStack();
		Minecraft mc = Minecraft.getInstance();
		if(!stack.isEmpty()) {
			matrixStack.pushPose();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.mulPose(new Quaternion(0, (TimeKeeper.getClientTick()+partialTicks)*0.8F, 0, true));
			mc.getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, packetOverlay, matrixStack, buffer);
			matrixStack.popPose();
		}
	}

	private void renderEffects(FusionCrafterTile te, FusionCrafterFXHandler handler, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer getter, int combinedLight, int packetOverlay) {
		Minecraft mc = Minecraft.getInstance();
		ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
		matrixStack.translate(0.5, 0.5, 0.5);
		ParticleStatus pStatus = mc.options.particles;
		double particleSetting = pStatus == ParticleStatus.ALL ? 1 : pStatus == ParticleStatus.DECREASED ? 2/3D : 1/3D;
		// Total particle allocation for ingredient effects
		int maxParticles = (int)(1000*particleSetting);
		List<FusionCrafterFXHandler.IngredFX> ingredFXList = handler.getIngredients(partialTicks);
		int i = 0;
		for(FusionCrafterFXHandler.IngredFX ingred : ingredFXList) {
			renderIngredientEffect(renderInfo, matrixStack, getter, partialTicks, i++, ingred, maxParticles/ingredFXList.size());
			if(ingred.arcPos != null) {
				EffectLib.renderLightningP2PRotate(matrixStack, getter, ingred.pos, ingred.arcPos, 8, TimeKeeper.getClientTick()/2, 0.06F, 0.04F, false, 0, 0x6300BD);
			}
		}
		Rotation cameraRotation = new Rotation(new Quat(renderInfo.rotation()));
		IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(particleType), matrixStack);
		if(handler.injectTime > 0) {
			rand.setSeed(3055);
			double anim = handler.getRotationAnim(partialTicks);
			int chargePCount = 64;
			float pScale = 0.125F/2;
			for(i = 0; i < chargePCount; i++) {
				anim += rand.nextGaussian();
				float scale = MathHelper.clamp(handler.injectTime*chargePCount - i, 0F, 1F)*pScale*(0.7F+(rand.nextFloat()*0.3F));
				if(scale <= 0) {
					break;
				}
				float rotX = (float)(rand.nextFloat()*Math.PI*2+anim/10);
				float rotY = (float)(rand.nextFloat()*Math.PI*2+anim/15);
				double radius = 0.35*MathUtils.clampMap(te.animProgress, 0.95F, 1F, 1F, 0F);
				double x = radius*MathHelper.cos(rotX)*MathHelper.sin(rotY);
				double y = radius*MathHelper.sin(rotX)*MathHelper.sin(rotY);
				double z = radius*MathHelper.cos(rotY);
				EffectLib.drawParticle(cameraRotation, builder, getTexture(DETextures.MIXED_PARTICLE), 1F, 0, 0, x, y, z, scale, 240);
			}
		}
		//Outer Loopy Effects
		if(handler.chargeState > 0) {
			for(i = 0; i < 4; i++) {
				float loopOffset = ((i/4F)*(float)Math.PI*2)+(TimeKeeper.getClientTick()/100F);
				for(int j = 0; j < 8; j++) {
					float rot = ((j/64F)*(float)Math.PI*2)+(TimeKeeper.getClientTick()/10F)+loopOffset;
					if(j > handler.chargeState*8) {
						continue;
					}
					double x = MathHelper.sin(rot)*2;
					double z = MathHelper.cos(rot)*2;
					double y = MathHelper.cos(rot+loopOffset)*1;
					float scale = 0.1F*(j/8F);
					EffectLib.drawParticle(cameraRotation, builder, getTexture(DETextures.ENERGY_PARTICLE, j), 106/255F, 13/255F, 173/255F, x, y, z, scale, 240);
				}
			}
			if(handler.injectTime > 0 && TimeKeeper.getClientTick() % 5 == 0) {
				int pos = rand.nextInt(4);
				for(i = 0; i < 4; i++) {
					if(i != pos) {
						continue;
					}
					float loopOffset = (i/4F)*(float)Math.PI*2+(TimeKeeper.getClientTick()/100F);
					float rot = (7/64F)*(float)Math.PI*2+(TimeKeeper.getClientTick()/10F)+loopOffset;
					double x = MathHelper.sin(rot)*2;
					double z = MathHelper.cos(rot)*2;
					double y = MathHelper.cos(rot+loopOffset)*1;
					EffectLib.renderLightningP2PRotate(matrixStack, getter, new Vector3(x, y, z), Vector3.ZERO, 8, TimeKeeper.getClientTick()/2, 0.06F, 0.04F, false, 0, 0x6300BD);
				}
			}
		}
	}

	private void renderIngredientEffect(ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer getter, float partialTicks, long randSeed, FusionCrafterFXHandler.IngredFX ingred, int totalParticles) {
		Rotation cameraRotation = new Rotation(new Quat(renderInfo.rotation()));
		IVertexBuilder builder = new TransformingVertexBuilder(getter.getBuffer(particleType), matrixStack);
		// Charge particle ball
		rand.setSeed(randSeed);
		double anim = ingred.getChargeAnim(partialTicks);
		int chargePCount = Math.min(64, totalParticles/3);
		float pScale = 0.025F;
		for(int i = 0; i < chargePCount; i++) {
			anim += rand.nextGaussian();
			float scale = MathHelper.clamp(ingred.getCharge()*ingred.dieOut*chargePCount - i, 0F, 1F)*pScale*(0.7F+rand.nextFloat()*0.3F);
			if(scale <= 0) {
				break;
			}
			float rotX = (float)(rand.nextFloat()*Math.PI*2 + anim/10);
			float rotY = (float)(rand.nextFloat()*Math.PI*2 + anim/15);
			double radius = 0.25;
			double x = ingred.pos.x+radius*MathHelper.cos(rotX)*MathHelper.sin(rotY);
			double y = ingred.pos.y+radius*MathHelper.sin(rotX)*MathHelper.sin(rotY);
			double z = ingred.pos.z+radius*MathHelper.cos(rotY);
			EffectLib.drawParticle(cameraRotation, builder, getTexture(DETextures.ENERGY_PARTICLE), 0F, 0.8F+rand.nextFloat()*0.2F, 1F, x, y, z, scale, 240);
		}
		// Charge Conversion Particles
		int itemPCount = Math.min(48, totalParticles / 3);
		rand.setSeed(randSeed);
		anim = ingred.getChargeAnim(partialTicks);
		pScale = 0.0125F;
		Vector3 pos = new Vector3();
		for(int i = 0; i < itemPCount; i++) {
			anim += rand.nextDouble()*69420;
			int seed = (int)Math.floor(anim/20);
			MathUtils.setRandSeed(seed);
			float pulse = ((float)anim/20) % 1F;
			float scale = MathHelper.clamp(ingred.coreAnim*ingred.dieOut*itemPCount - i, 0, 1);
			if(scale <= 0) {
				break;
			}
			scale *= 1-Math.sin(pulse*Math.PI*2);
			pos.set(MathUtils.nextFloat(), MathUtils.nextFloat(), MathUtils.nextFloat());
			pos.subtract(0.5);
			pos.normalize();
			pos.multiply(MathUtils.nextFloat()*0.1875);
			pos.add(ingred.pos);
			EffectLib.drawParticle(cameraRotation, builder, getTexture(DETextures.SPELL_PARTICLE), 0.7F, 0F, 0F, pos.x, pos.y, pos.z, scale*pScale, 240);
		}
		// Beam Effect
		double randOffset = 0.125;
		rand.setSeed(randSeed);
		anim = ingred.beamAnim;
		pScale = 0.025F;
		int beamPCount = Math.min(32, totalParticles/3);
		if(ingred.beamAnim > 0) {
			for(int i = 0; i < beamPCount; i++) {
				anim += rand.nextDouble()*64;
				float scale = MathHelper.clamp(Math.min(1, ingred.beamAnim/60)*ingred.dieOut*beamPCount - i, 0F, 1F)*pScale*(0.7F+rand.nextFloat()*0.3F);
				if(scale <= 0) {
					break;
				}
				Vector3 start = ingred.pos.copy().add((0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2);
				Vector3 end = new Vector3((0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2);
				pos = MathUtils.interpolateVec3(start, end, (anim/10) % 1D);
				EffectLib.drawParticle(cameraRotation, builder, getTexture(DETextures.SPARK_PARTICLE), 0.7F+(((float)anim/10F) % 1F)*0.3F, 0F, 0F, pos.x, pos.y, pos.z, scale, 240);
			}
		}
	}

	public TextureAtlasSprite getTexture(TextureAtlasSprite[] arr) {
		return arr[Math.floorMod(rand.nextInt(arr.length)+TimeKeeper.getClientTick(), arr.length)];
	}

	public TextureAtlasSprite getTexture(TextureAtlasSprite[] arr, int shift) {
		return arr[Math.floorMod(shift+TimeKeeper.getClientTick(), arr.length)];
	}
}
