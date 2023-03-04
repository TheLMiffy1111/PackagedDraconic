package thelm.packageddraconic.client.renderer;

import java.util.List;
import java.util.Random;

import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.client.DEMiscSprites;
import com.brandon3055.draconicevolution.client.render.EffectLib;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;

import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.vec.Quat;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.client.RenderTimer;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.client.fx.FusionCrafterFXHandler;

// Code modified from RenderTileFusionCraftingCore
public class FusionCrafterRenderer implements BlockEntityRenderer<FusionCrafterBlockEntity> {

	private static Random rand = new Random();

	private RenderType particleType = RenderType.create("particle_type", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, false,
			RenderType.CompositeState.builder().
			setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader)).
			setTextureState(new RenderStateShard.TextureStateShard(DEMiscSprites.ATLAS_LOCATION, false, false)).
			setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true)).
			createCompositeState(false));

	public FusionCrafterRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(FusionCrafterBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		renderContent(blockEntity, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);
		FusionCrafterFXHandler handler = (FusionCrafterFXHandler)blockEntity.fxHandler;
		if(handler.renderActive()) {
			renderEffects(blockEntity, handler, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);
		}
	}

	private void renderContent(FusionCrafterBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int packetOverlay) {
		ItemStack stack = !blockEntity.getOutputStack().isEmpty() && !blockEntity.isWorking ? blockEntity.getOutputStack() : blockEntity.getCatalystStack();
		Minecraft mc = Minecraft.getInstance();
		if(!stack.isEmpty()) {
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			poseStack.scale(0.5F, 0.5F, 0.5F);mc.getFrameTime();
			poseStack.mulPose(new Quaternion(0, (RenderTimer.INSTANCE.getTicks()+partialTicks)*0.8F, 0, true));
			mc.getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, packetOverlay, poseStack, buffer, (int)blockEntity.getBlockPos().asLong());
			poseStack.popPose();
		}
	}

	private void renderEffects(FusionCrafterBlockEntity blockEntity, FusionCrafterFXHandler handler, float partialTicks, PoseStack poseStack, MultiBufferSource getter, int combinedLight, int packetOverlay) {
		Minecraft mc = Minecraft.getInstance();
		Camera camera = mc.gameRenderer.getMainCamera();
		int ticks = RenderTimer.INSTANCE.getTicks();
		float time = ticks+partialTicks;
		poseStack.translate(0.5, 0.5, 0.5);
		ParticleStatus pStatus = mc.options.particles;
		double particleSetting = pStatus == ParticleStatus.ALL ? 1 : pStatus == ParticleStatus.DECREASED ? 2/3D : 1/3D;
		// Total particle allocation for ingredient effects
		int maxParticles = (int)(1000*particleSetting);
		List<FusionCrafterFXHandler.IngredFX> ingredFXList = handler.getIngredients(partialTicks);
		int i = 0;
		for(FusionCrafterFXHandler.IngredFX ingred : ingredFXList) {
			renderIngredientEffect(camera, poseStack, getter, partialTicks, i++, ingred, maxParticles/ingredFXList.size());
			if(ingred.arcPos != null) {
				EffectLib.renderLightningP2PRotate(poseStack, getter, ingred.pos, ingred.arcPos, 8, ticks/2, 0.06F, 0.04F, false, 0, 0x6300BD);
			}
		}
		Rotation cameraRotation = new Rotation(new Quat(camera.rotation()));
		VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(particleType), poseStack);
		if(handler.injectTime > 0) {
			rand.setSeed(3055);
			double anim = handler.getRotationAnim(partialTicks);
			int chargePCount = 64;
			float pScale = 0.125F/2;
			for(i = 0; i < chargePCount; i++) {
				anim += rand.nextGaussian();
				float scale = Mth.clamp(handler.injectTime*chargePCount - i, 0F, 1F)*pScale*(0.7F+(rand.nextFloat()*0.3F));
				if(scale <= 0) {
					break;
				}
				float rotX = (float)(rand.nextFloat()*Math.PI*2+anim/10);
				float rotY = (float)(rand.nextFloat()*Math.PI*2+anim/15);
				double radius = 0.35*MathUtils.clampMap(blockEntity.animProgress, 0.95F, 1F, 1F, 0F);
				double x = radius*Mth.cos(rotX)*Mth.sin(rotY);
				double y = radius*Mth.sin(rotX)*Mth.sin(rotY);
				double z = radius*Mth.cos(rotY);
				EffectLib.drawParticle(cameraRotation, builder, getTexture(DEMiscSprites.MIXED_PARTICLE), 1F, 0, 0, x, y, z, scale, 240);
			}
		}
		//Outer Loopy Effects
		if(handler.chargeState > 0) {
			for(i = 0; i < 4; i++) {
				float loopOffset = (i/4F)*(float)Math.PI*2 + time/100;
				for(int j = 0; j < 8; j++) {
					float rot = (j/64F)*(float)Math.PI*2 + time/10 + loopOffset;
					if(j > handler.chargeState*8) {
						continue;
					}
					double x = Mth.sin(rot)*2;
					double z = Mth.cos(rot)*2;
					double y = Mth.cos(rot+loopOffset)*1;
					float scale = 0.1F*(j/8F);
					EffectLib.drawParticle(cameraRotation, builder, getTexture(DEMiscSprites.ENERGY_PARTICLE, j), 106/255F, 13/255F, 173/255F, x, y, z, scale, 240);
				}
			}
			if(handler.injectTime > 0 && ticks % 5 == 0) {
				int pos = rand.nextInt(4);
				for(i = 0; i < 4; i++) {
					if(i != pos) {
						continue;
					}
					float loopOffset = (i/4F)*(float)Math.PI*2 + time/100;
					float rot = (7/64F)*(float)Math.PI*2 + time/10 + loopOffset;
					double x = Mth.sin(rot)*2;
					double z = Mth.cos(rot)*2;
					double y = Mth.cos(rot+loopOffset)*1;
					EffectLib.renderLightningP2PRotate(poseStack, getter, new Vector3(x, y, z), Vector3.ZERO, 8, ticks/2, 0.06F, 0.04F, false, 0, 0x6300BD);
				}
			}
		}
	}

	private void renderIngredientEffect(Camera camera, PoseStack poseStack, MultiBufferSource getter, float partialTicks, long randSeed, FusionCrafterFXHandler.IngredFX ingred, int totalParticles) {
		Rotation cameraRotation = new Rotation(new Quat(camera.rotation()));
		VertexConsumer builder = new TransformingVertexConsumer(getter.getBuffer(particleType), poseStack);
		// Charge particle ball
		rand.setSeed(randSeed);
		double anim = ingred.getChargeAnim(partialTicks);
		int chargePCount = Math.min(64, totalParticles/3);
		float pScale = 0.025F;
		for(int i = 0; i < chargePCount; i++) {
			anim += rand.nextGaussian();
			float scale = Mth.clamp(ingred.getCharge()*ingred.dieOut*chargePCount - i, 0F, 1F)*pScale*(0.7F+rand.nextFloat()*0.3F);
			if(scale <= 0) {
				break;
			}
			float rotX = (float)(rand.nextFloat()*Math.PI*2 + anim/10);
			float rotY = (float)(rand.nextFloat()*Math.PI*2 + anim/15);
			double radius = 0.25;
			double x = ingred.pos.x+radius*Mth.cos(rotX)*Mth.sin(rotY);
			double y = ingred.pos.y+radius*Mth.sin(rotX)*Mth.sin(rotY);
			double z = ingred.pos.z+radius*Mth.cos(rotY);
			EffectLib.drawParticle(cameraRotation, builder, getTexture(DEMiscSprites.ENERGY_PARTICLE), 0F, 0.8F+rand.nextFloat()*0.2F, 1F, x, y, z, scale, 240);
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
			float scale = Mth.clamp(ingred.coreAnim*ingred.dieOut*itemPCount - i, 0, 1);
			if(scale <= 0) {
				break;
			}
			scale *= 1-Math.sin(pulse*Math.PI*2);
			pos.set(MathUtils.nextFloat(), MathUtils.nextFloat(), MathUtils.nextFloat());
			pos.subtract(0.5);
			pos.normalize();
			pos.multiply(MathUtils.nextFloat()*0.1875);
			pos.add(ingred.pos);
			EffectLib.drawParticle(cameraRotation, builder, getTexture(DEMiscSprites.SPELL_PARTICLE), 0.7F, 0F, 0F, pos.x, pos.y, pos.z, scale*pScale, 240);
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
				float scale = Mth.clamp(Math.min(1, ingred.beamAnim/60)*ingred.dieOut*beamPCount - i, 0F, 1F)*pScale*(0.7F+rand.nextFloat()*0.3F);
				if(scale <= 0) {
					break;
				}
				Vector3 start = ingred.pos.copy().add((0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2);
				Vector3 end = new Vector3((0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2, (0.5-rand.nextDouble())*randOffset*2);
				pos = MathUtils.interpolateVec3(start, end, (anim/10) % 1D);
				EffectLib.drawParticle(cameraRotation, builder, getTexture(DEMiscSprites.SPARK_PARTICLE), 0.7F+(((float)anim/10F) % 1F)*0.3F, 0F, 0F, pos.x, pos.y, pos.z, scale, 240);
			}
		}
	}

	public TextureAtlasSprite getTexture(TextureAtlasSprite[] arr) {
		return arr[Math.floorMod(rand.nextInt(arr.length)+RenderTimer.INSTANCE.getTicks(), arr.length)];
	}

	public TextureAtlasSprite getTexture(TextureAtlasSprite[] arr, int shift) {
		return arr[Math.floorMod(shift+RenderTimer.INSTANCE.getTicks(), arr.length)];
	}
}
