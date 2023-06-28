package com.unascribed.yttr.content.block.abomination;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.client.AccessorParticle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TransfungusSporesBlock extends AirBlock implements Waterloggable {
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final IntProperty DISTANCE = IntProperty.of("distance", 1, 13);
	
	private static final VoxelShape SHAPE = createCuboidShape(4, 4, 4, 12, 12, 12);

	public TransfungusSporesBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false).with(DISTANCE, 13));
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		super.randomDisplayTick(state, world, pos, random);
		if (world.isClient) {
			addParticle(pos, false);
		}
	}
	
	@Environment(EnvType.CLIENT)
	private void addParticle(BlockPos pos, boolean prepopulate) {
		var r = ThreadLocalRandom.current();
		if (r.nextInt(3) != 0) return;
		var mc = MinecraftClient.getInstance();
		for (int i = 0; i < (prepopulate ? 30 : 1); i++) {
			double y = prepopulate ? r.nextDouble() : 0.15;
			var prt = mc.particleManager.addParticle(ParticleTypes.SPORE_BLOSSOM_AIR,
					pos.getX()+r.nextDouble(), pos.getY()+y, pos.getZ()+r.nextDouble(),
					0, 0, 0);
			if (prt == null) continue;
			float grav = 0.00025f;
			((AccessorParticle)prt).yttr$setGravityStrength(-grav);
			prt.scale(0.75f);
			prt.setMaxAge(400);
			prt.setVelocity(0, 0, 0);
			float b = (r.nextFloat()-r.nextFloat())*0.2f;
			prt.setColor(0.655f+b, 0.576f+b, 0.392f+b);
		}
	}

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return state.get(DISTANCE) == 13;
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (state.get(DISTANCE) == 13) {
			dropStacks(state, world, pos);
			world.removeBlock(pos, false);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		world.setBlockState(pos, updateDistanceFromFungus(state, world, pos), 3);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		int dist = getDistanceFromFungus(neighborState) + 1;
		if (dist != 1 || state.get(DISTANCE) != dist) {
			world.scheduleBlockTick(pos, this, 1);
		}

		return state;
	}

	private static BlockState updateDistanceFromFungus(BlockState state, WorldAccess world, BlockPos pos) {
		int dist = 13;
		var mut = new BlockPos.Mutable();

		for (Direction direction : Direction.values()) {
			mut.set(pos, direction);
			dist = Math.min(dist, getDistanceFromFungus(world.getBlockState(mut)) + 1);
			if (dist == 1) {
				break;
			}
		}

		return state.with(DISTANCE, dist);
	}

	private static int getDistanceFromFungus(BlockState state) {
		if (state.isOf(YBlocks.TRANSFUNGUS)) {
			return 0;
		} else if (state.isOf(YBlocks.TRANSFUNGUS_SPORES)) {
			return state.get(DISTANCE);
		} else {
			return 13;
		}
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, DISTANCE);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

}
