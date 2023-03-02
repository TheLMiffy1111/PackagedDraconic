package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;
import thelm.packageddraconic.network.PacketHandler;

public class SyncCrafterPacket {

	private BlockPos pos;
	private IFusionStateMachine.FusionState fusionState;
	private short progress;
	private float animProgress;
	private short animLength;

	public SyncCrafterPacket(FusionCrafterBlockEntity crafter) {
		pos = crafter.getBlockPos();
		fusionState = crafter.fusionState;
		progress = crafter.progress;
		animProgress = crafter.animProgress;
		animLength = crafter.animLength;
	}

	private SyncCrafterPacket(BlockPos pos, byte fusionState, short progress, float animProgress, short animLength) {
		this.pos = pos;
		this.fusionState = IFusionStateMachine.FusionState.values()[fusionState];
		this.progress = progress;
		this.animProgress = animProgress;
		this.animLength = animLength;
	}

	public static void encode(SyncCrafterPacket pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeByte(pkt.fusionState.ordinal());
		buf.writeShort(pkt.progress);
		buf.writeFloat(pkt.animProgress);
		buf.writeShort(pkt.animLength);
	}

	public static SyncCrafterPacket decode(FriendlyByteBuf buf) {
		return new SyncCrafterPacket(buf.readBlockPos(), buf.readByte(), buf.readShort(), buf.readFloat(), buf.readShort());
	}

	public static void handle(SyncCrafterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pkt.pos)) {
				BlockEntity be = level.getBlockEntity(pkt.pos);
				if(be instanceof FusionCrafterBlockEntity crafter) {
					crafter.fusionState = pkt.fusionState;
					crafter.progress = pkt.progress;
					crafter.animProgress = pkt.animProgress;
					crafter.animLength = pkt.animLength;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(FusionCrafterBlockEntity crafter) {
		double x = crafter.getBlockPos().getX()+0.5;
		double y = crafter.getBlockPos().getY()+0.5;
		double z = crafter.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, crafter.getLevel().dimension())), new SyncCrafterPacket(crafter));
	}
}
