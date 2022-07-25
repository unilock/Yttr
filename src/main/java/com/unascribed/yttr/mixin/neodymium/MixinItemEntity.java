package com.unascribed.yttr.mixin.neodymium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.Magnetized;

import com.google.common.hash.Hashing;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(ItemEntity.class)
public class MixinItemEntity {

	@Inject(at=@At("HEAD"), method="onPlayerCollision", cancellable=true)
	public void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
		if (this instanceof Magnetized m && (m.yttr$isMagnetizedBelow() || m.yttr$isMagnetizedAbove())) {
			ItemEntity self = (ItemEntity)(Object)this;
			if (player.squaredDistanceTo(self) > 0.25*0.25) {
				ci.cancel();
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="getRotation", cancellable=true)
	public void getRotation(float tickDelta, CallbackInfoReturnable<Float> ci) {
		if (this instanceof Magnetized m && (m.yttr$isMagnetizedBelow() != m.yttr$isMagnetizedAbove())) {
			ItemEntity self = (ItemEntity)(Object)this;
			ci.setReturnValue((Hashing.murmur3_32_fixed().hashInt(self.getId()).asInt()/9000f)%6);
		}
	}
	
}
