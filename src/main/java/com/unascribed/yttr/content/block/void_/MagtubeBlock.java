package com.unascribed.yttr.content.block.void_;

import com.unascribed.lib39.mesh.api.BlockNetworkManager;
import com.unascribed.yttr.content.block.basic.BasicConnectingBlock;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.world.network.FilterNetwork;
import com.unascribed.yttr.world.network.FilterNodeTypes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MagtubeBlock extends BasicConnectingBlock implements Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public MagtubeBlock(Settings settings) {
		super(0.375f, settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isIn(FluidTags.WATER));
	}
	
	@Override
	public boolean connectsTo(BlockState bs, Direction face) {
		return bs.isIn(YTags.Block.MAGTUBE_TARGETS);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		if (!state.isOf(oldState.getBlock())) {
			if (world instanceof ServerWorld sw) {
				BlockNetworkManager.get(sw).introduce(FilterNetwork.TYPE, pos, FilterNodeTypes.PIPE);
			}
		}
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		if (!newState.isOf(state.getBlock())) {
			if (world instanceof ServerWorld sw) {
				BlockNetworkManager.get(sw).destroy(FilterNetwork.TYPE, pos);
			}
		}
	}

}
