package com.unascribed.yttr.content.block.lazor;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public abstract class AbstractLazorBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING;

	public AbstractLazorBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	protected abstract boolean isEmitter();
	protected Block getBeam() {
		return YBlocks.LAZOR_BEAM;
	}
	protected boolean areEquivalent(BlockState a, BlockState b) {
		return a.getBlock() instanceof AbstractLazorBlock alba && b.getBlock() instanceof AbstractLazorBlock albb &&
				alba.getBeam() == albb.getBeam() &&
				a.get(FACING) == b.get(FACING);
	}
	protected BlockState copyProperties(BlockState from, BlockState to) {
		return to.with(FACING, from.get(FACING));
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection());
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		BlockPos behind = pos.offset(state.get(FACING).getOpposite());
		BlockPos ahead = pos.offset(state.get(FACING));
		BlockState behindState = world.getBlockState(behind);
		BlockState aheadState = world.getBlockState(ahead);
		if (!isEmitter() && !areEquivalent(behindState, state)) {
			world.setBlockState(pos, state.getFluidState().getBlockState());
		} else if (aheadState.isAir() || aheadState.materialReplaceable()) {
			if (isEmitter()) {
				state = copyProperties(state, getBeam().getDefaultState());
			}
			if (state.getBlock() instanceof Waterloggable) {
				state = state.with(Properties.WATERLOGGED, aheadState.getFluidState().isIn(FluidTags.WATER));
			}
			world.setBlockState(ahead, state);
		}
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		if (!world.getBlockTickScheduler().isQueued(pos, state.getBlock())) {
			world.scheduleBlockTick(pos, state.getBlock(), getFluidState(state).isEmpty() ? 1 : 2);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!world.getBlockTickScheduler().isQueued(pos, state.getBlock())) {
			world.scheduleBlockTick(pos, state.getBlock(), getFluidState(state).isEmpty() ? 1 : 2);
		}
	}

}
