package com.unascribed.yttr.content.item.block;

import java.util.List;

import com.unascribed.yttr.content.block.decor.BloqueBlockEntity;
import com.unascribed.yttr.init.YStatusEffects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BloqueBlockItem extends DyedBlockItem {

	public BloqueBlockItem(Block block, DyeColor color, Settings settings) {
		super(block, color, settings);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		int i = 1;
		while (I18n.hasTranslation("block.yttr.bloque.tip."+i)) {
			tooltip.add(Text.translatable("block.yttr.bloque.tip."+i));
			i++;
		}
	}
	
	@Override
	public ItemPlacementContext getPlacementContext(ItemPlacementContext context) {
		return context;
	}
	
	@Override
	protected boolean place(ItemPlacementContext context, BlockState state) {
		if (context.getStack().getSubNbt("BlockEntityTag") != null) {
			return super.place(context, state);
		}
		BlockPos bp = BlockPos.fromPosition(context.getHitPos());
		if (context.getWorld().getBlockEntity(bp) instanceof BloqueBlockEntity be && fillIn(be, context.getHitPos(), bp, context.getSide())) {
			return true;
		} else {
			if (super.place(context, state) && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof BloqueBlockEntity be) {
				fillIn(be, context.getHitPos().add(new Vec3d(context.getSide().getUnitVector()).multiply(0.2)), context.getBlockPos(), context.getSide());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 48;
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (remainingUseTicks % 5 == 0) {
			user.playSound(SoundEvents.BLOCK_CALCITE_HIT, 1, 1);
		}
		super.usageTick(world, user, stack, remainingUseTicks);
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		user.playSound(SoundEvents.BLOCK_CALCITE_BREAK, 1, 1);
		user.playSound(SoundEvents.BLOCK_CALCITE_BREAK, 1, 2);
		user.emitGameEvent(GameEvent.EAT);
		user.addStatusEffect(new StatusEffectInstance(YStatusEffects.BLEEDING, 600, 0));
		user.addStatusEffect(new StatusEffectInstance(YStatusEffects.DELICACENESS, 20, 0));
		stack.decrement(1);
		return stack;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (hand == Hand.OFF_HAND && user.isSneaking()) {
			user.setCurrentHand(hand);
			return TypedActionResult.success(user.getStackInHand(hand), false);
		}
		return TypedActionResult.pass(user.getStackInHand(hand));
	}

	private boolean fillIn(BloqueBlockEntity be, Vec3d hitPos, BlockPos bp, Direction side) {
		if (be.isWelded()) return false;
		int slot = be.getSlotForPlacement(hitPos, bp, side);
		if (be.get(slot) == null) {
			be.set(slot, color);
			return true;
		}
		return false;
	}

	@Override
	protected SoundEvent getPlaceSound(BlockState state) {
		return SoundEvents.BLOCK_CALCITE_PLACE;
	}

}
