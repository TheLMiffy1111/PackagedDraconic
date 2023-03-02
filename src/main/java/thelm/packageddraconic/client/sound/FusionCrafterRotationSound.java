package thelm.packageddraconic.client.sound;

import com.brandon3055.draconicevolution.handlers.DESounds;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

//Code modified from FusionRotationSound
public class FusionCrafterRotationSound extends SimpleSoundInstance implements TickableSoundInstance {

	private FusionCrafterBlockEntity blockEntity;

	public FusionCrafterRotationSound(FusionCrafterBlockEntity blockEntity) {
		super(DESounds.fusionRotation, SoundSource.BLOCKS, 1.5F, 1, blockEntity.getBlockPos());
		this.blockEntity = blockEntity;
		looping = true;
	}

	@Override
	public boolean isStopped() {
		return blockEntity.isRemoved() || !blockEntity.isWorking;
	}

	@Override
	public void tick() {}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
