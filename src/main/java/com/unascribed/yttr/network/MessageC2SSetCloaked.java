package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.C2SMessage;
import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.util.YLog;

import net.minecraft.server.network.ServerPlayerEntity;

public class MessageC2SSetCloaked extends C2SMessage {

	public boolean cloaked;
	
	public MessageC2SSetCloaked(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SSetCloaked(boolean cloaked) {
		super(YNetwork.CONTEXT);
		this.cloaked = cloaked;
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
		var slot = Yttr.trinketsAccess.getWorn(player, YItems.DIFFRACTOR::is);
		if (slot.isPresent()) {
			if (!player.getItemCooldownManager().isCoolingDown(YItems.DIFFRACTOR.get())) {
				((DiffractorPlayer)player).yttr$setCloaked(cloaked);
			} else if (cloaked) {
				YLog.warn("Player {} tried to cloak while their diffractor is on cooldown", player.getEntityName());
			}
		} else {
			YLog.warn("Player {} tried to cloak without a diffractor equipped", player.getEntityName());
		}
	}

}
