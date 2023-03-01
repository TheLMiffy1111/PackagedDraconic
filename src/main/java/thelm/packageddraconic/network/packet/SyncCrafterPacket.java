package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import com.brandon3055.draconicevolution.api.crafting.IFusionStateMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.tile.FusionCrafterTile;

public class SyncCrafterPacket {

	private BlockPos pos;
	private IFusionStateMachine.FusionState fusionState;
	private short progress;
	private float animProgress;
	private short animLength;

	public SyncCrafterPacket(FusionCrafterTile tile) {
		pos = tile.getBlockPos();
		fusionState = tile.fusionState;
		progress = tile.progress;
		animProgress = tile.animProgress;
		animLength = tile.animLength;
	}

	private SyncCrafterPacket(BlockPos pos, byte fusionState, short progress, float animProgress, short animLength) {
		this.pos = pos;
		this.fusionState = IFusionStateMachine.FusionState.values()[fusionState];
		this.progress = progress;
		this.animProgress = animProgress;
		this.animLength = animLength;
	}

	public static void encode(SyncCrafterPacket pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeByte(pkt.fusionState.ordinal());
		buf.writeShort(pkt.progress);
		buf.writeFloat(pkt.animProgress);
		buf.writeShort(pkt.animLength);
	}

	public static SyncCrafterPacket decode(PacketBuffer buf) {
		return new SyncCrafterPacket(buf.readBlockPos(), buf.readByte(), buf.readShort(), buf.readFloat(), buf.readShort());
	}

	public static void handle(SyncCrafterPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientWorld world = Minecraft.getInstance().level;
			if(world.isLoaded(pkt.pos)) {
				TileEntity te = world.getBlockEntity(pkt.pos);
				if(te instanceof FusionCrafterTile) {
					FusionCrafterTile tile = (FusionCrafterTile)te;
					tile.fusionState = pkt.fusionState;
					tile.progress = pkt.progress;
					tile.animProgress = pkt.animProgress;
					tile.animLength = pkt.animLength;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(FusionCrafterTile tile) {
		double x = tile.getBlockPos().getX()+0.5;
		double y = tile.getBlockPos().getY()+0.5;
		double z = tile.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, tile.getLevel().dimension())), new SyncCrafterPacket(tile));
	}
}
