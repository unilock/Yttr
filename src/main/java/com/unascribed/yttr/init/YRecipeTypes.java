package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.ShatteringRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;

public class YRecipeTypes {

	public static final RecipeType<CentrifugingRecipe> CENTRIFUGING = create("centrifuging");
	public static final RecipeType<VoidFilteringRecipe> VOID_FILTERING = create("void_filtering");
	public static final RecipeType<ShatteringRecipe> SHATTERING = create("shattering");

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.RECIPE_TYPE, YRecipeTypes.class, RecipeType.class);
	}
	
	private static <T extends Recipe<?>> RecipeType<T> create(String id) {
		String fullId = "yttr:"+id;
		return new RecipeType<T>() {
			@Override
			public String toString() {
				return fullId;
			}
		};
	}
	
}
