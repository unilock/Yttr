package com.unascribed.yttr.mixin.diffractor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import net.minecraft.entity.Entity;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(at=@At("HEAD"), method="isInvisible", cancellable=true)
	public void yttr$isInvisible(CallbackInfoReturnable<Boolean> ci) {
		var self = (Entity)(Object)this;
		if (!self.getWorld().isClient && this instanceof DiffractorPlayer dp && dp.yttr$isFullyCloaked()) {
			ci.setReturnValue(true);
		}
	}
	
	@Inject(at=@At("RETURN"), method="squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", cancellable=true)
	public void yttr$squaredDistanceTo(Entity subject, CallbackInfoReturnable<Double> ci) {
		// for mob tracking
		if (subject instanceof DiffractorPlayer dp && dp.yttr$isFullyCloaked()) {
			ci.setReturnValue(ci.getReturnValueD()*ci.getReturnValueD());
		}
	}
	
}
