package com.unascribed.yttr.mixin.transfungus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PistonBlockEntity.class)
public class MixinPistonBlockEntity {
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/world/World.setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal=1),
			method="tick")
	private static void tick(World world, BlockPos pos, BlockState state, PistonBlockEntity pbe, CallbackInfo ci) {
		if (pbe.getMovedBlockState().isOf(YBlocks.TRANSFUNGUS)) {
			var behind = pos.offset(pbe.getMovementDirection().getOpposite());
			if (world.getBlockState(behind).isAir()) {
				world.setBlockState(behind, YBlocks.TRANSFUNGUS_SPORES.getDefaultState());
			}
		}
	}
	
}
