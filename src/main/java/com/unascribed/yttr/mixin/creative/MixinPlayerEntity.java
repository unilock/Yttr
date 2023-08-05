package com.unascribed.yttr.mixin.creative;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mixinsupport.Clippy;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements Clippy {

	private boolean yttr$noclip;
	
	@Inject(at=@At("HEAD"), method="updateWaterSubmersionState")
	protected void yttr$creativeNoclip(CallbackInfoReturnable<Boolean> ci) {
		var self = (PlayerEntity)(Object)this;
		if (yttr$noclip) {
			if (Yttr.isEnlightened(self, true)) {
				self.noClip = true;
				self.setOnGround(false);
				self.getAbilities().flying = true;
			} else {
				yttr$noclip = false;
			}
		}
	}
	
	@Override
	public void yttr$setNoClip(boolean noclip) {
		yttr$noclip = noclip;
	}

	@Override
	public boolean yttr$isNoClip() {
		return yttr$noclip;
	}
	
}
