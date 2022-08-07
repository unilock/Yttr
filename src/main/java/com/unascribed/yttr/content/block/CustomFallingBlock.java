package com.unascribed.yttr.content.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class CustomFallingBlock extends FallingBlock {

	private final int color;
	
	public CustomFallingBlock(Settings settings, int color) {
		super(settings);
		this.color = color;
	}
	
	@Override
	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return color;
	}

}
