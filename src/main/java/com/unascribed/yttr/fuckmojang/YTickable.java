package com.unascribed.yttr.fuckmojang;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface YTickable {

	void tick();
	
	static void tick(World world, BlockPos bp, BlockState state, BlockEntity be) {
		if (be instanceof YTickable yt) yt.tick();
	}
	
}
