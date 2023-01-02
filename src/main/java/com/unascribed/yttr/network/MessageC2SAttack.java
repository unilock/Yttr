package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.C2SMessage;
import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.yttr.init.YNetwork;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @deprecated No longer used, kept to avoid changing message IDs and breaking compat
 */
@Deprecated
public class MessageC2SAttack extends C2SMessage {

	public MessageC2SAttack(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageC2SAttack() {
		super(YNetwork.CONTEXT);
	}

	@Override
	protected void handle(ServerPlayerEntity player) {
	}

}
