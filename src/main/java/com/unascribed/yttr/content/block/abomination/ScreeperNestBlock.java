package com.unascribed.yttr.content.block.abomination;

import com.unascribed.yttr.util.YTickable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public class ScreeperNestBlock extends FacingBlock implements BlockEntityProvider {

	public ScreeperNestBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx)
				.with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ScreeperNestBlockEntity(pos, state);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		if (world.getBlockEntity(pos) instanceof ScreeperNestBlockEntity be) {
			if (be.canInsert(0, stack, hit.getSide())) {
				if (!world.isClient) {
					ItemStack is = stack.copy();
					is.setCount(1);
					if (!player.isCreative()) {
						stack.decrement(1);
					}
					be.setStack(0, is);
					return ActionResult.SUCCESS;
				}
				return ActionResult.CONSUME;
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return YTickable::tick;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		Direction facing = state.get(FACING);
		if (world.getBlockState(pos.offset(facing)).isAir()) {
			int ofsX = facing.getOffsetX();
			int ofsY = facing.getOffsetY();
			int ofsZ = facing.getOffsetZ();
			double x = pos.getX()+0.5+(ofsX*0.5);
			double y = pos.getY()+0.5+(ofsY*0.5);
			double z = pos.getZ()+0.5+(ofsZ*0.5);
			world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, ofsX*0.05, ofsY*0.05, ofsZ*0.05);
		}
		if (random.nextInt(50) == 0) {
			world.playSound(MinecraftClient.getInstance().player, pos, SoundEvents.ENTITY_SILVERFISH_AMBIENT, SoundCategory.BLOCKS, 0.2f, 1.3f);
			world.playSound(MinecraftClient.getInstance().player, pos, SoundEvents.ENTITY_CREEPER_HURT, SoundCategory.BLOCKS, 0.1f, 0.7f);
		}
	}
	
}
