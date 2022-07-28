package com.unascribed.yttr.content.item.block;

import com.unascribed.yttr.content.block.decor.BloqueBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BloqueBlockItem extends DyedBlockItem {

	public BloqueBlockItem(Block block, DyeColor color, Settings settings) {
		super(block, color, settings);
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
		BlockPos bp = new BlockPos(context.getHitPos());
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
		return SoundEvents.BLOCK_CANDLE_PLACE;
	}

}
