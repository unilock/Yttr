package com.unascribed.yttr.mixin.coil;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YEnchantments;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;

// ensure our RETURN inject comes first so we don't wind up undoing someone else's break speed change
@Mixin(value=PlayerEntity.class, priority=1500)
public class MixinPlayerEntity {

	@Unique
	private float yttr$storedBreakSpeed;
	
	@Inject(at=@At(value="INVOKE", target="Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z"),
			method="getBlockBreakingSpeed")
	public void storeBreakSpeedBeforeOnGroundCheck(BlockState bs, CallbackInfoReturnable<Float> ci, @Local float breakSpeed) {
		yttr$storedBreakSpeed = breakSpeed;
	}
	
	@ModifyReturnValue(at=@At("RETURN"), method="getBlockBreakingSpeed")
	public float restoreBreakSpeed(float original, BlockState bs) {
        PlayerEntity self = (PlayerEntity)(Object)this;
		if (YEnchantments.STABILIZATION.isPresent() && original == yttr$storedBreakSpeed/5
				&& Yttr.trinketsAccess.count(self, is -> EnchantmentHelper.getLevel(YEnchantments.STABILIZATION.get(), is)) > 0) {
			return original*5;
		}
		return original;
	}

}
