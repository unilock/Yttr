package com.unascribed.yttr.mixin.ultrapure_bonus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.mixinsupport.UltrapureBonus;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {

	@Inject(at=@At("RETURN"), method="craft(Lnet/minecraft/inventory/CraftingInventory;)Lnet/minecraft/item/ItemStack;")
	public void craft(CraftingInventory inv, CallbackInfoReturnable<ItemStack> ci) {
		UltrapureBonus.handleCraft(inv, ci.getReturnValue());
	}


}
