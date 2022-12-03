package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YEnchantments;

import net.minecraft.enchantment.Enchantment;

public class StabilizationEnchantment extends CoilEnchantment {
	public StabilizationEnchantment() {
		super(Rarity.VERY_RARE);
	}

	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.stabilization) return 30000;
		return 15;
	}

	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.stabilization) return -30000;
		return super.getMinPower(level) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return super.canAccept(other) && !YEnchantments.SPRINGING.is(other);
	}
}
