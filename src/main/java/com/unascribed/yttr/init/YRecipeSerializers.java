package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.HaemopalRecipe;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.SecretShapedRecipe;
import com.unascribed.yttr.crafting.ShatteringRecipe;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;

public class YRecipeSerializers {

	public static final LampRecipe.Serializer LAMP_CRAFTING = new LampRecipe.Serializer();
	public static final CentrifugingRecipe.Serializer CENTRIFUGING = new CentrifugingRecipe.Serializer();
	public static final VoidFilteringRecipe.Serializer VOID_FILTERING = new VoidFilteringRecipe.Serializer();
	public static final ShatteringRecipe.Serializer SHATTERING = new ShatteringRecipe.Serializer();
	public static final SecretShapedRecipe.Serializer SECRET_CRAFTING_SHAPED = new SecretShapedRecipe.Serializer();
	public static final HaemopalRecipe.Serializer HAEMOPAL_CRAFTING = new HaemopalRecipe.Serializer();

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.RECIPE_SERIALIZER, YRecipeSerializers.class, RecipeSerializer.class);
	}
	
}
