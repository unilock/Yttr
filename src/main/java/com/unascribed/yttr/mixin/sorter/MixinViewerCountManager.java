package com.unascribed.yttr.mixin.sorter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(ViewerCountManager.class)
public class MixinViewerCountManager {

	@Shadow
	private int viewerCount;
	
	@Inject(at=@At("RETURN"), method="getInRangeViewerCount", cancellable=true)
	private void getInRangeViewerCount(World world, BlockPos pos, CallbackInfoReturnable<Integer> ci) {
		if (world.isClient) return;
		if (ci.getReturnValueI() == 0) {
			BlockPos.Mutable mut = new BlockPos.Mutable();
			for (Direction dir : Direction.Type.HORIZONTAL) {
				mut.set(pos).move(dir);
				BlockEntity be = world.getBlockEntity(mut);
				if (be instanceof SkeletalSorterBlockEntity) {
					SkeletalSorterBlockEntity ssbe = (SkeletalSorterBlockEntity)be;
					if (ssbe.accessingInventory == dir.getOpposite()) {
						ci.setReturnValue(1);
						return;
					}
				}
			}
		}
	}
	
}
