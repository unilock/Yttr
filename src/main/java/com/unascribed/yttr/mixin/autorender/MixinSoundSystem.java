package com.unascribed.yttr.mixin.autorender;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.lib39.core.mixinsupport.AutoMixinEligible;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundSystem;

@Environment(EnvType.CLIENT)
@Mixin(SoundSystem.class)
@AutoMixinEligible(ifSystemProperty="yttr.render")
public class MixinSoundSystem {

	@Inject(at=@At("HEAD"), method="start", cancellable=true)
	private void start(CallbackInfo ci) {
		ci.cancel();
	}
	
}
