package com.unascribed.yttr.mixin.snare;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.unascribed.yttr.init.YItems;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.random.RandomGenerator;

@Mixin(Recipe.class)
public interface MixinRecipe {

	@ModifyReturnValue(at=@At("RETURN"), method="getRemainder")
	default DefaultedList<ItemStack> getRemainingStacks(DefaultedList<ItemStack> original, Inventory inventory) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack is = inventory.getStack(i);
			if (is.getItem() == YItems.SNARE) {
				ItemStack copy = is.copy();
				copy.getNbt().remove("Contents");
				original.set(i, copy);
			} else if (is.getItem() == YItems.SHEARS) {
				ItemStack copy = is.copy();
				if (!copy.damage(4, RandomGenerator.createLegacy(), null)) {
					original.set(i, copy);
				}
			}
		}
		return original;
	}
	
}
