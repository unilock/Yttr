package com.unascribed.yttr.mixin.effector;

import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public abstract class MixinWorld implements YttrWorld {
	
	@Override
	public void yttr$scheduleRenderUpdate(BlockPos pos) {
	}
	
}
