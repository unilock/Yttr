package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.render.RifleHUDRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void render(GuiGraphics ctx, float tickDelta, CallbackInfo ci) {
		if (RifleHUDRenderer.scopeTime > 0) {
			RifleHUDRenderer.render(ctx, tickDelta);
			ci.cancel();
		}
	}
	
}
