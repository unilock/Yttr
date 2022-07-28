package com.unascribed.yttr.mixin.accessor;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(HopperBlockEntity.class)
public interface AccessorHopperBlockEntity {

	@Accessor("lastTickTime")
	long yttr$getLastTickTime();
	@Accessor("lastTickTime")
	void yttr$setLastTickTime(long v);
	
	@Accessor("transferCooldown")
	int yttr$getTransferCooldown();
	@Accessor("transferCooldown")
	void yttr$setTransferCooldown(int v);
	
	@Invoker("needsCooldown")
	boolean yttr$needsCooldown();
	
	@Invoker("insertAndExtract")
	static boolean yttr$insertAndExtract(World world, BlockPos pos, BlockState state, HopperBlockEntity blockEntity, BooleanSupplier booleanSupplier) {
		throw new AbstractMethodError();
	}
	
}
