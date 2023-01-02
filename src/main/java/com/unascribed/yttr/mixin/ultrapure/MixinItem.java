package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(Item.class)
public class MixinItem {

	@Shadow @Final @Mutable
	private int maxDamage;
	
	private Integer yttr$oldMaxDamage;
	
	@Inject(at=@At("HEAD"), method={"getItemBarStep", "getItemBarColor"}, cancellable=true)
	public void correctMaxDura(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (stack.hasNbt() && stack.getNbt().contains("yttr:DurabilityBonus")) {
			yttr$oldMaxDamage = this.maxDamage;
			this.maxDamage = stack.getMaxDamage();
		}
	}
	
	@Inject(at=@At("RETURN"), method={"getItemBarStep", "getItemBarColor"}, cancellable=true)
	public void uncorrectMaxDura(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
		if (yttr$oldMaxDamage != null) {
			this.maxDamage = yttr$oldMaxDamage;
			yttr$oldMaxDamage = null;
		}
	}
	
}
