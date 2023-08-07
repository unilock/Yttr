package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ReplicatorBlock extends Block implements BlockEntityProvider {
	
	private static final VoxelShape SHAPE = createCuboidShape(2, 2, 2, 14, 14, 14);

	public ReplicatorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ReplicatorBlockEntity(pos, state);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return YTickable::tick;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) return ActionResult.CONSUME;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
			ItemStack held = player.getStackInHand(hand);
			if ((rbe.locked || player.isSneaking()) && held.isEmpty()) {
				player.setStackInHand(hand, rbe.getStack(0));
				world.playSound(null, pos, YSounds.REPLICATOR_VEND, SoundCategory.BLOCKS, 1, 1);
				return ActionResult.SUCCESS;
			} else if (!rbe.locked && (player.isCreative() || player.getUuid().equals(rbe.owner))) {
				if (!ItemStack.canCombine(held, rbe.item)) {
					rbe.item = held.copy();
					world.playSound(null, pos, YSounds.REPLICATOR_UPDATE, SoundCategory.BLOCKS, 1, rbe.item.isEmpty() ? 1f : 1.25f);
					Yttr.sync(rbe);
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.CONSUME;
				}
			} else {
				world.playSound(null, pos, YSounds.REPLICATOR_REFUSE, SoundCategory.BLOCKS, 1, 0.75f);
				world.playSound(null, pos, YSounds.REPLICATOR_REFUSE, SoundCategory.BLOCKS, 1, 0.6f);
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.FAIL;
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		if (!player.isCreative()) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof ReplicatorBlockEntity rbe) {
				if (player.getUuid().equals(rbe.owner) || rbe.locked) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	private ItemStack getStack(BlockView world, BlockPos pos) {
		ItemStack stack = new ItemStack(this);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity) {
			stack.setSubNbt("BlockEntityTag", be.toNbt());
		}
		return stack;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getStack(world, pos);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof ReplicatorBlockEntity rbe && placer instanceof PlayerEntity pe && !pe.isCreative()) {
			rbe.owner = placer.getUuid();
		}
		world.playSound(placer instanceof PlayerEntity ? ((PlayerEntity)placer) : null, pos, YSounds.REPLICATOR_APPEAR, SoundCategory.BLOCKS, 1, 1);
	}
	
	@Override
	protected void spawnBreakParticles(World world, PlayerEntity player, BlockPos pos, BlockState state) {
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		world.playSound(null, pos, YSounds.REPLICATOR_DISAPPEAR, SoundCategory.BLOCKS, 1, 1);
		if (!player.isCreative()) {
			dropStack(world, pos, getStack(world, pos));
		}
	}

}
