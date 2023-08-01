package com.unascribed.yttr.compat.emi.recipe;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.compat.emi.YttrEmiPlugin;
import com.unascribed.yttr.crafting.ShatteringRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;

public class EmiShatteringRecipe implements EmiRecipe {
	private final Identifier id;
	private final EmiIngredient input;
	private final EmiStack output;
	public final boolean exclusive;
	
	public EmiShatteringRecipe(Recipe<? extends RecipeInputInventory> recipe) {
		this.id = recipe instanceof ShatteringRecipe ? recipe.getId() : Yttr.id("shattering/"+recipe.getId().getNamespace()+"/"+recipe.getId().getPath());
		this.input = EmiIngredient.of(recipe.getIngredients().get(0));
		this.output = EmiStack.of(recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager()));
		this.exclusive = recipe instanceof ShatteringRecipe;
	}
	
	public EmiShatteringRecipe(ShatteringRecipe recipe) {
		this((Recipe<RecipeInputInventory>)recipe);
	}
	
	public EmiShatteringRecipe(StonecuttingRecipe recipe) {
		this.id = Yttr.id("shattering/"+recipe.getId().getNamespace()+"/"+recipe.getId().getPath());
		this.input = EmiStack.of(recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager()));
		this.output = EmiIngredient.of(recipe.getIngredients().get(0)).getEmiStacks().get(0);
		this.exclusive = true;
	}
	
	public EmiShatteringRecipe(Identifier id, EmiIngredient input, EmiStack output, boolean exclusive) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.exclusive = exclusive;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.SHATTERING;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 76;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(exclusive ? YttrEmiPlugin.Texture.SHATTERING : EmiTexture.EMPTY_ARROW, 26, 1);
		widgets.addSlot(input, 0, 0);
		widgets.addSlot(output, 58, 0).recipeContext(this);
	}
}
