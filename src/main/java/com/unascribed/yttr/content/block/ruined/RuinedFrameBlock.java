package com.unascribed.yttr.content.block.ruined;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;

public class RuinedFrameBlock extends RuinedPipeBlock {
	public RuinedFrameBlock(Settings settings) {
		super(settings);
	}

	@Override
	public boolean connectsTo(BlockState bs, Direction face) {
		return bs.isOf(this);
	}
}