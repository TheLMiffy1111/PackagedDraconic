package thelm.packageddraconic.packet;

import com.brandon3055.brandonscore.client.particle.IntParticleData;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.handlers.DESounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thelm.packageddraconic.block.entity.FusionCrafterBlockEntity;

public record FinishCraftEffectsPacket(BlockPos pos, boolean doParticles) implements CustomPacketPayload {

	public static final Type<FinishCraftEffectsPacket> TYPE = new Type<>(ResourceLocation.parse("packageddraconic:finish_craft_effects"));
	public static final StreamCodec<RegistryFriendlyByteBuf, FinishCraftEffectsPacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, FinishCraftEffectsPacket::pos,
			ByteBufCodecs.BOOL, FinishCraftEffectsPacket::doParticles,
			FinishCraftEffectsPacket::new);

	@Override
	public Type<FinishCraftEffectsPacket> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(()->{
			ClientLevel level = Minecraft.getInstance().level;
			if(level.isLoaded(pos)) {
				if(doParticles) {
					level.addParticle(ParticleTypes.EXPLOSION, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 1, 0, 0);
					for(int i = 0; i < 100; i++) {
						double velX = (level.random.nextDouble()-0.5)*0.1;
						double velY = (level.random.nextDouble()-0.5)*0.1;
						double velZ = (level.random.nextDouble()-0.5)*0.1;
						level.addParticle(new IntParticleData(DEParticles.ENERGY_BASIC.get(), 0, 255, 255, 64), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, velX, velY, velZ);
					}
				}
				level.playLocalSound(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, DESounds.FUSION_COMPLETE.get(), SoundSource.BLOCKS, 4F, (1F+(level.random.nextFloat()-level.random.nextFloat())*0.2F)*0.7F, false);
			}
		});
	}

	public static void finishCraft(FusionCrafterBlockEntity crafter, boolean doParticles) {
		if(crafter.getLevel() instanceof ServerLevel level) {
			double x = crafter.getBlockPos().getX()+0.5;
			double y = crafter.getBlockPos().getY()+0.5;
			double z = crafter.getBlockPos().getZ()+0.5;
			PacketDistributor.sendToPlayersNear(level, null, x, y, z, 32, new FinishCraftEffectsPacket(crafter.getBlockPos(), doParticles));
		}
	}
}
