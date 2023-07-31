package com.unascribed.yttr.content.item;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class DropOfContinuityItem extends Item {

	public DropOfContinuityItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 170;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isCreative()) {
			finishUsing(user.getStackInHand(hand), world, user);
		} else {
			user.setCurrentHand(hand);
			if (!world.isClient) {
				world.playSoundFromEntity(null, user, YSounds.DROP_CAST, SoundCategory.PLAYERS, 1, 1);
			}
		}
		return TypedActionResult.success(user.getStackInHand(hand));
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		super.usageTick(world, user, stack, remainingUseTicks);
		if (world instanceof ServerWorld) {
			Box box = user.getBoundingBox();
			Vec3d center = box.getCenter();
			if (ThreadLocalRandom.current().nextInt(remainingUseTicks) < 40) {
				int m = 1;
				if (remainingUseTicks < 20) {
					m = 4;
				} else if (remainingUseTicks < 40) {
					m = 2;
				}
				((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, center.x, center.y, center.z, 1*m, box.getXLength()/3, box.getYLength()/3, box.getZLength()/3, 0.05);
				float f = ThreadLocalRandom.current().nextFloat()/2;
				((ServerWorld)world).spawnParticles(new DustParticleEffect(new Vector3f(1, 0.75f-f, 0.5f), 0.5f), center.x, center.y, center.z, 3*m, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0);
			}
		}
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		super.onStoppedUsing(stack, world, user, remainingUseTicks);
		if (!world.isClient) {
			world.playSoundFromEntity(null, user, YSounds.DROP_CAST_CANCEL, SoundCategory.PLAYERS, 1, 1);
			if ((getMaxUseTime(stack)-remainingUseTicks) > 15) {
				world.playSoundFromEntity(null, user, YSounds.DROP_CAST_CANCEL_AUDIBLE, SoundCategory.PLAYERS, 1, 1);
			}
		}
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (!world.isClient) {
			Set<Item> possibilities = getPossibilities();
			ItemStack gift = new ItemStack(Iterables.get(possibilities, ThreadLocalRandom.current().nextInt(possibilities.size())));
			gift.setCount(Math.min(gift.getMaxCount(), ThreadLocalRandom.current().nextInt(3)+1));
			if (user.isUsingItem()) {
				user.setStackInHand(user.getActiveHand(), gift);
			} else if (user instanceof PlayerEntity && ((PlayerEntity) user).isCreative()) {
				((PlayerEntity) user).getInventory().offerOrDrop(gift);
			} else {
				user.dropStack(gift);
				stack.setCount(0);
			}
			if (user instanceof ServerPlayerEntity) {
				YCriteria.BURN_DROP_OF_CONTINUITY.trigger((ServerPlayerEntity)user);
			}
		}
		if (world instanceof ServerWorld) {
			Box box = user.getBoundingBox();
			Vec3d center = box.getCenter();
			((ServerWorld)world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), center.x, center.y, center.z, 30, 0.25, 0.25, 0.25, 0.05);
			((ServerWorld)world).spawnParticles(new DustParticleEffect(new Vector3f(1, 0.75f, 0.5f), 0.5f), center.x, center.y, center.z, 10, box.getXLength()/2, box.getYLength()/2, box.getZLength()/2, 0.0125);
			ThreadLocalRandom r = ThreadLocalRandom.current();
			for (int i = 0; i < 50; i++) {
				((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, center.x, center.y, center.z, 0, r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.25);
				((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, center.x, center.y, center.z, 0, r.nextGaussian(), r.nextGaussian(), r.nextGaussian(), 0.25);
			}
		}
		return super.finishUsing(stack, world, user);
	}
	
	public static Set<Item> getPossibilities() {
		Set<Item> possibilities = Sets.newHashSet();
		Registries.ITEM.getTag(YTags.Item.GIFTS).get().stream()
			.map(re -> re.value())
			.forEach(possibilities::add);
		Registries.BLOCK.getTag(YTags.Block.GIFTS).get().stream()
			.map(re -> re.value().asItem())
			.forEach(possibilities::add);
		// the gifts tag used to include all the fabric tool tags but those are gone now
		Registries.ITEM.getEntries().stream()
			.map(Map.Entry::getValue)
			.filter(i -> i instanceof ToolItem)
			.forEach(possibilities::add);
		Registries.ITEM.getTag(YTags.Item.NOT_GIFTS).get().stream()
			.map(re -> re.value())
			.forEach(possibilities::remove);
		possibilities.remove(null);
		possibilities.remove(Items.AIR);
		return possibilities;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.BLOCK;
	}
	
	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}
	
}
