package com.unascribed.yttr.init;

import com.unascribed.lib39.core.api.util.LatchReference;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.enchant.AnnihilationCurseEnchantment;
import com.unascribed.yttr.content.enchant.DisjunctionEnchantment;
import com.unascribed.yttr.content.enchant.ShatteringCurseEnchantment;
import com.unascribed.yttr.content.enchant.SpringingEnchantment;
import com.unascribed.yttr.content.enchant.StabilizationEnchantment;
import com.unascribed.yttr.content.enchant.VorpalEnchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;

public class YEnchantments {

	public static final DisjunctionEnchantment DISJUNCTION = new DisjunctionEnchantment();
	public static final VorpalEnchantment VORPAL = new VorpalEnchantment();
	public static final ShatteringCurseEnchantment SHATTERING_CURSE = new ShatteringCurseEnchantment();
	public static final AnnihilationCurseEnchantment ANNIHILATION_CURSE = new AnnihilationCurseEnchantment();
	
	public static final LatchReference<SpringingEnchantment> SPRINGING = YLatches.create();
	public static final LatchReference<StabilizationEnchantment> STABILIZATION = YLatches.create();
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ENCHANTMENT, YEnchantments.class, Enchantment.class);
	}
	
}
