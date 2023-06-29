package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class InfiniteVoidFluidBlock extends FluidBlock {
	public InfiniteVoidFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		// no-op to prevent deadlock, and as infinite void doesn't flow
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return state;
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
	}

	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		return new ItemStack(YItems.VOID_BUCKET);
	}
}