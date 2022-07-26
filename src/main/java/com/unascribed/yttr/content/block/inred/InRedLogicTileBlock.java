package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedLogic;

import com.google.common.base.Ascii;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class InRedLogicTileBlock extends InRedDeviceBlock implements Waterloggable {
	public enum BooleanMode implements StringIdentifiable {
		BITWISE,
		BOOLEAN,
		;
		@Override
		public String asString() {
			return Ascii.toLowerCase(name());
		}
	}
	
	public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final EnumProperty<BooleanMode> MODE = EnumProperty.of("mode", BooleanMode.class);

	public static final VoxelShape BASE_SHAPE_Z = Block.createCuboidShape(4, 0, 2, 12, 2, 14);
	public static final VoxelShape BASE_SHAPE_X = Block.createCuboidShape(2, 0, 4, 14, 2, 12);

	public InRedLogicTileBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	public boolean canBlockStay(World world, BlockPos pos) {
		return InRedLogic.isSideSolid(world, pos.down(), Direction.UP)
				//TODO: are these conditions even necessary?
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_SCAFFOLD
				|| world.getBlockState(pos.down()).getBlock() == YBlocks.INRED_BLOCK;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return canBlockStay((World)world, pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Axis.X ? BASE_SHAPE_X : BASE_SHAPE_Z;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getCollisionShape(state, world, pos, context);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}
}
