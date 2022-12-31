package com.unascribed.yttr.content.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class TemporaryFluidBlock extends FluidBlock {
	public TemporaryFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (random.nextInt(4) == 0) {
			world.setBlockState(pos, state.getFluidState().getBlockState());
		}
	}
}