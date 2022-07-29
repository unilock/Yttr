package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public interface AccessorDimensionType {

	@Accessor("THE_NETHER")
	static DimensionType yttr$getTheNether() { throw new AbstractMethodError(); }
	
}
