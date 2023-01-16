package com.unascribed.yttr.mixinsupport;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public interface ComplexDamageEnchant {

	public record AttackResult(@Nullable DamageSource src, float amount) {}
	
	@Nullable AttackResult handleAttack(int level, Entity target, @Nullable Entity attacker);
	
}
