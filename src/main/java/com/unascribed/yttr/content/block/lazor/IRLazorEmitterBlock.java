package com.unascribed.yttr.content.block.lazor;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;

public class IRLazorEmitterBlock extends AbstractLazorBlock {

	public IRLazorEmitterBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected boolean isEmitter() {
		return true;
	}
	
	@Override
	protected Block getBeam() {
		return YBlocks.IR_LAZOR_BEAM;
	}
}
