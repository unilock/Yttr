package com.unascribed.yttr.content.block;

import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface Voidloggable extends FluidDrainable, FluidFillable {
	BooleanProperty VOIDLOGGED = BooleanProperty.of("voidlogged");

	@Override
	default boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !state.get(VOIDLOGGED) && fluid == YFluids.VOID;
	}

	@Override
	default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!state.get(VOIDLOGGED) && fluidState.getFluid() == YFluids.VOID) {
			if (!world.isClient()) {
				world.setBlockState(pos, state.with(VOIDLOGGED, true), 3);
				world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	default ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		if (state.get(VOIDLOGGED)) {
			world.setBlockState(pos, state.with(VOIDLOGGED, false), 3);
			return new ItemStack(YItems.VOID_BUCKET);
		} else {
			return ItemStack.EMPTY;
		}
	}
}
