package com.unascribed.yttr.content.block;

import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;

public class TemporaryAirBlock extends AirBlock {
	public TemporaryAirBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (random.nextInt(4) == 0) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
}