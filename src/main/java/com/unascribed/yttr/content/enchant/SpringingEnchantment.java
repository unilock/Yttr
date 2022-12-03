package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.YConfig;

public class SpringingEnchantment extends CoilEnchantment {

	public SpringingEnchantment() {
		super(Rarity.RARE);
	}

	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.springing) return 30000;
		return 10 * level;
	}

	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.springing) return -30000;
		return getMinPower(level) + 30;
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}
	
	@Override
	public boolean isTreasure() {
		return !YConfig.Enchantments.cursesInTable;
	}

}
