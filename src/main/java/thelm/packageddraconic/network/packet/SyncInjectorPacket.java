package thelm.packageddraconic.network.packet;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import thelm.packageddraconic.block.entity.MarkedInjectorBlockEntity;
import thelm.packageddraconic.network.PacketHandler;

public class SyncInjectorPacket {

	private BlockPos pos;
	private long op;
	private long req;

	public SyncInjectorPacket(MarkedInjectorBlockEntity injector) {
		pos = injector.getBlockPos();
		op = injector.getInjectorEnergy();
		req = injector.getEnergyRequirement();
	}

	private SyncInjectorPacket(BlockPos pos, long op, long req) {
		this.pos = pos;
		this.op = op;
		this.req = req;
	}

	public static void encode(SyncInjectorPacket pkt, FriendlyByteBuf buf) {
		buf.writeBlockPos(pkt.pos);
		buf.writeLong(pkt.op);
		buf.writeLong(pkt.req);
	}

	public static SyncInjectorPacket decode(FriendlyByteBuf buf) {
		return new SyncInjectorPacket(buf.readBlockPos(), buf.readLong(), buf.readLong());
	}

	public static void handle(SyncInjectorPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pkt.pos)) {
				BlockEntity be = level.getBlockEntity(pkt.pos);
				if(be instanceof MarkedInjectorBlockEntity injector) {
					injector.setInjectorEnergy(pkt.op);
					injector.setEnergyRequirement(pkt.req, 0);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void sync(MarkedInjectorBlockEntity injector) {
		double x = injector.getBlockPos().getX()+0.5;
		double y = injector.getBlockPos().getY()+0.5;
		double z = injector.getBlockPos().getZ()+0.5;
		PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(()->new TargetPoint(x, y, z, 32, injector.getLevel().dimension())), new SyncInjectorPacket(injector));
	}
}
