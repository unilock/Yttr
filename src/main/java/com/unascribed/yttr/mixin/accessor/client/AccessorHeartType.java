package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Mixin(targets="net.minecraft.client.gui.hud.InGameHud$HeartType")
@Environment(EnvType.CLIENT)
public interface AccessorHeartType {

	@Invoker("getU")
	int yttr$getU(boolean halfHeart, boolean blinking);
	
}
