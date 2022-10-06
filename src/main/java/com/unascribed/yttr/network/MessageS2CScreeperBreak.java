package com.unascribed.yttr.network;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YSounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class MessageS2CScreeperBreak extends S2CMessage {
	
	public BlockPos pos;
	@MarshalledAs("varint")
	public int blockState;
	public boolean boom;
	
	public MessageS2CScreeperBreak(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CScreeperBreak(BlockPos pos, BlockState blockState, boolean boom) {
		super(YNetwork.CONTEXT);
		this.pos = pos;
		this.blockState = Block.getRawIdFromState(blockState);
		this.boom = boom;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		double cX = pos.getX()+0.5;
		double cY = pos.getY()+0.5;
		double cZ = pos.getZ()+0.5;
		var r = ThreadLocalRandom.current();
		mc.world.addBlockBreakParticles(pos, Block.getStateFromRawId(blockState));
		for (int i = 0; i < 10; i++) {
			if (boom) {
				for (int j = 0; j < 2; j++) {
					mc.world.addParticle(ParticleTypes.SMOKE, cX, cY, cZ, r.nextGaussian()*0.15, r.nextGaussian()*0.15, r.nextGaussian()*0.15);
				}
			}
			double magnitude = boom ? 0.1 : 0.05;
			mc.world.addParticle(ParticleTypes.CLOUD, cX, cY, cZ, r.nextGaussian()*magnitude, r.nextGaussian()*magnitude, r.nextGaussian()*magnitude);
		}
		if (boom) {
			mc.world.addParticle(ParticleTypes.EXPLOSION, cX, cY, cZ, 0, 0, 0);
			mc.world.playSound(mc.player, cX, cY, cZ, YSounds.SMALL_EXPLODE, SoundCategory.BLOCKS, 1, r.nextFloat(0.9f, 1.2f));
			mc.world.playSound(mc.player, cX, cY, cZ, SoundEvents.ENTITY_SILVERFISH_DEATH, SoundCategory.BLOCKS, 0.2f, r.nextFloat(0.9f, 1.2f));
			mc.world.playSound(mc.player, cX, cY, cZ, SoundEvents.ENTITY_CREEPER_DEATH, SoundCategory.BLOCKS, 0.8f, r.nextFloat(1.2f, 1.6f));
		}
	}

}
