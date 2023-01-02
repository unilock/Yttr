package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YCriteria;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	public World world;
	
	@Inject(at=@At("HEAD"), method="fall")
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		Object self = this;
		if (self instanceof ServerPlayerEntity spe && onGround && (landedState.getBlock() == Blocks.WARPED_HYPHAE || landedState.getBlock() == Blocks.CRIMSON_HYPHAE)) {
			var id = world.getBiome(landedPosition).getKey().map(k -> k.getValue()).orElse(null);
			if (id != null && id.getNamespace().equals("yttr") && id.getPath().equals("scorched_terminus")) {
				if (landedState.get(PillarBlock.AXIS) != Axis.Y) {
					YCriteria.TERMINUS_HOUSE.trigger(spe);
				}
			}
		}
	}
	
}
