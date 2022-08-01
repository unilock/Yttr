package com.unascribed.yttr.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface YTickable {

	void tick();
	@Environment(EnvType.CLIENT)
	default void clientTick() {}

	static void tick(World world, BlockPos bp, BlockState state, BlockEntity be) {
		if (be instanceof YTickable yt) {
			yt.tick();
			if (world.isClient) {
				yt.clientTick();
			}
		}
	}

}
