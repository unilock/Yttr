package com.unascribed.yttr.mixin.effector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YCriteria;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(at=@At("HEAD"), method="onDeath")
	public void onDeath(DamageSource source, CallbackInfo ci) {
		if (source.getName().equals("yttr.effector_fall")) {
			if (source.getAttacker() instanceof ServerPlayerEntity) {
				YCriteria.KILL_WITH_EFFECTOR.trigger((ServerPlayerEntity)source.getAttacker());
			}
		}
	}
	
}
