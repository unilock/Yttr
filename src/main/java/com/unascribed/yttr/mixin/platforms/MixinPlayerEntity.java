package com.unascribed.yttr.mixin.platforms;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

	@Inject(at=@At("HEAD"), method="clipAtLedge", cancellable=true)
	public void yttr$disableSneakEdgeForPlatforms(CallbackInfoReturnable<Boolean> cir) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (Yttr.isWearingPlatforms(self) && self.isSneaking()) {
			cir.setReturnValue(false);
		}
	}
	
}
