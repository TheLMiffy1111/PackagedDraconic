package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.tile.MarkedInjectorTile;

public class SyncInjectorPacket {

	private BlockPos pos;
	private long op;
	private long req;

	public SyncInjectorPacket(MarkedInjectorTile tile) {
		pos = tile.getBlockPos();
		op = tile.getInjectorEnergy();
		req = tile.getEnergyRequirement();
	}

	private SyncInjectorPacket(BlockPos pos, long op, long req) {
		this.pos = pos;
		this.op = op;
		this.req = req;
	}

	public static void encode(SyncInjectorPacket pkt, PacketBuffer buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeLong(pkt.op);
		buf.writeLong(pkt.req);
	}

	public static SyncInjectorPacket decode(PacketBuffer buf) {
		return new SyncInjectorPacket(buf.readBlockPos(), buf.readLong(), buf.readLong());
	}

	public static void handle(SyncInjectorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientWorld world = Minecraft.getInstance().level;
			if(world.isLoaded(pkt.pos)) {
				TileEntity te = world.getBlockEntity(pkt.pos);
				if(te instanceof MarkedInjectorTile) {
					MarkedInjectorTile tile = (MarkedInjectorTile)te;
					tile.setInjectorEnergy(pkt.op);
					tile.setEnergyRequirement(pkt.req, 0);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(MarkedInjectorTile tile) {
		double x = tile.getBlockPos().getX()+0.5;
		double y = tile.getBlockPos().getY()+0.5;
		double z = tile.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, tile.getLevel().dimension())), new SyncInjectorPacket(tile));
	}
}
