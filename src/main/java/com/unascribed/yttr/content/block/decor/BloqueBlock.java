package com.unascribed.yttr.content.block.decor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;

import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class BloqueBlock extends Block implements Waterloggable, BlockEntityProvider, BlockPickInteractionAware {
	
	public static final int XSIZE = 2;
	public static final int YSIZE = 3;
	public static final int ZSIZE = 2;
	
	public static final double XSIZED = XSIZE;
	public static final double YSIZED = YSIZE;
	public static final double ZSIZED = ZSIZE;
	
	public static final int SLOTS = XSIZE*YSIZE*ZSIZE;
	
	public static final DamageSource DAMAGE_SOURCE = new DamageSource("yttr.bloque") {};
	
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public static final VoxelShape[] VOXEL_SHAPES = new VoxelShape[SLOTS];
	
	static {
		for (int y = 0; y < YSIZE; y++) {
			for (int x = 0; x < XSIZE; x++) {
				for (int z = 0; z < ZSIZE; z++) {
					VOXEL_SHAPES[getSlot(x, y, z)] = VoxelShapes.cuboid(x/XSIZED, y/YSIZED, z/ZSIZED, (x+1)/XSIZED, (y+1)/YSIZED, (z+1)/ZSIZED);
				}
			}
		}
	}

	public BloqueBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState()
				.with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (world.getBlockEntity(pos) instanceof BloqueBlockEntity be) {
			VoxelShape vs = VoxelShapes.empty();
			for (int i = 0; i < SLOTS; i++) {
				if (be.get(i) != null) {
					vs = VoxelShapes.combine(vs, VOXEL_SHAPES[i], BooleanBiFunction.OR);
				}
			}
			if (vs != VoxelShapes.empty()) {
				return vs;
			}
		}
		return VoxelShapes.cuboid(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);
	}
	
	public static int getSlot(Vec3d hitVec, BlockPos blockPos, Direction face) {
		return getSlot(hitVec.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ()), face);
	}
	
	public static int getSlot(Vec3d hitVec, Direction face) {
		int x = (int)((hitVec.x)*XSIZE);
		int y = (int)((hitVec.y)*YSIZE);
		int z = (int)((hitVec.z)*ZSIZE);
		if (y >= YSIZE && face == Direction.DOWN) {
			y = YSIZE-1;
		}
		if (y < 0 && face == Direction.UP) {
			y = 0;
		}
		if (x >= XSIZE && face == Direction.EAST) {
			x = XSIZE-1;
		}
		if (x < 0 && face == Direction.WEST) {
			x = 0;
		}
		if (z >= ZSIZE && face == Direction.SOUTH) {
			z = ZSIZE-1;
		}
		if (z < 0 && face == Direction.NORTH) {
			z = 0;
		}
		return getSlot(x, y, z);
	}
	
	public static int getSlot(int x, int y, int z) {
		return (y*ZSIZE*XSIZE)+(x*ZSIZE)+z;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		if (builder.getNullable(LootContextParameters.BLOCK_ENTITY) instanceof BloqueBlockEntity be) {
			List<ItemStack> li = new ArrayList<>();
			for (int i = 0; i < SLOTS; i++) {
				DyeColor color = be.get(i);
				if (color != null) {
					li.add(new ItemStack(Registry.ITEM.get(Yttr.id(color.name().toLowerCase(Locale.ROOT)+"_bloque"))));
				}
			}
			return li;
		}
		return Collections.emptyList();
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(YItems.WHITE_BLOQUE);
	}

	@Override
	public ItemStack getPickedStack(BlockState state, BlockView view, BlockPos pos, @Nullable PlayerEntity player, @Nullable HitResult result) {
		if (result instanceof BlockHitResult bhr && view.getBlockEntity(pos) instanceof BloqueBlockEntity be) {
			DyeColor color = be.get(getSlot(bhr.getPos(), bhr.getBlockPos(), bhr.getSide()));
			if (color != null) {
				return new ItemStack(Registry.ITEM.get(Yttr.id(color.name().toLowerCase(Locale.ROOT)+"_bloque")));
			}
		}
		return getPickStack(view, pos, state);
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity le) {
			if (le.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
				entity.damage(DAMAGE_SOURCE, 1);
			}
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState bs = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (bs.isOf(this)) {
			return bs;
		}
		return super.getPlacementState(ctx)
				.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		if (context.getStack().isIn(YTags.Item.BLOQUES) && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof BloqueBlockEntity be) {
			int slot = getSlot(context.getHitPos(), context.getBlockPos(), context.getSide());
			if (slot < 0 || slot >= SLOTS) return false;
			return be.get(slot) == null;
		}
		return false;
	}
	
	@Override
	public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
		return world.getBlockEntity(pos) instanceof BloqueBlockEntity be && be.onSyncedBlockEvent(type, data);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BloqueBlockEntity(pos, state);
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
		return !isFullCube(world, pos, state) ? Waterloggable.super.tryFillWithFluid(world, pos, state, fluidState) : false;
	}

	@Override
	public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return !isFullCube(world, pos, state) ? Waterloggable.super.canFillWithFluid(world, pos, state, fluid) : false;
	}

	private boolean isFullCube(BlockView world, BlockPos pos, BlockState state) {
		return world.getBlockEntity(pos) instanceof BloqueBlockEntity be && be.isFullCube();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		switch(type) {
			case LAND:
				return false;
			case WATER:
				return world.getFluidState(pos).isIn(FluidTags.WATER);
			case AIR:
				return false;
			default:
				return false;
		}
	}
	
}