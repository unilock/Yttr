package com.unascribed.yttr.mixin.coil;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(Entity.class)
public class MixinEntity {
	
	@ModifyReturnValue(at=@At("RETURN"), method="getJumpVelocityMultiplier")
	protected float getJumpVelocityMultiplier(float original) {
		Object self = this;
		if (self instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity)self;
			if (p.isSneaking()) return original;
			int level = Yttr.getSpringingLevel(p);
			if (level > 0) {
				return original+(level/4f);
			}
		}
		return original;
	}
	
}
