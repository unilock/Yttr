package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YTags;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class GlassyVoidBlock extends AbstractGlassBlock {
	private final int translucency;
	
	public GlassyVoidBlock(int translucency, Settings settings) {
		super(settings);
		this.translucency = translucency;
	}

	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel()-translucency;
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isIn(YTags.Block.VOID_GLASS) ? true : super.isSideInvisible(state, stateFrom, direction);
	}
}