package com.unascribed.yttr.content.block.decor;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixinsupport.SlopeStander;
import com.unascribed.yttr.util.math.opengjk.OpenGJK;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import com.google.common.collect.Iterables;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class CleavedBlock extends Block implements BlockEntityProvider, BlockColorProvider, Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final IntProperty LUMINANCE = IntProperty.of("luminance", 0, 15);
	
	public CleavedBlock(Settings settings) {
		super(settings.luminance(bs -> bs.get(LUMINANCE)));
		setDefaultState(getDefaultState().with(WATERLOGGED, false).with(LUMINANCE, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LUMINANCE);
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CleavedBlockEntity(pos, state);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (noCollisionBox.get()) return VoxelShapes.empty();
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity) be).getShape();
		}
		return super.getOutlineShape(state, world, pos, context);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		super.onSteppedOn(world, pos, state, entity);
		var down = pos.down();
		var bs = world.getBlockState(down);
		if (bs.isIn(YTags.Block.CLEAVE_PASSTHRU)) {
			bs.getBlock().onSteppedOn(world, down, bs, entity);
		}
	}
	
	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		super.onLandedUpon(world, state, pos, entity, fallDistance);
		var down = pos.down();
		var bs = world.getBlockState(down);
		if (bs.isIn(YTags.Block.CLEAVE_PASSTHRU)) {
			bs.getBlock().onLandedUpon(world, bs, down, entity, fallDistance);
		}
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		var down = pos.down();
		var bs = world.getBlockState(down);
		if (bs.isIn(YTags.Block.CLEAVE_PASSTHRU)) {
			bs.onEntityCollision(world, down, entity);
		}
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		BlockEntity be = builder.getOptionalParameter(LootContextParameters.BLOCK_ENTITY);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getDroppedStacks(builder);
		}
		return Collections.emptyList();
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			world.syncWorldEvent(player, 2001, pos, getRawIdFromState(((CleavedBlockEntity) be).getDonor()));
		}
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().calcBlockBreakingDelta(player, world, pos);
		}
		return super.calcBlockBreakingDelta(state, player, world, pos);
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		// lie, for grass
		return 0;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return ((CleavedBlockEntity)be).getDonor().getBlock().getPickStack(world, pos, ((CleavedBlockEntity)be).getDonor());
		}
		return super.getPickStack(world, pos, state);
	}
	
	public static final ThreadLocal<Boolean> noCollisionBox = ThreadLocal.withInitial(() -> false);
	
	public void onEntityNearby(BlockState state, World world, BlockPos pos, Entity entity) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity && entity instanceof SlopeStander ss) {
			// we don't check onGround as then you "unsnap" while going down a slope and it looks really bad
			if (entity instanceof FlyingEntity || (entity instanceof PlayerEntity pe && (pe.isFallFlying() || pe.getAbilities().flying))) {
				return;
			}
			if (entity.getVehicle() != null) return;
			if (entity.getType() != null && entity.getType().isIn(YTags.Entity.UNADJUSTABLE)) return;

			float acc = 1f/CleavedBlockEntity.SHAPE_GRANULARITY;
			try {
				noCollisionBox.set(true);
				// TODO this probably isn't efficient, but it works
				double correction;
				double fudge = 0.2;
				if (entity.isSprinting()) {
					fudge = 0.3;
				}
				var box = entity.getBoundingBox().expand(-0.2, fudge, -0.2);
				boolean fullCollision = false;
				double minAdjustment = Float.POSITIVE_INFINITY;
				double minAdjustmentAbs = Float.POSITIVE_INFINITY;
				var player = new OpenGJK.Polytope();
				for (var bp : BlockPos.iterateOutwards(pos, 1, 1, 1)) {
					var bs = world.getBlockState(bp);
					if (bs.isOf(this) && world.getBlockEntity(bp) instanceof CleavedBlockEntity cbe) {
						if (cbe.isAxisAligned()) {
							// This shape is not interesting. Minecraft can handle it just fine.
							// Our attempts to fudge it will make things worse.
							continue;
						}
						var poly = polygonsToPolytope(cbe.getPolygons());
						int steps = 16;
						for (int j = 0; j < steps; j++) {
							double ofs = (acc*2)-(((double)j/steps)*(acc*4));
							double aofs = Math.abs(ofs);
							boxToPolytope(-bp.getX(), ofs-bp.getY(), -bp.getZ(), box, player);
							double coll = OpenGJK.compute_minimum_distance(player, poly, new OpenGJK.Simplex());
							if (coll < 0.0001) {
								if (aofs < minAdjustmentAbs) {
									boxToPolytope(-bp.getX(), ofs-bp.getY(), -bp.getZ(), box, player);
									if (!entity.isOnGround()) {
										fullCollision = OpenGJK.compute_minimum_distance(player, poly, new OpenGJK.Simplex()) < 0.0001;
									}
									minAdjustment = ofs;
									minAdjustmentAbs = aofs;
								}
								break;
							}
						}
					}
				}
				if (Double.isFinite(minAdjustment)) {
					correction = minAdjustment;
					if (fullCollision) {
						entity.setOnGround(true); // for better animations
					}
				} else {
					correction = 0;
				}
				while (Math.abs(correction) > 0.001 && !Iterables.isEmpty(world.getBlockCollisions(entity, entity.getBoundingBox().offset(0, correction, 0)))) {
					correction /= 2;
				}
				if (Math.abs(correction) < 0.001) correction = 0;
				ss.yttr$setYOffset(correction);
			} finally {
				noCollisionBox.set(false);
			}
			
		}
	}

	public static OpenGJK.Polytope polygonsToPolytope(Iterable<Polygon> polygons) {
		var pt = new OpenGJK.Polytope();
		var points = new LinkedHashSet<Vec3d>();
		for (var poly : polygons) {
			for (var edge : poly) {
				points.add(edge.srcPoint());
				points.add(edge.dstPoint());
			}
		}
		pt.coord = points.stream()
				.map(v -> new double[] {v.x, v.y, v.z})
				.toArray(double[][]::new);
		return pt;
	}

	@CanIgnoreReturnValue
	public static OpenGJK.Polytope boxToPolytope(double x1, double y1, double z1, double x2, double y2, double z2, OpenGJK.Polytope cur) {
		var pt = cur == null ? new OpenGJK.Polytope() : cur;
		if (pt.coord == null || pt.coord.length != 8) {
			pt.coord = new double[8][3];
		}
		
		pt.coord[0][0] = x1;
		pt.coord[0][1] = y1;
		pt.coord[0][2] = z1;
		
		pt.coord[1][0] = x2;
		pt.coord[1][1] = y1;
		pt.coord[1][2] = z1;
		
		pt.coord[2][0] = x1;
		pt.coord[2][1] = y1;
		pt.coord[2][2] = z2;
		
		pt.coord[3][0] = x2;
		pt.coord[3][1] = y1;
		pt.coord[3][2] = z2;
		
		pt.coord[4][0] = x1;
		pt.coord[4][1] = y2;
		pt.coord[4][2] = z1;
		
		pt.coord[5][0] = x2;
		pt.coord[5][1] = y2;
		pt.coord[5][2] = z1;
		
		pt.coord[6][0] = x1;
		pt.coord[6][1] = y2;
		pt.coord[6][2] = z2;
		
		pt.coord[7][0] = x2;
		pt.coord[7][1] = y2;
		pt.coord[7][2] = z2;
		
		return pt;
	}
	
	@CanIgnoreReturnValue
	public static OpenGJK.Polytope boxToPolytope(double ox, double oy, double oz, Box box, OpenGJK.Polytope cur) {
		double x1 = box.minX+ox;
		double y1 = box.minY+oy;
		double z1 = box.minZ+oz;
		double x2 = box.maxX+ox;
		double y2 = box.maxY+oy;
		double z2 = box.maxZ+oz;
		return boxToPolytope(x1, y1, z1, x2, y2, z2, cur);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof CleavedBlockEntity) {
			return MinecraftClient.getInstance().getBlockColors().getColor(((CleavedBlockEntity)be).getDonor(), world, pos, tintIndex);
		}
		return -1;
	}
	
}
