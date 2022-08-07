package com.unascribed.yttr.content.block.ruined;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RuinedLeverBlock extends LeverBlock {
	public RuinedLeverBlock(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (player.isCreative()) return super.onUse(state, world, pos, player, hand, hit);
		world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3f, 0.6f);
		world.breakBlock(pos, false);
		return ActionResult.SUCCESS;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) { return 0; }

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) { return 0; }
}