package com.unascribed.yttr.content.block.natural;

import com.unascribed.yttr.init.YBlocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class SqueezeLeavesBlock extends LeavesBlock implements BlockColorProvider {

	public SqueezeLeavesBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (state.get(WATERLOGGED)) {
			BlockState newState = YBlocks.SQUEEZED_LEAVES.getDefaultState();
			newState = newState.with(DISTANCE, state.get(DISTANCE))
					.with(PERSISTENT, state.get(PERSISTENT))
					.with(WATERLOGGED, true);
			world.setBlockState(pos, newState);
			return newState.onUse(world, player, hand, hit);
		}
		return ActionResult.PASS;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		int waterColor = world.getColor(pos, BiomeColors.WATER_COLOR);
		int waterR = (waterColor >> 16)&0xFF;
		int waterG = (waterColor >>  8)&0xFF;
		int waterB = (waterColor >>  0)&0xFF;
		int leafR = waterB;
		int leafG = waterB-(waterR/4);
		int leafB = waterG-(waterB/3);
		if (!state.get(Properties.WATERLOGGED)) {
			leafR = leafR*2/3;
			leafG = leafG*2/3;
			leafB = leafB*2/3;
		}
		int leafColor = (leafR<<16) | (leafG<<8) | (leafB);
		return leafColor;
	}

}
