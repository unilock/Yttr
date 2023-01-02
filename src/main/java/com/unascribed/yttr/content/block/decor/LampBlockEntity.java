package com.unascribed.yttr.content.block.decor;

import org.jetbrains.annotations.Nullable;

import com.unascribed.lib39.waypoint.api.AbstractHaloBlockEntity;
import com.unascribed.yttr.init.YBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LampBlockEntity extends AbstractHaloBlockEntity {
	
	public LampBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.LAMP, pos, state);
	}
	
	@Override
	public boolean shouldRenderHalo() {
		return getCachedState().get(LampBlock.LIT);
	}

	@Override
	public int getGlowColor() {
		return getCachedState().get(LampBlock.COLOR).glowColor;
	}
	
	@Override
	public @Nullable Direction getFacing() {
		return getCachedState().contains(WallLampBlock.FACING) ? getCachedState().get(WallLampBlock.FACING) : null;
	}
	

}
