package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorBlockEntity;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.text.component.TranslatableComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class FlopperBlockEntity extends HopperBlockEntity implements YTickable {

	private BlockState realState;

	public FlopperBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public void tick() {
		realState = getCachedState();
		try {
			((AccessorBlockEntity)this).yttr$setCachedState(realState.with(FlopperBlock.FACING, Direction.DOWN));
			HopperBlockEntity.serverTick(world, pos, realState, this);
		} finally {
			((AccessorBlockEntity)this).yttr$setCachedState(realState);
			realState = null;
		}
	}
	
	public BlockState getRealState() {
		return realState == null ? getCachedState() : realState;
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return YBlockEntities.FLOPPER;
	}
	
	@Override
	protected Text getContainerName() {
		return new TranslatableComponent("block.yttr.flopper");
	}
	
	@Override
	public VoxelShape getInputAreaShape() {
		return YBlocks.FLOPPER.getFunnelShape(getRealState());
	}
	
}
