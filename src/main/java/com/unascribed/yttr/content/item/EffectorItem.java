package com.unascribed.yttr.content.item;

import java.util.Arrays;
import java.util.List;

import com.unascribed.yttr.init.*;
import net.minecraft.entity.damage.DamageSource;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.network.MessageS2CEffectorHole;
import com.unascribed.yttr.util.AdventureHelper;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;

public class EffectorItem extends Item {

	public static final int MAX_FUEL = 2048;
	
	public EffectorItem(Settings settings) {
		super(settings);
		DispenserBlock.registerBehavior(this, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				var world = pointer.getWorld();
				var dir = pointer.getBlockState().get(DispenserBlock.FACING);
				var pos = pointer.getPos().offset(dir, 3);
				boolean infiniteFuel = stack.hasNbt() && stack.getNbt().getBoolean("InfiniteFuel");
				int fuel = infiniteFuel ? MAX_FUEL : getFuel(stack);
				if (fuel <= 0) {
					return stack;
				}
				int amt = effect(world, pos, dir, stack, null, Math.min(fuel, 32), true);
				if (!infiniteFuel) setFuel(stack, fuel-amt);
				YNetwork.sendPacketToPlayersWatching(world, pos, new MessageS2CEffectorHole(pos, dir, amt).toClientboundVanillaPacket());
				return stack;
			}
		});
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (!user.canModifyBlocks()) return TypedActionResult.pass(stack);
		if (getFuel(stack) >= MAX_FUEL) return TypedActionResult.pass(stack);
		BlockHitResult hr = raycast(world, user, FluidHandling.SOURCE_ONLY);
		if (hr.getType() == Type.BLOCK) {
			BlockState bs = world.getBlockState(hr.getBlockPos());
			if (bs.getBlock() instanceof FluidDrainable && bs.getFluidState().getFluid().isIn(YTags.Fluid.VOID)) {
				ItemStack fuckingGodDamnItMojangYOUBROKEYOUROWNAPI = ((FluidDrainable)bs.getBlock()).tryDrainFluid(world, hr.getBlockPos(), bs);
				if (fuckingGodDamnItMojangYOUBROKEYOUROWNAPI.getItem() == YItems.VOID_BUCKET) {
					user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1, 1);
					if (world.isClient) return TypedActionResult.success(stack, true);
					setFuel(stack, MAX_FUEL);
					return TypedActionResult.success(stack, false);
				}
			}
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() == null) return ActionResult.PASS;
		World world = context.getWorld();
		if (context.getPlayer().canModifyBlocks()) {
			BlockHitResult hr = raycast(world, context.getPlayer(), FluidHandling.SOURCE_ONLY);
			if (hr.getType() == Type.BLOCK) {
				FluidState fs = world.getFluidState(hr.getBlockPos());
				if (fs.isIn(YTags.Fluid.VOID) && fs.isSource()) return ActionResult.PASS;
			}
		}
		if (!(world instanceof ServerWorld)) return ActionResult.SUCCESS;
		if (!AdventureHelper.canUse(context.getPlayer(), context.getStack(), world, context.getBlockPos())) return ActionResult.FAIL;
		BlockPos pos = context.getBlockPos();
		Direction dir = context.getSide().getOpposite();
		ItemStack stack = context.getStack();
		boolean infiniteFuel = context.getPlayer().getAbilities().creativeMode || (stack.hasNbt() && stack.getNbt().getBoolean("InfiniteFuel"));
		int fuel = infiniteFuel ? MAX_FUEL : getFuel(stack);
		if (fuel <= 0) {
			context.getPlayer().sendMessage(Text.translatable("tip.yttr.effector.no_fuel"), true);
			return ActionResult.FAIL;
		}
		int amt = effect(world, pos, dir, stack, context.getPlayer(), Math.min(fuel, 32), true);
		YStats.add(context.getPlayer(), YStats.BLOCKS_EFFECTED, amt*100);
		if (context.getPlayer() instanceof ServerPlayerEntity) {
			YCriteria.EFFECT_BLOCK.trigger((ServerPlayerEntity)context.getPlayer(), pos, stack);
		}
		if (!infiniteFuel) setFuel(stack, fuel-amt);
		new MessageS2CEffectorHole(pos, dir, amt).sendToAllWatching(context.getPlayer());
		return ActionResult.SUCCESS;
	}
	
	@Override
	public boolean onClickedOnOther(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		if (slot.getStack().isOf(YItems.VOID_BUCKET)) {
			slot.setStack(new ItemStack(Items.BUCKET));
			player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1, 1);
			setFuel(stack, MAX_FUEL);
			return true;
		}
		return false;
	}
	
	public int getFuel(ItemStack stack) {
		return stack.hasNbt() ? stack.getNbt().getInt("Fuel") : 0;
	}
	
	public void setFuel(ItemStack stack, int fuel) {
		if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
		stack.getNbt().putInt("Fuel", fuel);
	}

	public interface RenderUpdateCallback {
		void scheduleRenderUpdate(int x, int y, int z);
	}
	
	public static int effect(World world, BlockPos pos, Direction dir, @Nullable ItemStack stack, @Nullable PlayerEntity owner, int distance, boolean server) {
		BlockPos.Mutable cursor = pos.mutableCopy();
		BlockPos.Mutable outerCursor = new BlockPos.Mutable();
		Axis axisZ = dir.getAxis();
		List<Axis> axes = Arrays.asList(Direction.Axis.values());
		Axis axisX = Iterables.find(axes, a -> a != axisZ);
		Axis axisY = Iterables.find(Lists.reverse(axes), a -> a != axisZ);
		int hits = -2;
		for (int z = -2; z < distance; z++) {
			cursor.set(pos).move(dir, z);
			boolean everythingWasUnpassable = true;
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					outerCursor.set(cursor);
					move(outerCursor, axisX, x);
					move(outerCursor, axisY, y);
					BlockState bs = world.getBlockState(outerCursor);
					if (bs.getHardness(world, outerCursor) < 0) continue;
					if (!bs.isAir()) everythingWasUnpassable = false;
					world.phaseBlock(outerCursor, 150, 0, owner == null ? null : new EffectorDamageSource(world, owner));
				}
			}
			if (z >= 0 && server && everythingWasUnpassable) {
				break;
			}
			hits++;
		}
		return hits;
	}

	public static void move(BlockPos.Mutable mut, Axis axis, int distance) {
		if (distance != 0) {
			int x = axis == Axis.X ? distance : 0;
			int y = axis == Axis.Y ? distance : 0;
			int z = axis == Axis.Z ? distance : 0;
			mut.move(x, y, z);
		}
	}
	
	public static class EffectorDamageSource extends DamageSource {

		public EffectorDamageSource(World world, @Nullable Entity source) {
			super(world.getDamageSources().registry.getHolderOrThrow(YDamageTypes.EFFECTOR_FALL), source);
		}
		
		@Override
		public Text getDeathMessage(LivingEntity entity) {
			// no .item support
			String string = "death.attack." + this.getName();
			return Text.translatable(string, entity.getDisplayName(), this.getSource().getDisplayName());
		}
	}

}
