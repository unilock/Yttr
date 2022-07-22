package com.unascribed.yttr.mixin.effector;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.CollisionView;

@Mixin(BlockCollisionSpliterator.class)
public class MixinBlockCollisionSpliterator {

	@Shadow @Final
	private BlockPos.Mutable pos;
	@Shadow @Final
	private CollisionView world;
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/world/BlockView.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			ordinal=0, method="computeNext")
	public BlockState replaceBlockState(BlockState in) {
		if (world instanceof YttrWorld && ((YttrWorld)world).yttr$isPhased(pos)) {
			return Blocks.VOID_AIR.getDefaultState();
		}
		return in;
	}
	
}
