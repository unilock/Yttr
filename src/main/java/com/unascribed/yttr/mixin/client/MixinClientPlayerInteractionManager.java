package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
@Environment(EnvType.CLIENT)
public class MixinClientPlayerInteractionManager {

	@Inject(at=@At("HEAD"), method="hasCreativeInventory", cancellable=true)
	public void yttr$preferSurvivalInventory(CallbackInfoReturnable<Boolean> ci) {
		if (Yttr.prefersSurvivalInventory(MinecraftClient.getInstance().player)) {
			ci.setReturnValue(false);
		}
	}
	
}
