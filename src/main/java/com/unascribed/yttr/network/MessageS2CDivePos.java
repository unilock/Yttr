package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDivePos extends S2CMessage {
	
	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int z;
	
	public MessageS2CDivePos(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDivePos(int x, int z) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.z = z;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).setPos(x, z);
		}
	}

}
