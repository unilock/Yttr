package com.unascribed.yttr.mixin.ultrapure;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.unascribed.yttr.mixinsupport.UltrapureBonus;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;

@Mixin(ShapelessRecipe.class)
public class MixinShapelessRecipe {

	@ModifyReturnValue(at=@At("RETURN"), method="craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;")
	public ItemStack craft(ItemStack original, RecipeInputInventory inv) {
		UltrapureBonus.handleCraft(inv, original);
		return original;
	}


}
