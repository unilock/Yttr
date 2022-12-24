package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;

@Mixin(FallingBlockEntity.class)
public interface AccessorFallingBlockEntity {

	@Accessor("block")
	void yttr$setBlock(BlockState block);
	
}
