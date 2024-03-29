package com.unascribed.yttr.content.block.inred;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InRedDemoCyclerBlock extends InRedDeviceBlock {
	public InRedDemoCyclerBlock(Settings settings) {
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new InRedDemoCyclerBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDemoCyclerBlockEntity) {
			if (!world.isClient) {
				((InRedDemoCyclerBlockEntity) be).activate();
			} else {
				world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1f, 1f);
			}
		}
		return ActionResult.SUCCESS;
	}
}
