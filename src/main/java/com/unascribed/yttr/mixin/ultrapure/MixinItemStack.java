package com.unascribed.yttr.mixin.ultrapure;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@Mixin(ItemStack.class)
public class MixinItemStack {

	@Shadow
	private NbtCompound nbt;
	
	@ModifyReturnValue(at=@At("RETURN"), method="getMaxDamage")
	public int getMaxDamage(int original) {
		if (nbt != null && nbt.contains("yttr:DurabilityBonus")) {
			// every level of durability bonus is +25%
			return (original*(4+(nbt.getInt("yttr:DurabilityBonus"))))/4;
		}
		return original;
	}
	
}
