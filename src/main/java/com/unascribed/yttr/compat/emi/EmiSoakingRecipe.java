package com.unascribed.yttr.compat.emi;

import java.util.Collections;
import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.SoakingRecipe;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EmiSoakingRecipe implements EmiRecipe {

	private final Identifier id;
	private final List<EmiIngredient> inputs;
	private final EmiIngredient catalyst;
	private final EmiStack output;
	private final boolean consumesCatalyst;
	
	public EmiSoakingRecipe(SoakingRecipe r) {
		this.id = r.getId();
		this.inputs = r.getSoakingIngredients().map(
					is -> List.of(EmiStack.of(is)),
					li -> li.stream().map(EmiIngredient::of).toList()
				);
		this.catalyst = EmiIngredient.of(r.getCatalyst().getMatchingFluids().stream()
				.map(f -> EmiStack.of(f, 81000))
				.toList());
		this.output = EmiStack.of(r.getResult().<ItemStack>map(
					is -> is,
					bs -> new ItemStack(bs.getBlock().asItem())
				));
		this.consumesCatalyst = r.getResult().right().isPresent();
	}
	
	public EmiSoakingRecipe(Identifier id, List<EmiIngredient> inputs, EmiIngredient catalyst, EmiStack output, boolean consumesCatalyst) {
		this.id = id;
		this.inputs = inputs;
		this.catalyst = catalyst;
		this.output = output;
		this.consumesCatalyst = consumesCatalyst;
	}

	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.SOAKING;
	}
	
	@Override
	public int getDisplayHeight() {
		return 36;
	}
	
	@Override
	public int getDisplayWidth() {
		return 118;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return consumesCatalyst ? Lists.newArrayList(Iterables.concat(inputs, Collections.singleton(catalyst))) : inputs;
	}
	
	@Override
	public List<EmiIngredient> getCatalysts() {
		return consumesCatalyst ? List.of() : List.of(catalyst);
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int x = (widgets.getWidth()/2);
		widgets.addTexture(Yttr.id("textures/gui/curved_arrow_down.png"), x, 2, 16, 16, 0, 0, 16, 16, 16, 16);
		widgets.addTexture(Yttr.id("textures/gui/curved_arrow.png"), x+20, 2, 16, 16, 0, 0, 16, 16, 16, 16);
		
		widgets.addSlot(catalyst, x, 20)
			.drawBack(false);
		widgets.addSlot(catalyst, x+16, 20)
			.drawBack(false);
		

		widgets.addSlot(output, x+40, 0)
			.recipeContext(this);
		
		int y = 0;
		x -= 4;
		int sX = x;
		x -= Math.min(inputs.size(), 3)*18;
		int rem = inputs.size();
		for (EmiIngredient input : inputs) {
			rem--;
			widgets.addSlot(input, x, y);
			x += 18;
			if (x >= sX) {
				x = sX;
				x -= Math.min(rem, 3)*18;
				y += 18;
			}
		}
	}

}
