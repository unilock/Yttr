package com.unascribed.yttr.content.enchant;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixinsupport.ComplexDamageEnchant;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;

public class DisjunctionEnchantment extends DamageEnchantment implements ComplexDamageEnchant {

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
	public AttackResult handleAttack(int level, Entity target, @Nullable Entity attacker) {
		if (target.getType().isIn(YTags.Entity.DISJUNCTIBLE)) {
			return new AttackResult(null, level * 3.5f);
		}
		return null;
	}
	
	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		return 0;
	}

}
