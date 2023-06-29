package com.unascribed.yttr.content.block.ruined;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FernBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class WastelandGrassBlock extends FernBlock {
	public WastelandGrassBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(YBlocks.WASTELAND_DIRT);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		return false;
	}
}