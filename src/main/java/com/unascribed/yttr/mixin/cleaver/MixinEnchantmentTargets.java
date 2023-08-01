package com.unascribed.yttr.mixin.cleaver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.item.Item;

// WEAPON and DIGGER
@Mixin(targets={"net/minecraft/enchantment/EnchantmentTarget$C_hkngjlpo", "net/minecraft/enchantment/EnchantmentTarget$C_gdezogus"})
public class MixinEnchantmentTargets {

	@Inject(at=@At("HEAD"), method="isAcceptableItem(Lnet/minecraft/item/Item;)Z", cancellable=true)
	public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> ci) {
		if (item == YItems.REINFORCED_CLEAVER) {
			ci.setReturnValue(true);
		}
	}
	
}
