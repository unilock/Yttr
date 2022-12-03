package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YEnchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class ShatteringCurseEnchantment extends Enchantment {
	public ShatteringCurseEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.DIGGER, EquipmentSlot.values());
	}

	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.shattering) return 30000;
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.shattering) return -30000;
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
		return super.canAccept(other) && other != Enchantments.SILK_TOUCH && other != YEnchantments.ANNIHILATION_CURSE;
	}
	
}
