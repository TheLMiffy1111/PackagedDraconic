package thelm.packageddraconic.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;

public record SyncInjectorPacket(BlockPos pos, long op, long req) implements CustomPacketPayload {

	public static final Type<SyncInjectorPacket> TYPE = new Type<>(ResourceLocation.parse("packageddraconic:sync_injector"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncInjectorPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncInjectorPacket::pos,
			ByteBufCodecs.VAR_LONG, SyncInjectorPacket::op,
			ByteBufCodecs.VAR_LONG, SyncInjectorPacket::req,
			SyncInjectorPacket::new);

	public SyncInjectorPacket(MarkedInjectorBlockEntity injector) {
		this(injector.getBlockPos(), injector.getInjectorEnergy(), injector.getEnergyRequirement());
	}

	@Override
	public Type<SyncInjectorPacket> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pos)) {
				BlockEntity be = level.getBlockEntity(pos);
				if(be instanceof MarkedInjectorBlockEntity injector) {
					injector.setInjectorEnergy(op);
					injector.setEnergyRequirement(req, 0);
				}
			}
		});
	}

	public static void sync(MarkedInjectorBlockEntity injector) {
		if(injector.getLevel() instanceof ServerLevel level) {
			double x = injector.getBlockPos().getX()+0.5;
			double y = injector.getBlockPos().getY()+0.5;
			double z = injector.getBlockPos().getZ()+0.5;
			PacketDistributor.sendToPlayersNear(level, null, x, y, z, 32, new SyncInjectorPacket(injector));
		}
	}
}
