package com.unascribed.yttr.mixin.snare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RepairItemRecipe;
import net.minecraft.world.World;

@Mixin(RepairItemRecipe.class)
public class MixinRepairItemRecipe {

	@Inject(at=@At("HEAD"), method="matches", cancellable=true)
	public void yttr$matches(CraftingInventory inv, World world, CallbackInfoReturnable<Boolean> ci) {
		for (int i = 0; i < inv.size(); i++) {
			if (inv.getStack(i).isOf(YItems.SNARE)) {
				ci.setReturnValue(false);
				return;
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="craft", cancellable=true)
	public void yttr$craft(CraftingInventory inv, CallbackInfoReturnable<ItemStack> ci) {
		for (int i = 0; i < inv.size(); i++) {
			if (inv.getStack(i).isOf(YItems.SNARE)) {
				ci.setReturnValue(ItemStack.EMPTY);
				return;
			}
		}
	}
	
}
