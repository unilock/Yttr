package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.effect.BleedingStatusEffect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;

public class YStatusEffects {

	public static final StatusEffect DELICACENESS = new StatusEffect(StatusEffectCategory.BENEFICIAL, 0xA68FE0) {};
	public static final StatusEffect BLEEDING = new BleedingStatusEffect(StatusEffectCategory.HARMFUL, 0xFF0606);
	public static final StatusEffect POTION_SICKNESS = new StatusEffect(StatusEffectCategory.HARMFUL, 0xC8C6E2) {};

	public static void init() {
		Yttr.autoRegister(Registry.STATUS_EFFECT, YStatusEffects.class, StatusEffect.class);
	}

}
