package com.unascribed.yttr.content.block.abomination;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.content.entity.SlippingTransfungusEntity;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.BlockItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TransfungusBlock extends Block {
	private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

	public TransfungusBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		super.randomDisplayTick(state, world, pos, random);
		var r = ThreadLocalRandom.current();
		world.addParticle(ParticleTypes.PORTAL,
				pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,
				r.nextGaussian(0, 0.2), 0, r.nextGaussian(0, 0.2));
		if (r.nextInt(10) == 0) {
			world.playSound(null, pos, SoundEvents.ENTITY_ENDERMITE_AMBIENT, SoundCategory.BLOCKS, 0.2f, 1.6f);
		}
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), Direction.UP, SideShapeType.CENTER);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			if (world.getBlockState(pos.down()).isAir()) {
				var ste = new SlippingTransfungusEntity((World)world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state);
				world.setBlockState(pos, YBlocks.TRANSFUNGUS_SPORES.getDefaultState(), 3);
				world.spawnEntity(ste);
			}
			return getFluidState(state).getBlockState();
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (hit.getSide().getAxis() != Axis.Y && world.getBlockState(pos.offset(hit.getSide().getOpposite())).isAir()) {
			world.setBlockState(pos.offset(hit.getSide().getOpposite()), state);
			world.setBlockState(pos, YBlocks.TRANSFUNGUS_SPORES.getDefaultState().with(TransfungusSporesBlock.DISTANCE, 1));
			return ActionResult.success(world.isClient);
		}
		return ActionResult.PASS;
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (!world.isClient && entity instanceof ItemEntity item && item.getStack().getItem() instanceof BlockItem bi && !item.cannotPickup()) {
			List<BlockPos> queue = Lists.newArrayList(pos);
			List<BlockPos> nextQueue = Lists.newArrayList();
			Set<BlockPos> seen = Sets.newHashSet();
			ListMultimap<Integer, BlockPos> sporesByDistance = ArrayListMultimap.create();
			var dirs = Direction.values();
			while (!queue.isEmpty()) {
				for (var bp : queue) {
					seen.add(bp);
					var bs = world.getBlockState(bp);
					if (bp == pos || bs.isOf(YBlocks.TRANSFUNGUS_SPORES)) {
						for (var d : dirs) {
							var p = bp.offset(d);
							if (!seen.contains(p)) nextQueue.add(p);
						}
						if (bs.isOf(YBlocks.TRANSFUNGUS_SPORES)) {
							sporesByDistance.put(bs.get(TransfungusSporesBlock.DISTANCE), bp);
						}
					}
				}
				queue.clear();
				queue.addAll(nextQueue);
				nextQueue.clear();
			}
			for (int i = 12; i > 0; i--) {
				var li = sporesByDistance.get(i);
				Collections.shuffle(li, ThreadLocalRandom.current());
				for (var bp : li) {
					if (bi.place(new AutomaticItemPlacementContext(world, bp, Direction.NORTH, item.getStack(), Direction.UP)).isAccepted()) {
						item.setPickupDelay(20);
						item.setStack(item.getStack());
						world.playSound(null, pos, SoundEvents.ENTITY_ENDERMITE_AMBIENT, SoundCategory.BLOCKS, 0.2f, 2);
						world.playSound(null, bp, SoundEvents.ENTITY_ENDERMITE_STEP, SoundCategory.BLOCKS, 0.2f, 1);
						world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 0.1f, 0.8f);
						world.playSound(null, bp, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 0.1f, 1.2f);
						if (world instanceof ServerWorld sw) {
							var r = ThreadLocalRandom.current();
							for (int j = 0; j < 3; j++) {
								sw.spawnParticles(ParticleTypes.PORTAL, pos.getX()+r.nextGaussian(0.5, 0.2), pos.getY()+r.nextGaussian(0.5, 0.2), pos.getZ()+r.nextGaussian(0.5, 0.2), 0,
										(bp.getX()-pos.getX()), (bp.getY()-pos.getY())-1, (bp.getZ()-pos.getZ()),
									1);
							}
						}
						return;
					}
				}
			}
		}
	}
	
}
