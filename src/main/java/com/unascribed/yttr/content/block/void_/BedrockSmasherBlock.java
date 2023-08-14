package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.mechanics.rifle.Shootable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BedrockSmasherBlock extends Block implements Shootable {
	private static final VoxelShape SHAPE = VoxelShapes.union(
			VoxelShapes.cuboid(6/16D, 0, 2/16D, 10/16D, 13/16D, 14/16D),
			VoxelShapes.cuboid(0, 12.5/16D, 0, 1, 13.5/16D, 1)
		);

	public BedrockSmasherBlock(Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public boolean onShotByRifle(World world, BlockState bs, LivingEntity user, RifleMode mode, float power, BlockPos pos, BlockHitResult bhr) {
		boolean correctMode = (mode == RifleMode.EXPLODE);
		boolean enoughPower = (power > 1.1f);
		boolean isOverworld = (world.getRegistryKey().getValue().toString().equals("minecraft:overworld"));
		boolean isBottomTen = (pos.getY() < (world.getBottomY() + 10));
		boolean bhrIsUp = (bhr.getSide() == Direction.UP);
		boolean breakBedrockAnywhere = YConfig.General.breakBedrockAnywhere;

		if (correctMode && enoughPower && bhrIsUp && ((isOverworld && isBottomTen) || breakBedrockAnywhere)) {
			BlockPos down = pos.down();
			BlockState downState = world.getBlockState(down);
			
			if (downState.isOf(Blocks.BEDROCK)) {
				performBedrockBreaking(world, down, user, pos);
			}
		}

		return false;
	}

	private void performBedrockBreaking(World world, BlockPos down, LivingEntity user, BlockPos pos) {
		boolean isBottomY = (down.getY() == world.getBottomY());
		boolean isOverworld = (world.getRegistryKey().getValue().toString().equals("minecraft:overworld"));

		if (isBottomY && isOverworld) {
			createVoidGeyser(world, down, user, pos);
		} else {
			createRuinedBedrock(world, down, user);
		}

		if (user instanceof ServerPlayerEntity) {
			YCriteria.BREAK_BEDROCK.trigger((ServerPlayerEntity)user, pos, user.getStackInHand(Hand.MAIN_HAND));
		}

		YStats.add(user, YStats.BEDROCK_BROKEN, 1);
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
		world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.SNAP, SoundCategory.BLOCKS, 1, 2);
		world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.SNAP, SoundCategory.BLOCKS, 1, 1.5f);
		world.playSound(null, down.getX()+0.5, down.getY()+0.5, down.getZ()+0.5, YSounds.CLANG, SoundCategory.BLOCKS, 1, 0.5f);
		if (world instanceof ServerWorld) {
			((ServerWorld)world).spawnParticles(ParticleTypes.EXPLOSION, down.getX()+0.5, down.getY()+1, down.getZ()+0.5, 8, 1, 1, 1, 0);
		}
	}

	private void createVoidGeyser(World world, BlockPos down, LivingEntity user, BlockPos pos) {
		world.setBlockState(down, YBlocks.VOID_GEYSER.getDefaultState());
		VoidGeyserBlockEntity.setDefaultName(world, down, user);
		world.playSound(null, down.getX() + 0.5, down.getY() + 0.5, down.getZ() + 0.5, YSounds.VOID_HOLE, SoundCategory.BLOCKS, 1, 0.5f);
		YStats.add(user, YStats.GEYSERS_OPENED, 1);

		if (user instanceof ServerPlayerEntity) {
			YCriteria.OPEN_GEYSER.trigger((ServerPlayerEntity) user, pos, user.getStackInHand(Hand.MAIN_HAND));
		}
	}

	private void createRuinedBedrock(World world, BlockPos down, LivingEntity user) {
		world.setBlockState(down, YBlocks.RUINED_BEDROCK.getDefaultState());
		world.breakBlock(down.north(), true, user);
		world.breakBlock(down.south(), true, user);
	}

}