package com.unascribed.yttr.compat.emi.widget;

import java.util.List;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RuinedOutputWidget extends SlotWidget {

	private final EmiStack stack;
	
	public RuinedOutputWidget(EmiStack stack, int x, int y) {
		super(EmiIngredient.of(Ingredient.EMPTY), x, y);
		this.stack = stack;
	}
	
	@Override
	public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
		return List.of(TooltipComponent.of(Text.translatable("container.enchant.clue",
					Text.translatable(stack.getItemStack().getTranslationKey()+".alt")
				).formatted(Formatting.ITALIC).asOrderedText()));
	}

}
