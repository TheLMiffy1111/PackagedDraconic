package thelm.packageddraconic.client.sound;

import com.brandon3055.draconicevolution.handlers.DESounds;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundCategory;
import thelm.packageddraconic.tile.FusionCrafterTile;

//Code modified from FusionRotationSound
public class FusionCrafterRotationSound extends SimpleSound implements ITickableSound {

	private FusionCrafterTile tile;

	public FusionCrafterRotationSound(FusionCrafterTile tile) {
		super(DESounds.fusionRotation, SoundCategory.BLOCKS, 1.5F, 1, tile.getBlockPos());
		this.tile = tile;
		looping = true;
	}

	@Override
	public boolean isStopped() {
		return tile.isRemoved() || !tile.isWorking;
	}

	@Override
	public void tick() {}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
}
