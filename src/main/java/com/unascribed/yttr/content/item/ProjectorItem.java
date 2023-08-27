package com.unascribed.yttr.content.item;

import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.DelayedTask;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.decor.ContinuousPlatformBlock;
import com.unascribed.yttr.content.block.decor.ContinuousPlatformBlock.Age;
import com.unascribed.yttr.content.block.decor.ContinuousPlatformBlock.PlatformLog;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.util.AdventureHelper;
import com.unascribed.yttr.util.ControlHintable;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class ProjectorItem extends Item implements ControlHintable {

	public ProjectorItem(Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, (ptr, stack) -> {
			World w = ptr.getWorld();
			BlockPos.Mutable mut = ptr.getPos().mutableCopy();
			Direction face = ptr.getBlockState().get(DispenserBlock.FACING);
			mut.move(face, face.getAxis() == Axis.Y ? 1 : 2);
			int i = 0;
			while (canReplace(w.getBlockState(mut))) {
				if (i > 64) break;
				BlockPos pos = mut.toImmutable();
				Yttr.delayedServerTasks.add(new DelayedTask(i*2, () -> {
					createPlatform(null, w, pos, false);
					w.playSound(null, pos, YSounds.PROJECT, SoundCategory.BLOCKS, 1.2f, 0.5f+(ThreadLocalRandom.current().nextFloat()/2));
				}));
				mut.move(face);
				i++;
			}
			return stack;
		});
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (stack.hasNbt()) stack.getNbt().remove("LastBlock");
		user.setCurrentHand(hand);
		if (user instanceof ServerPlayerEntity) {
			YCriteria.PROJECT.trigger((ServerPlayerEntity)user);
		}
		return TypedActionResult.consume(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState bs = world.getBlockState(pos);
		if (bs.isOf(YBlocks.CONTINUOUS_PLATFORM)) {
			if (bs.get(ContinuousPlatformBlock.AGE) != Age.IMMORTAL) {
				if (!world.isClient) {
					world.setBlockState(pos, bs.with(ContinuousPlatformBlock.AGE, Age.IMMORTAL).with(ContinuousPlatformBlock.SPEEDY, false));
					if (world instanceof ServerWorld) {
						((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 14, 0.5, 0.5, 0.5, 0.05);
					}
					world.playSound(null, pos, YSounds.PROJECT, SoundCategory.PLAYERS, 1, 1.5f+(ThreadLocalRandom.current().nextFloat()/2));
				}
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAIL;
			}
		}
		return ActionResult.PASS;
	}
	
	protected void createPlatform(@Nullable PlayerEntity player, World world, BlockPos origin, boolean speedy) {
		BlockPos.Mutable pos = origin.mutableCopy();
		if (world instanceof ServerWorld) {
			((ServerWorld)world).spawnParticles(ParticleTypes.CRIT, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 1.5, 0.5, 1.5, 0.05);
			((ServerWorld)world).spawnParticles(ParticleTypes.FIREWORK, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 1.5, 0.5, 1.5, 0.05);
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				pos.set(origin).move(x, 0, z);
				BlockState bs = world.getBlockState(pos);
				// check isBlockBreakingRestricted for compatibility with warding/protection mods
				// we have our own adventure handling, so always check survival
				if (canReplace(bs) && (player == null || !player.isBlockBreakingRestricted(world, pos, GameMode.SURVIVAL))) {
					world.setBlockState(pos, YBlocks.CONTINUOUS_PLATFORM.getDefaultState()
							.with(ContinuousPlatformBlock.LOGGED, PlatformLog.by(bs))
							.with(ContinuousPlatformBlock.SPEEDY, speedy));
				}
			}
		}
	}

	private boolean canReplace(BlockState bs) {
		return bs.isAir() || bs.materialReplaceable() || (bs.isOf(YBlocks.CONTINUOUS_PLATFORM) && bs.get(ContinuousPlatformBlock.AGE) != Age.IMMORTAL);
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 200;
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (world.isClient) return;
		if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
		int ticks = getMaxUseTime(stack)-remainingUseTicks;
		if (ticks != 0 && ticks < 10) return;
		if (ticks > 0) ticks -= 10;
		BlockPos lastPos = stack.getNbt().contains("LastBlock") ? NbtHelper.toBlockPos(stack.getNbt().getCompound("LastBlock")) : null;
		BlockPos pos = BlockPos.fromPosition(user.getPos().subtract(0, 1, 0).add(user.getRotationVector().multiply(ticks/2f, ticks/3f, ticks/2f)));
		if (lastPos != null) {
			double len = Math.sqrt(lastPos.getSquaredDistance(pos));
			double diffX = pos.getX()-lastPos.getX();
			double diffY = pos.getY()-lastPos.getY();
			double diffZ = pos.getZ()-lastPos.getZ();
			BlockPos.Mutable mut = new BlockPos.Mutable();
			int count = (int)(len*2);
			for (int i = 0; i < count; i++) {
				double t = (i/(double)count);
				double x = lastPos.getX()+(diffX*t);
				double y = lastPos.getY()+(diffY*t);
				double z = lastPos.getZ()+(diffZ*t);
				mut.set(x, y, z);
				if (AdventureHelper.canUse(user, stack, world, mut)) {
					createPlatform(user instanceof PlayerEntity pe ? pe : null, world, mut, true);
				}
			}
		} else {
			if (AdventureHelper.canUse(user, stack, world, pos)) {
				createPlatform(user instanceof PlayerEntity pe ? pe : null, world, pos, false);
			}
		}
		stack.getNbt().put("LastBlock", NbtHelper.fromBlockPos(pos));
		if (ticks == 0) {
			if (user.fallDistance > 20 && user instanceof ServerPlayerEntity spe) {
				YCriteria.PROJECT_WITH_LONG_FALL.trigger(spe);
			}
			user.fallDistance = 0;
			if (user.getPos().y < pos.getY()+1) {
				user.teleport(user.getPos().x, pos.getY()+1, user.getPos().z);
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.PROJECT, SoundCategory.PLAYERS, 0.75f, 1f+(ThreadLocalRandom.current().nextFloat()/2));
			world.playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.PROJECT, SoundCategory.PLAYERS, 0.75f, 1.5f+(ThreadLocalRandom.current().nextFloat()/2));
		}
		if (ticks % 2 == 0) {
			world.playSound(null, pos, YSounds.PROJECT, SoundCategory.PLAYERS, 1.2f, 0.5f+(ThreadLocalRandom.current().nextFloat()/2));
		}
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		if (user instanceof PlayerEntity) {
			((PlayerEntity)user).getItemCooldownManager().set(this, 250);
		}
		return stack;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int ticks = getMaxUseTime(stack)-remainingUseTicks;
		if (ticks > 20) {
			if (user instanceof PlayerEntity) {
				((PlayerEntity)user).getItemCooldownManager().set(this, 250-remainingUseTicks);
			}
		}
	}
	
	@Override
	public String getState(PlayerEntity player, ItemStack stack, boolean fHeld) {
		if (player.isUsingItem() && player.getItemUseTime() > 20) return "using";
		if (player.canModifyBlocks() || stack.canPlaceOn(Registries.BLOCK, new CachedBlockPosition(player.getWorld(), player.getBlockPos(), false) {
			@Override
			public BlockState getBlockState() {
				return YBlocks.CONTINUOUS_PLATFORM.getDefaultState();
			}
			@Override
			public BlockEntity getBlockEntity() {
				return null;
			}
		})) return "normal";
		return "limited";
	}
	
}
