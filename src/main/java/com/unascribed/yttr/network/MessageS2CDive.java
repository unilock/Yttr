package com.unascribed.yttr.network;

import java.util.List;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.lib39.tunnel.api.annotation.field.MarshalledAs;
import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.world.Geyser;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class MessageS2CDive extends S2CMessage {
	
	@MarshalledAs("varint")
	public int x;
	@MarshalledAs("varint")
	public int z;
	public List<Geyser> geysers;
	
	public MessageS2CDive(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDive(int x, int z, List<Geyser> geysers) {
		super(YNetwork.CONTEXT);
		this.x = x;
		this.z = z;
		this.geysers = geysers;
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		mc.setScreen(new SuitScreen(x, z, geysers));
	}

}
