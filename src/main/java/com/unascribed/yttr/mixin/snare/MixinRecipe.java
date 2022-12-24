package com.unascribed.yttr.mixin.snare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.init.YItems;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.random.RandomGenerator;

@Mixin(Recipe.class)
public interface MixinRecipe {

	@Inject(at=@At("RETURN"), method="getRemainder")
	default void getRemainingStacks(Inventory inventory, CallbackInfoReturnable<DefaultedList<ItemStack>> ci) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack is = inventory.getStack(i);
			if (is.getItem() == YItems.SNARE) {
				ItemStack copy = is.copy();
				copy.getNbt().remove("Contents");
				ci.getReturnValue().set(i, copy);
			} else if (is.getItem() == YItems.SHEARS) {
				ItemStack copy = is.copy();
				if (!copy.damage(4, RandomGenerator.createLegacy(), null)) {
					ci.getReturnValue().set(i, copy);
				}
			}
		}
	}
	
}
