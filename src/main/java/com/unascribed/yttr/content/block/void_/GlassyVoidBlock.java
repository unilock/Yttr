package com.unascribed.yttr.content.block.void_;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class GlassyVoidBlock extends Block {
	public GlassyVoidBlock(Settings settings) {
		super(settings);
	}

	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel();
	}
}