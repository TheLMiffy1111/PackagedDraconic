package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import com.brandon3055.brandonscore.client.particle.IntParticleType;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.handlers.DESounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.network.PacketHandler;

public class FinishCraftEffectsPacket {

	private BlockPos pos;
	private boolean doParticles;

	public FinishCraftEffectsPacket(BlockPos pos, boolean doParticles) {
		this.pos = pos;
		this.doParticles = doParticles;
	}

	public static void encode(FinishCraftEffectsPacket pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeBoolean(pkt.doParticles);
	}

	public static FinishCraftEffectsPacket decode(FriendlyByteBuf buf) {
		return new FinishCraftEffectsPacket(buf.readBlockPos(), buf.readBoolean());
	}

	public static void handle(FinishCraftEffectsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pkt.pos)) {
				if(pkt.doParticles) {
					level.addParticle(ParticleTypes.EXPLOSION, pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, 1, 0, 0);
					for(int i = 0; i < 100; i++) {
						double velX = (level.random.nextDouble()-0.5)*0.1;
						double velY = (level.random.nextDouble()-0.5)*0.1;
						double velZ = (level.random.nextDouble()-0.5)*0.1;
						level.addParticle(new IntParticleType.IntParticleData(DEParticles.energy_basic, 0, 255, 255, 64), pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, velX, velY, velZ);
					}
				}
				level.playLocalSound(pkt.pos.getX()+0.5, pkt.pos.getY()+0.5, pkt.pos.getZ()+0.5, DESounds.fusionComplete, SoundSource.BLOCKS, 4F, (1F+(level.random.nextFloat()-level.random.nextFloat())*0.2F)*0.7F, false);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void finishCraft(FusionCrafterBlockEntity crafter, boolean doParticles) {
		double x = crafter.getBlockPos().getX()+0.5;
		double y = crafter.getBlockPos().getY()+0.5;
		double z = crafter.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, crafter.getLevel().dimension())), new FinishCraftEffectsPacket(crafter.getBlockPos(), doParticles));
	}
}
