package com.unascribed.yttr.mixin.diffractor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Inject(at=@At("HEAD"), method="getArmorVisibility", cancellable=true)
	public void yttr$getArmorVisibility(CallbackInfoReturnable<Float> ci) {
		if (this instanceof DiffractorPlayer dp && dp.yttr$isCloaked()) {
			ci.setReturnValue(0f);
		}
	}

	@Inject(at=@At("HEAD"), method="getAttackDistanceScalingFactor", cancellable=true)
	public void yttr$getAttackDistanceScalingFactor(CallbackInfoReturnable<Double> ci) {
		if (this instanceof DiffractorPlayer dp && dp.yttr$isFullyCloaked()) {
			ci.setReturnValue(0D);
		}
	}
	
}
