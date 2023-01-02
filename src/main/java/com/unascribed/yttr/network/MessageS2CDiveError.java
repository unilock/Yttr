package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDiveError extends S2CMessage {
	
	public String msg;
	
	public MessageS2CDiveError(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDiveError(String msg) {
		super(YNetwork.CONTEXT);
		this.msg = msg;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).showError(msg);
		}
	}

}
