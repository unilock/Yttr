package com.unascribed.yttr.mixin.flopper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.mechanism.FlopperBlock;
import com.unascribed.yttr.content.block.mechanism.FlopperBlockEntity;

import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(HopperBlockEntity.class)
public class MixinHopperBlockEntity {

	@Inject(at=@At("HEAD"), method="getInputInventory", cancellable=true)
	private static void getInputInventory(World world, Hopper hopper, CallbackInfoReturnable<Inventory> ci) {
		if (hopper instanceof FlopperBlockEntity) {
			FlopperBlockEntity flopper = (FlopperBlockEntity)hopper;
			ci.setReturnValue(HopperBlockEntity.getInventoryAt(flopper.getWorld(), flopper.getPos().offset(flopper.getCachedState().get(FlopperBlock.FACING))));
		}
	}
	
	@ModifyVariable(at=@At(value="FIELD", target="net/minecraft/util/math/Direction.DOWN:Lnet/minecraft/util/math/Direction;", shift=Shift.BY, by=3), method="extract")
	private static Direction modifyExtractDirection(Direction in, World world, Hopper hopper) {
		if (hopper instanceof FlopperBlockEntity) {
			return ((FlopperBlockEntity)hopper).getCachedState().get(FlopperBlock.FACING).getOpposite();
		}
		return in;
	}
	
}
