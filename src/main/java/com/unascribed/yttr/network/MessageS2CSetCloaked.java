package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.util.YRandom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance.AttenuationType;

public class MessageS2CSetCloaked extends S2CMessage {

	@MarshalledAs("varint")
	public int entityId;
	@MarshalledAs("varint")
	public int cloakTime;
	public boolean cloaked;
	
	public MessageS2CSetCloaked(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CSetCloaked(int entityId, int cloakTime, boolean cloaked) {
		super(YNetwork.CONTEXT);
		this.entityId = entityId;
		this.cloakTime = cloakTime;
		this.cloaked = cloaked;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.world.getEntityById(entityId) instanceof DiffractorPlayer dp) {
			dp.yttr$setCloaked(cloaked);
			dp.yttr$setCloakTime(cloakTime);
			if (player == mc.player && !cloaked) {
				mc.getSoundManager().play(new PositionedSoundInstance(YSounds.UNCLOAK.getId(), mc.player.getSoundCategory(), 1, 1, YRandom.get(), false, 0, AttenuationType.NONE, 0, 0, 0, true));
			}
		}
	}

}
