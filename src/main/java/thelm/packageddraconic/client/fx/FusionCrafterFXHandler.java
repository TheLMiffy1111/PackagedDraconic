package thelm.packageddraconic.client.fx;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.brandon3055.brandonscore.api.TimeKeeper;
import com.brandon3055.brandonscore.utils.MathUtils;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;
import com.brandon3055.draconicevolution.handlers.DESounds;

import codechicken.lib.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;
import thelm.packageddraconic.client.sound.FusionCrafterRotationSound;

// Code modified from FusionTileFXHandler
public class FusionCrafterFXHandler implements Runnable {

	private static Random rand = new Random();
	private final FusionCrafterBlockEntity crafter;
	private float rotationTick = 0;
	private float rotationSpeed = 0;
	private int coreDischarge = -1;

	private int baseCraftTime = 300;
	private int translateStartTime = 0;
	private int rotStartTime = 30;	
	private int beamStartTime = 60;
	private int dieOutStart = 100;
	private float animRadius = 2;
	public float injectTime = 0;
	public float chargeState = 0;
	private int runTick = 0;
	private FusionCrafterRotationSound sound = null;

	public FusionCrafterFXHandler(FusionCrafterBlockEntity crafter) {
		this.crafter = crafter;
	}

	@Override
	public void run() {
		IFusionRecipe recipe;
		if(!crafter.isWorking || (recipe = crafter.effectRecipe) == null) {
			rotationTick = -3;
			sound = null;
			injectTime = 0;
			chargeState = 0;
			runTick = -1;
			return;
		}
		IFusionStateMachine.FusionState state = crafter.getFusionState();
		if(state.ordinal() < IFusionStateMachine.FusionState.CRAFTING.ordinal()) {
			rotationTick = -3;
			rotationSpeed = 0;
			injectTime = 0;
			runTick = -1;
		}
		else {
			float prevTick = rotationTick;
			Vector3 corePos = Vector3.fromTileCenter(crafter);
			if(runTick <= 0) {
				crafter.getLevel().playLocalSound(corePos.x, corePos.y, corePos.z, DESounds.fusionComplete, SoundSource.BLOCKS, 0.5F, 0.5F, false);
			}
			if(runTick == -1) {
				getIngredients(0).forEach(e->crafter.getLevel().addParticle(ParticleTypes.EXPLOSION, corePos.x+e.pos.x, corePos.y+e.pos.y, corePos.z + e.pos.z, 1, 0, 0));
			}
			rotationTick += rotationSpeed;
			runTick++;
			rotationSpeed = (float)baseCraftTime / Math.max(crafter.animLength, 1);
			if(rotationTick+3 >= rotStartTime && prevTick+3 < rotStartTime+3) {
				crafter.getLevel().playLocalSound(corePos.x, corePos.y, corePos.z, DESounds.fusionComplete, SoundSource.BLOCKS, 2F, 0.5F, false);
				if(sound == null) {
					sound = new FusionCrafterRotationSound(crafter);
					sound.setPitch(0.5F+(1.5F*(rotationSpeed-1)));
					Minecraft.getInstance().getSoundManager().play(sound);
				}
			}
			injectTime = Math.max(0, (rotationTick-beamStartTime)/(float)(baseCraftTime-beamStartTime));
			if(injectTime > 0) {
				if(TimeKeeper.getClientTick() % 5 == 0) {
					crafter.getLevel().playLocalSound(corePos.x, corePos.y, corePos.z, DESounds.energyBolt, SoundSource.BLOCKS, 1F, 1F, false);
				}
			}
		}
		long totalCharge = crafter.getInjectors().stream().mapToLong(IFusionInjector::getInjectorEnergy).sum();
		chargeState = totalCharge / (float)recipe.getEnergyCost();
		float arcChance = chargeState*0.1F + crafter.animProgress*0.2F + (rotationSpeed > 1 ? ((rotationSpeed-1)*0.25F) : 0F);
		if(coreDischarge != -1) {
			coreDischarge = -1;
		}
		else if(rand.nextFloat() < arcChance) {
			List<IngredFX> ingreds = getIngredients(0);
			if(ingreds.isEmpty()) {
				return;
			}
			coreDischarge = rand.nextInt(ingreds.size());
			Vector3 pos = Vector3.fromTileCenter(crafter).add(ingreds.get(coreDischarge).pos);
			crafter.getLevel().playLocalSound(pos.x, pos.y, pos.z, DESounds.energyBolt, SoundSource.BLOCKS, 2F, 1F, false);
		}
	}

	public List<IngredFX> getIngredients(float partialTicks) {
		List<IngredFX> ingredFXES = new ArrayList<>();
		Vector3 corePos = Vector3.fromTileCenter(crafter);
		int injCount = (int)crafter.getInjectors().stream().filter(e->!e.getInjectorStack().isEmpty()).count();
		double baseRotateSpeed = 8;
		baseRotateSpeed /= 1200;
		baseRotateSpeed *= Math.PI*2;
		float rotateAnim = getRotationAnim(partialTicks);
		int i = 0;
		for(IFusionInjector iInjector : crafter.getInjectors()) {
			if(iInjector.getInjectorStack().isEmpty()) {
				continue;
			}
			MarkedInjectorBlockEntity injector = (MarkedInjectorBlockEntity)iInjector;
			Vector3 injPos = Vector3.fromTileCenter(injector).subtract(corePos);
			injPos.add(Vector3.fromVec3i(injector.getDirection().getNormal()).multiply(0.45));
			float startAngle = (i/(float)injCount)*(float)Math.PI*2;
			startAngle += (rotateAnim >= rotStartTime ? rotateAnim-rotStartTime : 0)*baseRotateSpeed;
			double x = Mth.cos(startAngle)*animRadius;
			double z = Mth.sin(startAngle)*animRadius;
			Vector3 animPos = new Vector3(x, 0, z);
			if(rotateAnim < rotStartTime) {
				animPos = MathUtils.interpolateVec3(injPos, animPos, rotationTick-translateStartTime > 0 ? (rotateAnim-translateStartTime)/(rotStartTime-translateStartTime) : 0);
			}
			IngredFX ingredFX = new IngredFX(animPos, injector);
			if(i == coreDischarge) {
				ingredFX.arcPos = Vector3.ZERO;
			}
			if(rotateAnim > 0) {
				ingredFX.coreAnim = Math.min(1, (rotateAnim/translateStartTime)*2);
			}
			ingredFX.beamAnim = rotateAnim - beamStartTime;
			ingredFX.dieOut = Mth.clamp(1-(rotateAnim-dieOutStart)/(baseCraftTime-dieOutStart), 0, 1);
			ingredFXES.add(ingredFX);
			i++;
		}
		return ingredFXES;
	}

	public boolean renderActive() {
		return crafter.isWorking;
	}

	public float getRotationAnim(float partialTicks) {
		return rotationTick + rotationSpeed*partialTicks;
	}

	public static class IngredFX {

		public Vector3 pos;
		private IFusionInjector injector;
		public Vector3 arcPos = null;
		public float beamAnim = 0;
		public float coreAnim = 0;
		public float dieOut = 1;

		public IngredFX(Vector3 pos, IFusionInjector injector) {
			this.pos = pos;
			this.injector = injector;
		}

		public double getChargeAnim(float partialTicks) {
			return TimeKeeper.getClientTick()+partialTicks;
		}

		public float getCharge() {
			return injector.getInjectorEnergy() / (float)injector.getEnergyRequirement();
		}
	}
}
