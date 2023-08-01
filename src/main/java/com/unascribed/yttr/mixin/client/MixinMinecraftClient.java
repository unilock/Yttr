package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.compat.modmenu.YttrConfigScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.MusicSound;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	public Screen currentScreen;
	
	@Inject(at=@At("HEAD"), method="getMusic", cancellable=true)
	public void getMusic(CallbackInfoReturnable<MusicSound> ci) {
		if (currentScreen instanceof YttrConfigScreen) {
			ci.setReturnValue(YttrConfigScreen.MUSIC);
		}
	}
	
}
