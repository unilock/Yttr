package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.init.YNetwork;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CSoulImpurity extends S2CMessage {

	@MarshalledAs("varint")
	public int impurity;
	
	public MessageS2CSoulImpurity(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CSoulImpurity(int impurity) {
		super(YNetwork.CONTEXT);
		this.impurity = impurity;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		YttrClient.soulImpurity = impurity;
	}

}
