package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.C2SMessage;
import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.mixinsupport.Clippy;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class MessageC2SCreativeNoClip extends C2SMessage {

	public boolean noclip;
	
	public MessageC2SCreativeNoClip(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SCreativeNoClip(boolean noclip) {
		super(YNetwork.CONTEXT);
		this.noclip = noclip;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		if (Yttr.isEnlightened(player, false)) {
			player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS,
					1, noclip?1.65f:1.05f);
			((Clippy)player).yttr$setNoClip(noclip);
		}
	}

}
