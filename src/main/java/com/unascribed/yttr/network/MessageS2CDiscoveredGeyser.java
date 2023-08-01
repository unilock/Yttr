package com.unascribed.yttr.network;

import com.unascribed.lib39.tunnel.api.NetworkContext;
import com.unascribed.lib39.tunnel.api.S2CMessage;
import com.unascribed.yttr.client.screen.SuitScreen;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YNetwork;
import com.unascribed.yttr.world.Geyser;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.util.math.MatrixStack;

public class MessageS2CDiscoveredGeyser extends S2CMessage {
	
	public Geyser geyser;
	
	public MessageS2CDiscoveredGeyser(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageS2CDiscoveredGeyser(Geyser geyser) {
		super(YNetwork.CONTEXT);
		this.geyser = geyser;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	protected void handle(MinecraftClient mc, ClientPlayerEntity player) {
		if (mc.currentScreen instanceof SuitScreen) {
			((SuitScreen)mc.currentScreen).addGeyser(geyser);
		} else {
			String name = geyser.name;
			mc.getToastManager().add((ctx, manager, startTime) -> {
				MatrixStack matrices = ctx.getMatrices();

				ctx.drawTexture(Toast.TEXTURE, 0, 0, 0, 0, 160, 32);
				ctx.drawTexture(SuitRenderer.SUIT_TEX, 4, 4, 23, 18, 12, 12, SuitRenderer.SUIT_TEX_WIDTH, SuitRenderer.SUIT_TEX_HEIGHT);
				ctx.drawText(manager.getGame().textRenderer, "§l"+I18n.translate("yttr.geyser_discovered"), 30, 7, -1, false);
				ctx.drawText(manager.getGame().textRenderer, name, 30, 18, -1, false);
				return startTime >= 5000 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
			});
		}
	}

}
