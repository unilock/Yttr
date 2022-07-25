package com.unascribed.yttr.mixin.continuity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.content.block.natural.RootOfContinuityBlock;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

@Mixin(EndIslandFeature.class)
public class MixinEndIslandFeature {

	@Inject(at=@At("TAIL"), method="place")
	public void place(FeatureContext<DefaultFeatureConfig> context, CallbackInfoReturnable<Boolean> ci) {
		if (!YConfig.WorldGen.continuity) return;
		if (ci.getReturnValueZ()) {
			BlockPos pos = context.getOrigin();
			StructureWorldAccess world = context.getWorld();
			BlockPos.Mutable cur = pos.mutableCopy();
			BlockState bs = world.getBlockState(cur);
			int i = 0;
			while (!bs.isAir()) {
				if (i++ > 50) return;
				cur.move(Direction.DOWN);
				bs = world.getBlockState(cur);
			}
			cur.move(Direction.UP);
			world.setBlockState(cur, YBlocks.ROOT_OF_CONTINUITY.getDefaultState().with(RootOfContinuityBlock.ANCHOR, true), 3);
		}
	}
	
}
