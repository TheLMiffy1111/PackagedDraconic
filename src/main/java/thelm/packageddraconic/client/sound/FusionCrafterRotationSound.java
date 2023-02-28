package thelm.packageddraconic.client.sound;

import com.brandon3055.draconicevolution.lib.DESoundHandler;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import thelm.packageddraconic.tile.TileFusionCrafter;

// Code modified from FusionRotationSound
public class FusionCrafterRotationSound extends PositionedSound implements ITickableSound {
	
	private TileFusionCrafter tile;

	public FusionCrafterRotationSound(TileFusionCrafter tile) {
		super(DESoundHandler.fusionRotation, SoundCategory.BLOCKS);
		this.tile = tile;
		xPosF = tile.getPos().getX()+0.5F;
		yPosF = tile.getPos().getY()+0.5F;
		zPosF = tile.getPos().getZ()+0.5F;
		repeat = true;
		volume = 1.5F;
	}

	public boolean isDonePlaying() {
		return tile.isInvalid() || !tile.isWorking;
	}

	public void update() {
		this.pitch = 0.1F+(tile.progress-1000)/1000F*1.9F;
	}
}
