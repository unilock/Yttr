package com.unascribed.yttr.mixin.void_fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;

@Mixin(BundleItem.class)
public class MixinBundleItem {

	@Inject(at=@At("HEAD"), method="onStackClicked", cancellable=true)
	public void yttr$onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> ci) {
		if (slot.getStack().isOf(YItems.VOID_BUCKET)) {
			ci.setReturnValue(false);
		}
	}
	
	
}
