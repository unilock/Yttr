package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YTags;

import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class GlassyVoidPaneBlock extends PaneBlock {
	private final int translucency;
	
	public GlassyVoidPaneBlock(int translucency, Settings settings) {
		super(settings);
		this.translucency = translucency;
	}

	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return world.getMaxLightLevel()-translucency;
	}

	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isIn(YTags.Block.VOID_GLASS_PANES) ? true : super.isSideInvisible(state, stateFrom, direction);
	}
}