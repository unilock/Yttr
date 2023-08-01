package com.unascribed.yttr.compat.emi.recipe;

import java.text.DecimalFormat;
import java.util.List;

import com.unascribed.yttr.compat.emi.YttrEmiPlugin;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EmiVoidFilteringRecipe implements EmiRecipe {
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

	private final Identifier id;
	private final float chance;
	private final EmiStack output;
	
	public EmiVoidFilteringRecipe(VoidFilteringRecipe r) {
		this.id = r.getId();
		this.chance = r.getChance();
		this.output = EmiStack.of(
				r.getResult(MinecraftClient.getInstance().world.getRegistryManager())
		).setChance(chance / 100f);
	}
	
	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.VOID_FILTERING;
	}
	
	@Override
	public int getDisplayHeight() {
		return 18;
	}
	
	@Override
	public int getDisplayWidth() {
		return 92;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of();
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(output, 0, 0)
			.recipeContext(this);
		widgets.addText(Text.translatable("emi.category.yttr.void_filtering.chance", DECIMAL_FORMAT.format(chance)).asOrderedText(),
				22, 5, 0xFF404040, false);
	}

}
