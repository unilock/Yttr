package com.unascribed.yttr.mixin.creative;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	public boolean noClip;
	
	@Inject(at=@At("HEAD"), method="wouldPoseNotCollide(Lnet/minecraft/entity/EntityPose;)Z", cancellable=true)
	public void wouldPoseNotCollide(EntityPose pose, CallbackInfoReturnable<Boolean> ci) {
		if (noClip) ci.setReturnValue(true);
	}
	
}
