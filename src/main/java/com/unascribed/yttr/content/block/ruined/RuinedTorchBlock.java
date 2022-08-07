package com.unascribed.yttr.content.block.ruined;

import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public class RuinedTorchBlock extends TorchBlock {
	public RuinedTorchBlock(Settings settings, ParticleEffect particle) {
		super(settings, particle);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {}
}