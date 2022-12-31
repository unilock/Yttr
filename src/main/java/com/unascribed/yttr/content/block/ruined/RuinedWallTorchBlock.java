package com.unascribed.yttr.content.block.ruined;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RuinedWallTorchBlock extends WallTorchBlock {
	public RuinedWallTorchBlock(Settings settings, ParticleEffect particleEffect) {
		super(settings, particleEffect);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {}
}