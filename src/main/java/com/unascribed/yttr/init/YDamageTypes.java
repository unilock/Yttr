package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class YDamageTypes {
	public static final RegistryKey<DamageType> BLEEDING = key("bleeding");
	public static final RegistryKey<DamageType> BLOQUE = key("bloque");
	public static final RegistryKey<DamageType> EFFECTOR_FALL = key("effector_fall");
	public static final RegistryKey<DamageType> LAZOR = key("lazor");
	public static final RegistryKey<DamageType> MAGNET = key("magnet");
	public static final RegistryKey<DamageType> OVERCHARGE = key("overcharge");
	public static final RegistryKey<DamageType> RIFLE = key("rifle");
	public static final RegistryKey<DamageType> SCISSORS = key("scissors");
	public static final RegistryKey<DamageType> SOLVENT = key("solvent");
	public static final RegistryKey<DamageType> SUIT_INTEGRITY_FAILURE = key("suit_integrity_failure");
	public static final RegistryKey<DamageType> SUIT_SUFFOCATION = key("suit_suffocation");
	public static final RegistryKey<DamageType> VOID_RIFLE = key("void_rifle");
	public static final RegistryKey<DamageType> VORPAL = key("vorpal");

	private static RegistryKey<DamageType> key(String name) {
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Yttr.id(name));
	}

	public static void init() {
	}
}
