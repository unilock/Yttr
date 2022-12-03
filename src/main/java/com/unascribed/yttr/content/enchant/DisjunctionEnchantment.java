package com.unascribed.yttr.content.enchant;

import com.unascribed.yttr.YConfig;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class DisjunctionEnchantment extends DamageEnchantment {

	public static final EntityGroup ENDER = new EntityGroup();
	
	public DisjunctionEnchantment() {
		super(Rarity.UNCOMMON, 1, EquipmentSlot.MAINHAND);
	}

	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.disjunction) return 30000;
		return super.getMinPower(level);
	}

	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.disjunction) return -30000;
		return super.getMaxPower(level);
	}
	
	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		if (group == ENDER) {
			return level * 3.5f;
		}
		return 0;
	}

}
