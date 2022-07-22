package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@Mixin(ItemStack.class)
public class MixinItemStack {

	@Shadow
	private NbtCompound nbt;
	
	@Inject(at=@At("RETURN"), method="getMaxDamage", cancellable=true)
	public void getMaxDamage(CallbackInfoReturnable<Integer> ci) {
		if (nbt != null && nbt.contains("yttr:DurabilityBonus")) {
			// every level of durability bonus is +25%
			ci.setReturnValue((ci.getReturnValueI()*(4+(nbt.getInt("yttr:DurabilityBonus"))))/4);
		}
	}
	
}
