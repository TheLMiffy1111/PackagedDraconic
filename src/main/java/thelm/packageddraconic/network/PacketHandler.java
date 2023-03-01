package thelm.packageddraconic.network;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import thelm.packageddraconic.network.packet.FinishCraftEffectsPacket;
import thelm.packageddraconic.network.packet.SyncCrafterPacket;
import thelm.packageddraconic.network.packet.SyncInjectorPacket;

public class PacketHandler {

	public static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("packageddraconic", PROTOCOL_VERSION),
			()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(id++, SyncInjectorPacket.class,
				SyncInjectorPacket::encode, SyncInjectorPacket::decode,
				SyncInjectorPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		INSTANCE.registerMessage(id++, SyncCrafterPacket.class,
				SyncCrafterPacket::encode, SyncCrafterPacket::decode,
				SyncCrafterPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		INSTANCE.registerMessage(id++, FinishCraftEffectsPacket.class,
				FinishCraftEffectsPacket::encode, FinishCraftEffectsPacket::decode,
				FinishCraftEffectsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
}
