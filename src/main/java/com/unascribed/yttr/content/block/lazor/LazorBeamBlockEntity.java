package com.unascribed.yttr.content.block.lazor;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mechanics.HaloBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LazorBeamBlockEntity extends BlockEntity implements HaloBlockEntity {

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
	public Object getStateObject() {
		return getCachedState();
	}
	
	@Override
	public @Nullable Direction getFacing() {
		return getCachedState().isOf(YBlocks.LAZOR_BEAM) ? getCachedState().get(LazorBeamBlock.FACING) : null;
	}

}
