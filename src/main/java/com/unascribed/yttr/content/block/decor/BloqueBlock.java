package com.unascribed.yttr.content.block.decor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.unascribed.yttr.init.*;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;

import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.joml.Vector3f;

public class BloqueBlock extends Block implements Waterloggable, BlockEntityProvider, BlockPickInteractionAware {
	
	public static final int XSIZE = 2;
	public static final int YSIZE = 3;
	public static final int ZSIZE = 2;
	
	public static final double XSIZED = XSIZE;
	public static final double YSIZED = YSIZE;
	public static final double ZSIZED = ZSIZE;
	
	public static final int SLOTS = XSIZE*YSIZE*ZSIZE;
	
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
			return be.getVoxelShape();
		}
		return VoxelShapes.cuboid(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack is = player.getStackInHand(hand);
		if (is.isOf(YItems.VOID_BUCKET) || (is.isOf(Items.BUCKET) && player.isCreative())) {
			if (!world.isClient && world.getBlockEntity(pos) instanceof BloqueBlockEntity be) {
				Box box = getOutlineShape(state, world, pos, ShapeContext.absent()).getBoundingBox();
				Vec3d center = box.getCenter().add(pos.getX(), pos.getY(), pos.getZ());
				double dX = box.getXLength()/2;
				double dY = box.getYLength()/2;
				double dZ = box.getZLength()/2;
				if (is.isOf(YItems.VOID_BUCKET) && be.isWeldable()) {
					if (world instanceof ServerWorld sw) {
						sw.spawnParticles(new DustParticleEffect(new Vector3f(0, 0, 0), 1), center.x, center.y, center.z, 18, dX, dY, dZ, 1);
						sw.playSound(null, pos, YSounds.DISSOLVE, SoundCategory.PLAYERS, 1, 1);
					}
					if (!be.isWelded() && player instanceof ServerPlayerEntity spe) {
						YCriteria.WELD_BLOQUE.trigger(spe);
						YStats.add(player, YStats.BLOQUES_WELDED, be.getPopCount());
					}
					be.weld();
				} else if (is.isOf(Items.BUCKET) && be.isWelded()) {
					be.unweld();
					if (world instanceof ServerWorld sw) {
						sw.spawnParticles(new DustParticleEffect(new Vector3f(1, 0, 0), 1), center.x, center.y, center.z, 10, dX, dY, dZ, 1);
						sw.spawnParticles(new DustParticleEffect(new Vector3f(0, 1, 0), 1), center.x, center.y, center.z, 10, dX, dY, dZ, 1);
						sw.spawnParticles(new DustParticleEffect(new Vector3f(0, 0, 1), 1), center.x, center.y, center.z, 10, dX, dY, dZ, 1);
						sw.spawnParticles(new DustParticleEffect(new Vector3f(1, 1, 0), 1), center.x, center.y, center.z, 10, dX, dY, dZ, 1);
						sw.spawnParticles(new DustParticleEffect(new Vector3f(0, 1, 1), 1), center.x, center.y, center.z, 10, dX, dY, dZ, 1);
						sw.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.PLAYERS, 1, 0.5f);
					}
				} else {
					return ActionResult.FAIL;
				}
				return ActionResult.SUCCESS;
			}
			return ActionResult.CONSUME;
		}
		if (is.getMiningSpeedMultiplier(state) <= 1) return ActionResult.PASS;
		if (world.getBlockEntity(pos) instanceof BloqueBlockEntity be && (!be.isWelded() || player.isCreative())) {
			if (!world.isClient) {
				Vec3d hitVec = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
				int x = (int)((hitVec.x)*XSIZE);
				int y = (int)((hitVec.y)*YSIZE);
				int z = (int)((hitVec.z)*ZSIZE);
				int slot = getSlot(x, y, z);
				DyeColor cur = be.get(slot);
				if (cur == null) {
					Direction face = hit.getSide();
					x -= face.getOffsetX();
					y -= face.getOffsetY();
					z -= face.getOffsetZ();
					slot = getSlot(x, y, z);
					cur = be.get(slot);
				}
				if (cur != null) {
					be.set(slot, null);
					ItemStack drop = new ItemStack(Registries.ITEM.get(Yttr.id(cur.name().toLowerCase(Locale.ROOT)+"_bloque")));
					if (!player.isCreative()) {
						ItemEntity ie = new ItemEntity(world, pos.getX()+((x+.5)/XSIZE), pos.getY()+((y+.5)/YSIZE), pos.getZ()+((z+.5)/ZSIZE), drop);
						world.spawnEntity(ie);
					}
					if (be.isWelded()) {
						world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.PLAYERS, 1, 0.5f);
						Yttr.sync(be);
					} else {
						world.playSound(null, pos, SoundEvents.BLOCK_CALCITE_BREAK, SoundCategory.PLAYERS, 1, 1.3f);
					}
					if (be.getPopCount() == 0) {
						world.setBlockState(pos, world.getFluidState(pos).getBlockState());
					} else {
						world.updateNeighborsAlways(pos, this);
					}
				}
			}
			return ActionResult.success(world.isClient);
		}
		return ActionResult.PASS;
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
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		if (builder.getOptionalParameter(LootContextParameters.BLOCK_ENTITY) instanceof BloqueBlockEntity be) {
			if (be.isWelded()) {
				return List.of(new ItemStack(YItems.DELRENE_SCRAP, be.getPopCount()));
			}
			List<ItemStack> li = new ArrayList<>();
			for (int i = 0; i < SLOTS; i++) {
				DyeColor color = be.get(i);
				if (color != null) {
					li.add(new ItemStack(Registries.ITEM.get(Yttr.id(color.name().toLowerCase(Locale.ROOT)+"_bloque"))));
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
				return new ItemStack(Registries.ITEM.get(Yttr.id(color.name().toLowerCase(Locale.ROOT)+"_bloque")));
			}
		}
		return getPickStack(view, pos, state);
	}
	
	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity le) {
			if (world.getBlockEntity(pos) instanceof BloqueBlockEntity be && be.isWelded()) return;
			if (le.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
				entity.damage(entity.getDamageSources().create(YDamageTypes.BLOQUE), 1);
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
			if (be.isWelded()) return false;
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