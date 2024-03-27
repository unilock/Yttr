package com.unascribed.yttr.mixin.substitute;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.google.gson.JsonObject;
import com.unascribed.yttr.mixinsupport.SetNoSubstitution;

import net.minecraft.recipe.Ingredient;

@Mixin(Ingredient.class)
public abstract class MixinIngredient {
	
	@Shadow @Final
	private Ingredient.Entry[] entries;
	
	@ModifyReturnValue(at=@At("RETURN"), method="entryFromJson")
	private static Ingredient.Entry entryFromJson(Ingredient.Entry original, JsonObject obj) {
		if (obj.has("yttr:no_substitution") && obj.get("yttr:no_substitution").getAsBoolean() && original instanceof SetNoSubstitution) {
			((SetNoSubstitution)original).yttr$setNoSubstitution(true);
		}
		return original;
	}
	
}
