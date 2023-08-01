package com.unascribed.yttr.content.item;

import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YStatusEffects;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.util.EquipmentSlots;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedCleaverItem extends CleaverItem {

	private final Multimap<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
			.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 8, EntityAttributeModifier.Operation.ADDITION))
			.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2.2, EntityAttributeModifier.Operation.ADDITION))
			.build();
	
	public ReinforcedCleaverItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return !miner.getAbilities().creativeMode || !miner.isSneaking();
	}
	
	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
		return slot == EquipmentSlot.MAINHAND ? modifiers : super.getAttributeModifiers(slot);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		int armorAmount = 0;
		for (ItemStack armor : target.getArmorItems()) {
			if (!armor.isEmpty()) armorAmount++;
		}
		if (!target.getType().isIn(YTags.Entity.BLOODLESS)) {
			StatusEffectInstance cur = target.getStatusEffect(YStatusEffects.BLEEDING);
			int duration = 5*20;
			if (cur != null) {
				duration += cur.getDuration();
			}
			if (duration > 20*20) duration = 20*20;
			if (cur == null || attacker.getWorld().random.nextInt(8)-2 > armorAmount) {
				int level = cur == null ? 0 : Math.min(5, cur.getAmplifier()+1);
				target.addStatusEffect(new StatusEffectInstance(YStatusEffects.BLEEDING, duration, level, false, false, true));
			} else if (attacker.getWorld().random.nextInt(3) == 0) {
				target.addStatusEffect(new StatusEffectInstance(YStatusEffects.BLEEDING, duration, cur.getAmplifier(), false, false, true));
			}
		}
		for (EquipmentSlot es : EquipmentSlots.ARMOR) {
			if (attacker.getWorld().random.nextInt(3) == 0) {
				target.getEquippedStack(es).damage(12, target, (e) -> {
					target.sendEquipmentBreakStatus(es);
					if (attacker instanceof ServerPlayerEntity) {
						YCriteria.BREAK_ARMOR_WITH_CLEAVER.trigger((ServerPlayerEntity)attacker);
					}
				});
			}
		}
		return true;
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		stack.damage(1, miner, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}
	
	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return 4.5f;
	}
	
	@Override
	public boolean isSuitableFor(BlockState state) {
		return true;
	}
	
	@Override
	public int getEnchantability() {
		return 17;
	}
	
	@Override
	public boolean requiresSneaking() {
		return true;
	}
	
}
