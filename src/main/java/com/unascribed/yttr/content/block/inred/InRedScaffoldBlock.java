package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedLogic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InRedScaffoldBlock extends ScaffoldingBlock {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final BooleanProperty UP = BooleanProperty.of("up");

	public InRedScaffoldBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState()
				.with(NORTH, false)
				.with(SOUTH, false)
				.with(EAST, false)
				.with(WEST, false)
				.with(UP, false)
				.with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(NORTH, SOUTH, EAST, WEST, UP);
	}

	private boolean getCableConnections(BlockView world, BlockPos pos, Direction dir) {
		if (world.getBlockState(pos.offset(dir)).getBlock() == YBlocks.INRED_CABLE) return true;
		return InRedLogic.canConnect(world, pos.offset(dir), dir.getOpposite());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		return super.getPlacementState(ctx)
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(UP, getCableConnections(world, pos, Direction.UP));
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		world.setBlockState(pos, state
				.with(NORTH, getCableConnections(world, pos, Direction.NORTH))
				.with(SOUTH, getCableConnections(world, pos, Direction.SOUTH))
				.with(EAST, getCableConnections(world, pos, Direction.EAST))
				.with(WEST, getCableConnections(world, pos, Direction.WEST))
				.with(UP, getCableConnections(world, pos, Direction.UP))
		);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Blocks.SCAFFOLDING);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.updateNeighborsAlways(pos, this);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		world.updateNeighborsAlways(pos, this);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		world.updateNeighborsAlways(pos, this);
	}
	
}
