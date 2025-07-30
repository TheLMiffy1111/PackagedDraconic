package thelm.packageddraconic.packet;

import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

public record SyncCrafterPacket(BlockPos pos, IFusionStateMachine.FusionState fusionState, short progress, float animProgress, short animLength) implements CustomPacketPayload {

	public static final Type<SyncCrafterPacket> TYPE = new Type<>(ResourceLocation.parse("packageddraconic:sync_crafter"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncCrafterPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, SyncCrafterPacket::pos,
			ByteBufCodecs.idMapper(ByIdMap.continuous(
					IFusionStateMachine.FusionState::ordinal,
					IFusionStateMachine.FusionState.values(),
					ByIdMap.OutOfBoundsStrategy.ZERO),
					IFusionStateMachine.FusionState::ordinal), SyncCrafterPacket::fusionState,
			ByteBufCodecs.SHORT, SyncCrafterPacket::progress,
			ByteBufCodecs.FLOAT, SyncCrafterPacket::animProgress,
			ByteBufCodecs.SHORT, SyncCrafterPacket::animLength,
			SyncCrafterPacket::new);

	public SyncCrafterPacket(FusionCrafterBlockEntity crafter) {
		this(crafter.getBlockPos(), crafter.fusionState, crafter.progress, crafter.animProgress, crafter.animLength);
	}

	@Override
	public Type<SyncCrafterPacket> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pos)) {
				BlockEntity be = level.getBlockEntity(pos);
				if(be instanceof FusionCrafterBlockEntity crafter) {
					crafter.fusionState = fusionState;
					crafter.progress = progress;
					crafter.animProgress = animProgress;
					crafter.animLength = animLength;
				}
			}
		});
	}

	public static void sync(FusionCrafterBlockEntity crafter) {
		if(crafter.getLevel() instanceof ServerLevel level) {
			double x = crafter.getBlockPos().getX()+0.5;
			double y = crafter.getBlockPos().getY()+0.5;
			double z = crafter.getBlockPos().getZ()+0.5;
			PacketDistributor.sendToPlayersNear(level, null, x, y, z, 32, new SyncCrafterPacket(crafter));
		}
	}
}
