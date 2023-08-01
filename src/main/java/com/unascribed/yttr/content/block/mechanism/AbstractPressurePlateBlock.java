package com.unascribed.yttr.content.block.mechanism;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

// 1.20's AbstractPressurePlateBlock sucks
public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
	protected static final Box BOX = new Box(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

	protected AbstractPressurePlateBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getRedstoneOutput(state) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}

	protected int getTickRate() {
		return 20;
	}

	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		return hasTopRim(world, blockPos) || sideCoversSmallSquare(world, blockPos, Direction.UP);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		int i = this.getRedstoneOutput(state);
		if (i > 0) {
			this.updatePlateState((Entity) null, world, pos, state, i);
		}

	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i == 0) {
				this.updatePlateState(entity, world, pos, state, i);
			}

		}
	}

	protected void updatePlateState(@Nullable Entity entity, World world, BlockPos pos, BlockState state, int output) {
		int i = this.getRedstoneOutput(world, pos);
		boolean bl = output > 0;
		boolean bl2 = i > 0;
		if (output != i) {
			BlockState blockState = this.setRedstoneOutput(state, i);
			world.setBlockState(pos, blockState, 2);
			this.updateNeighbors(world, pos);
			world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
		}

		if (!bl2 && bl) {
			this.playDepressSound(world, pos);
			world.emitGameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
		} else if (bl2 && !bl) {
			this.playPressSound(world, pos);
			world.emitGameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
		}

		if (bl2) {
			world.scheduleBlockTick(new BlockPos(pos), this, this.getTickRate());
		}

	}

	protected abstract void playPressSound(WorldAccess world, BlockPos pos);

	protected abstract void playDepressSound(WorldAccess world, BlockPos pos);

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			if (this.getRedstoneOutput(state) > 0) {
				this.updateNeighbors(world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	protected void updateNeighbors(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return this.getRedstoneOutput(state);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? this.getRedstoneOutput(state) : 0;
	}

	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	protected abstract int getRedstoneOutput(World world, BlockPos pos);

	protected abstract int getRedstoneOutput(BlockState state);

	protected abstract BlockState setRedstoneOutput(BlockState state, int rsOut);
}