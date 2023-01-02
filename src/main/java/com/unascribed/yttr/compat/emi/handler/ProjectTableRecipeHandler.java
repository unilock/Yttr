package com.unascribed.yttr.compat.emi.handler;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.inventory.ProjectTableScreenHandler;

import dev.emi.emi.api.EmiRecipeHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import net.minecraft.screen.slot.Slot;

public class ProjectTableRecipeHandler implements EmiRecipeHandler<ProjectTableScreenHandler> {

	private static final int RESULT_SLOT = 0;
	
	private static final int GRID_END = (3*3)+1;
	private static final int INV_END = GRID_END+(9*2);
	private static final int PLAYER_INV_END = INV_END+(9*4);
	
	@Override
	public List<Slot> getInputSources(ProjectTableScreenHandler handler) {
		return handler.slots.subList(0, PLAYER_INV_END);
	}
	
	@Override
	public List<Slot> getCraftingSlots(ProjectTableScreenHandler handler) {
		return handler.slots.subList(1, GRID_END);
	}

	@Override
	public @Nullable Slot getOutputSlot(ProjectTableScreenHandler handler) {
		return handler.getSlot(RESULT_SLOT);
	}

	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree();
	}
}
