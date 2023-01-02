package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class FlopperBlockEntity extends HopperBlockEntity implements YTickable {

	public FlopperBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public void tick() {
		HopperBlockEntity.serverTick(world, pos, getCachedState().with(FlopperBlock.FACING, Direction.DOWN), this);
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return YBlockEntities.FLOPPER;
	}
	
	@Override
	protected Text getContainerName() {
		return Text.translatable("block.yttr.flopper");
	}
	
	@Override
	public VoxelShape getInputAreaShape() {
		return YBlocks.FLOPPER.getFunnelShape(getCachedState());
	}
	
}
