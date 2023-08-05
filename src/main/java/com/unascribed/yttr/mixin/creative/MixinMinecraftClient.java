package com.unascribed.yttr.mixin.creative;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.Clippy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	public boolean chunkCullingEnabled;

	private boolean yttr$wasNoclipping;

	@Inject(at=@At("HEAD"), method="render(Z)V", cancellable=true)
	public void renderPre(boolean tick, CallbackInfo ci) {
		if (player != null && ((Clippy)player).yttr$isNoClip()) {
			yttr$wasNoclipping = true;
			chunkCullingEnabled = false;
		} else if (yttr$wasNoclipping) {
			yttr$wasNoclipping = false;
			chunkCullingEnabled = true;
		}
	}
	
}
