package com.unascribed.yttr.content.item;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.content.entity.ThrownGlowingGasEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GlowingGasItem extends Item {

	public GlowingGasItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		if (!user.canModifyBlocks()) return TypedActionResult.pass(itemStack);
		var r = ThreadLocalRandom.current();
		world.playSound(null,
			user.getX(), user.getY(), user.getZ(),
			SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL,
			0.5f, r.nextFloat(0.3f, 1)
		);
		if (!world.isClient) {
			var ent = new ThrownGlowingGasEntity(world, user);
			ent.setItem(itemStack);
			ent.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);
			world.spawnEntity(ent);
		}

		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}

		return TypedActionResult.success(itemStack, world.isClient());
	}

}
