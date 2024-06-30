package com.unascribed.yttr.mixin.diffractor.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.DiffractorClientLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Source;


@Mixin(Source.class)
@Environment(EnvType.CLIENT)
public class MixinSource {

	@Shadow @Final
	private int pointer;
	
	@Inject(at=@At("HEAD"), method="play")
	public void yttr$play(CallbackInfo ci) {
		DiffractorClientLogic.tweakAlSource(pointer);
	}
	
}

