package com.unascribed.yttr.mixin.rifle.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.render.RifleHUDRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	
	@Inject(at=@At("HEAD"), method="renderHand", cancellable=true)
	private void renderHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
		if (RifleHUDRenderer.scopeTime > 2) {
			ci.cancel();
		}
	}
	
}
