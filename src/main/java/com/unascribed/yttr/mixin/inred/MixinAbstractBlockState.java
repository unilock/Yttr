package com.unascribed.yttr.mixin.inred;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.MapCodec;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

@Mixin(AbstractBlockState.class)
public abstract class MixinAbstractBlockState extends State<Block, BlockState> {

	protected MixinAbstractBlockState(Block owner, ImmutableMap<Property<?>, Comparable<?>> entries, MapCodec<BlockState> codec) {
		super(owner, entries, codec);
	}

	@Inject(at=@At("HEAD"), method="isOf", cancellable=true)
	public void isOf(Block block, CallbackInfoReturnable<Boolean> ci) {
		if (block == Blocks.SCAFFOLDING && this.owner == YBlocks.INRED_SCAFFOLD) {
			ci.setReturnValue(true);
		}
	}
	
}
