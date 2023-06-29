package com.unascribed.yttr.content.block.lazor;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class IRLazorBeamBlock extends AbstractLazorBlock implements Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public IRLazorBeamBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}
	
	@Override
	protected Block getBeam() {
		return YBlocks.IR_LAZOR_BEAM;
	}

	@Override
	protected boolean isEmitter() {
		return false;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (entity instanceof LivingEntity && ((int)entity.getEyeY()) == pos.getY()) {
			((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 80));
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : Fluids.EMPTY.getDefaultState();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
	
}
