package com.unascribed.yttr.content.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CustomShapeBlock extends Block {

	private final VoxelShape shape;
	
	public CustomShapeBlock(Settings settings, VoxelShape shape) {
		super(settings);
		this.shape = shape;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return shape;
	}

}
