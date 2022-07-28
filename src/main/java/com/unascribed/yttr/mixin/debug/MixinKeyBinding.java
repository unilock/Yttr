package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputUtil;
import com.unascribed.yttr.client.render.ProfilerRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBind;

@Environment(EnvType.CLIENT)
@Mixin(KeyBind.class)
public class MixinKeyBinding {

	@Inject(at=@At("HEAD"), method="onKeyPressed")
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
		ProfilerRenderer.handleKey(key);
	}
	
}
