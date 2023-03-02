package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.handlers.DESounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.tile.FusionCrafterTile;

public class FinishCraftEffectsPacket {

	private BlockPos pos;
	private boolean doParticles;

	public FinishCraftEffectsPacket(BlockPos pos, boolean doParticles) {
		this.pos = pos;
		this.doParticles = doParticles;
	}

	public static void encode(FinishCraftEffectsPacket pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeBoolean(pkt.doParticles);
	}

	public static FinishCraftEffectsPacket decode(PacketBuffer buf) {
		return new FinishCraftEffectsPacket(buf.readBlockPos(), buf.readBoolean());
	}

	public static void handle(FinishCraftEffectsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientWorld world = Minecraft.getInstance().level;
			if(world.isLoaded(pkt.pos)) {
				if(pkt.doParticles) {
					world.addParticle(ParticleTypes.EXPLOSION, pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, 1, 0, 0);
					for(int i = 0; i < 100; i++) {
						double velX = (world.random.nextDouble()-0.5)*0.1;
						double velY = (world.random.nextDouble()-0.5)*0.1;
						double velZ = (world.random.nextDouble()-0.5)*0.1;
						world.addParticle(new IntParticleType.IntParticleData(DEParticles.energy_basic, 0, 255, 255, 64), pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, velX, velY, velZ);
					}
				}
				world.playLocalSound(pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, DESounds.fusionComplete, SoundCategory.BLOCKS, 4F, (1F+(world.random.nextFloat()-world.random.nextFloat())*0.2F)*0.7F, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void finishCraft(FusionCrafterTile tile, boolean doParticles) {
		double x = tile.getBlockPos().getX()+0.5;
		double y = tile.getBlockPos().getY()+0.5;
		double z = tile.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, tile.getLevel().dimension())), new FinishCraftEffectsPacket(tile.getBlockPos(), doParticles));
	}
}
