package com.unascribed.yttr.mixin.diffractor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.DiffractorPlayer;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {

	@Shadow @Final
	private Entity entity;
	
	@Inject(at=@At("HEAD"), method="tick", cancellable=true)
	public void tick(CallbackInfo ci) {
		if (entity instanceof DiffractorPlayer dp && dp.yttr$isFullyCloaked()) {
			ci.cancel();
		}
	}

}
