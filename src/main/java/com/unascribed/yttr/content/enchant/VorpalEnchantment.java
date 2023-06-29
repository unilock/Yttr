package com.unascribed.yttr.content.enchant;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YDamageTypes;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixinsupport.ComplexDamageEnchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class VorpalEnchantment extends Enchantment implements ComplexDamageEnchant {

	public VorpalEnchantment() {
		super(Rarity.VERY_RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMinPower(int level) {
		if (!YConfig.Enchantments.vorpal) return 30000;
		return 20 + 10 * (level-1);
	}
	
	@Override
	public int getMaxPower(int level) {
		if (!YConfig.Enchantments.vorpal) return -30000;
		return getMinPower(level)+50;
	}
	
	@Override
	public boolean isTreasure() {
		return true;
	}
	
	@Override
	public int getMaxLevel() {
		return 4;
	}
	
	@Override
	public AttackResult handleAttack(int level, Entity target, @Nullable Entity attacker) {
		if (ThreadLocalRandom.current().nextInt(150) < level*level) {
			if (attacker instanceof ServerPlayerEntity) {
				Box b = target.getBoundingBox();
				Vec3d c = b.getCenter();
				ParticleS2CPacket pkt = new ParticleS2CPacket(ParticleTypes.INSTANT_EFFECT, false, c.x, c.y, c.z,
						(float)b.getXLength()/2, (float)b.getYLength()/2, (float)b.getZLength()/2,
						0, 20);
				((ServerPlayerEntity)attacker).networkHandler.sendPacket(pkt);
				SoundCategory cat = attacker.getSoundCategory();
				double x = target.getPos().x;
				double y = target.getPos().y;
				double z = target.getPos().z;
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT1, cat, 0.5f, 0.5f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT1, cat, 0.5f, 0.5f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT1, cat, 0.5f, 0.7f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT1, cat, 0.5f, 0.9f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT2, cat, 0.5f, 1.5f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT2, cat, 0.5f, 1.5f);
				attacker.getWorld().playSound(null, x, y, z, YSounds.VORPALHIT2, cat, 0.5f, 1.5f);
				YCriteria.VORPAL_HIT.trigger(((ServerPlayerEntity)attacker));
			}
			return new AttackResult(attacker.getDamageSources().create(YDamageTypes.VORPAL, attacker), 100);
		}
		return null;
	}
	
}
