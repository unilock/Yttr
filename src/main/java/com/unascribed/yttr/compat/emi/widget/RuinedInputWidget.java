package com.unascribed.yttr.compat.emi.widget;

import java.util.List;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RuinedInputWidget extends SlotWidget {

	private boolean disableTooltip = false;
	
	public RuinedInputWidget(int x, int y) {
		super(EmiIngredient.of(Ingredient.EMPTY), x, y);
	}
	
	public RuinedInputWidget disableTooltip(boolean b) {
		this.disableTooltip = b;
		return this;
	}
	
	@Override
	public List<TooltipComponent> getTooltip(int mouseX, int mouseY) {
		return disableTooltip ? List.of() : List.of(TooltipComponent.of(Text.translatable("container.enchant.clue")
				.formatted(Formatting.ITALIC).asOrderedText()));
	}

}
