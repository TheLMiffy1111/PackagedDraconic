package thelm.packageddraconic.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.packagedauto.network.ISelfHandleMessage;
import thelm.packageddraconic.network.PacketHandler;
import thelm.packageddraconic.tile.TileFusionCrafter;

public class PacketSyncCrafter implements ISelfHandleMessage<IMessage> {

	private long pos;
	private short progress;

	public PacketSyncCrafter() {}

	public PacketSyncCrafter(TileFusionCrafter tile) {
		pos = tile.getPos().toLong();
		progress = tile.progress;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = buf.readLong();
		progress = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos);
		buf.writeShort(progress);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(()->{
			WorldClient world = Minecraft.getMinecraft().world;
			BlockPos pos = BlockPos.fromLong(this.pos);
			if(world.isBlockLoaded(pos)) {
				TileEntity te = world.getTileEntity(pos);
				if(te instanceof TileFusionCrafter) {
					((TileFusionCrafter)te).progress = progress;
				}
			}
		});
		return null;
	}

	public static void sync(TileFusionCrafter tile) {
		double x = tile.getPos().getX()+0.5;
		double y = tile.getPos().getY()+0.5;
		double z = tile.getPos().getZ()+0.5;
		PacketHandler.INSTANCE.sendToAllAround(new PacketSyncCrafter(tile), new TargetPoint(tile.getWorld().provider.getDimension(), x, y, z, 32));
	}
}
