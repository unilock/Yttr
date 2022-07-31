package com.unascribed.yttr.mixin.effector.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
@Mixin(targets="net/minecraft/client/render/chunk/RenderedChunk")
public abstract class MixinRenderedChunk {

	@Shadow @Final
	private WorldChunk chunk;
	
	@Inject(at=@At("HEAD"), method="getBlockEntity", cancellable=true)
	public void getBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> ci) {
		if (chunk != null && chunk.getWorld() instanceof YttrWorld yw) {
			if (yw.yttr$isPhased(chunk.getPos(), pos)) {
				ci.setReturnValue(null);
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="getBlockState", cancellable=true)
	public void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> ci) {
		if (chunk != null && chunk.getWorld() instanceof YttrWorld yw) {
			if (yw.yttr$isPhased(chunk.getPos(), pos)) {
				ci.setReturnValue(Blocks.VOID_AIR.getDefaultState());
			}
		}
	}
	
}
