package com.unascribed.yttr.compat.emi.recipe;

import java.text.DecimalFormat;
import java.util.List;

import com.unascribed.yttr.compat.emi.YttrEmiPlugin;
import com.unascribed.yttr.init.YItems;

import dev.emi.emi.api.recipe.EmiIngredientRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiResolutionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EmiGiftRecipe extends EmiIngredientRecipe {
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	
	private final List<EmiStack> stacks;
	private final EmiIngredient ingredient;

	public EmiGiftRecipe(List<EmiStack> stacks) {
		this.stacks = stacks;
		this.ingredient = EmiStack.of(YItems.DROP_OF_CONTINUITY);
	}

	@Override
	protected EmiIngredient getIngredient() {
		return ingredient;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(ingredient);
	}
	
	@Override
	protected List<EmiStack> getStacks() {
		return stacks;
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return stacks;
	}

	@Override
	protected EmiRecipe getRecipeContext(EmiStack stack, int offset) {
		return new EmiResolutionRecipe(ingredient, stack);
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.CONTINUITY_GIFTS;
	}

	@Override
	public Identifier getId() {
		return new Identifier("yttr", "continuity_gifts");
	}
	
	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);
		widgets.add(new TextWidget(Text.translatable("emi.category.yttr.continuity_gifts.chance", DECIMAL_FORMAT.format(100D/stacks.size())).asOrderedText(),
				86, 5, 0xFF404040, false) {
			@Override
			public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
				return List.of(TooltipComponent.of(Text.translatable("emi.category.yttr.continuity_gifts.chance.tooltip").asOrderedText()));
			}
		});
	}
	
}
