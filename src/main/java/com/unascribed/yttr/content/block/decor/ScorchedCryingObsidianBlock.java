package com.unascribed.yttr.content.block.decor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public class ScorchedCryingObsidianBlock extends Block {

	public ScorchedCryingObsidianBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		if (random.nextInt(5) == 0) {
			Direction d = Direction.random(random);
			if (d != Direction.UP) {
				BlockPos blockPos = pos.offset(d);
				BlockState blockState = world.getBlockState(blockPos);
				if (!state.isOpaque() || !blockState.isSideSolidFullSquare(world, blockPos, d.getOpposite())) {
					double x = d.getOffsetX() == 0 ? random.nextDouble() : 0.5 + d.getOffsetX() * 0.6;
					double y = d.getOffsetY() == 0 ? random.nextDouble() : 0.5 + d.getOffsetY() * 0.6;
					double z = d.getOffsetZ() == 0 ? random.nextDouble() : 0.5 + d.getOffsetZ() * 0.6;
					world.addParticle(ParticleTypes.DRIPPING_LAVA, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0.0, 0.0, 0.0);
				}
			}
		}
	}

}
