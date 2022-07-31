package com.unascribed.yttr.compat.emi;

import java.util.List;

import com.unascribed.yttr.Yttr;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public class EmiCentrifugingRecipe implements EmiRecipe {

	private final Identifier id;
	private final EmiIngredient input;
	private final List<EmiStack> outputs;
	
	public EmiCentrifugingRecipe(Identifier id, EmiIngredient input, List<EmiStack> outputs) {
		this.id = id;
		this.input = input;
		this.outputs = outputs;
	}
	
	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.CENTRIFUGING;
	}
	
	@Override
	public int getDisplayHeight() {
		return 94;
	}
	
	@Override
	public int getDisplayWidth() {
		return 95;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(Yttr.id("textures/gui/centrifuge.png"), 0, 0, 94, 95, 41, 8);
		
		widgets.addSlot(outputOrEmpty(0), 0, 31)
			.output(true)
			.recipeContext(this);
		widgets.addSlot(outputOrEmpty(1), 38, 0)
			.output(true)
			.recipeContext(this);
		widgets.addSlot(outputOrEmpty(2), 68, 39)
			.output(true)
			.recipeContext(this);
		widgets.addSlot(outputOrEmpty(3), 30, 69)
			.output(true)
			.recipeContext(this);
		
		widgets.addSlot(input, 38, 39);
	}

	private EmiIngredient outputOrEmpty(int i) {
		return i >= outputs.size() ? EmiIngredient.of(Ingredient.empty()) : outputs.get(i);
	}

}
