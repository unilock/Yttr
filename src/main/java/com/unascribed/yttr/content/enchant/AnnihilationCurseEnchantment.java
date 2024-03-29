package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YEnchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class AnnihilationCurseEnchantment extends Enchantment {
	public AnnihilationCurseEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.DIGGER, EquipmentSlot.values());
	}

	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.annihilation) return 30000;
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.annihilation) return -30000;
		return 40;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
	
	@Override
	public boolean isTreasure() {
		return !YConfig.Enchantments.cursesInTable;
	}
	
	@Override
	protected boolean canAccept(Enchantment other) {
		return super.canAccept(other) && other != Enchantments.SILK_TOUCH && other != Enchantments.FORTUNE && other != YEnchantments.SHATTERING_CURSE;
	}
	
}
