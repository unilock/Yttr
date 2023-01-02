package com.unascribed.yttr.content.block.lazor;

import org.jetbrains.annotations.Nullable;

import com.unascribed.lib39.waypoint.api.AbstractHaloBlockEntity;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LazorBeamBlockEntity extends AbstractHaloBlockEntity {

	public LazorBeamBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.LAZOR_BEAM, pos, state);
	}

	@Override
	public boolean shouldRenderHalo() {
		return true;
	}

	@Override
	public int getGlowColor() {
		return getCachedState().isOf(YBlocks.LAZOR_BEAM) ? getCachedState().get(LazorBeamBlock.COLOR).glowColor : 0;
	}
	
	@Override
	public @Nullable Direction getFacing() {
		return getCachedState().isOf(YBlocks.LAZOR_BEAM) ? getCachedState().get(LazorBeamBlock.FACING) : null;
	}

}
