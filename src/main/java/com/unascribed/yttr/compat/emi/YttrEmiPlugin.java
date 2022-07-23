package com.unascribed.yttr.compat.emi;

import java.util.function.Function;

import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.SecretShapedRecipe;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;

public class YttrEmiPlugin implements EmiPlugin {

	@Override
	public void register(EmiRegistry registry) {
		registry.addRecipe(EmiWorldInteractionRecipe.builder()
			.id(new Identifier("yttr", "stripping/squeeze_log"))
			.leftInput(EmiStack.of(YBlocks.SQUEEZE_LOG))
			.rightInput(EmiStack.of(Items.IRON_AXE), true)
			.output(EmiStack.of(YBlocks.STRIPPED_SQUEEZE_LOG))
			.build());
		registry.removeRecipes(er -> registry.getRecipeManager().get(er.getId())
				.map(it -> it instanceof SecretShapedRecipe || it instanceof LampRecipe
						|| (it instanceof CraftingRecipe cr && (cr.getIngredients().size() > 9
								|| (cr instanceof ShapedRecipe sr && (sr.getWidth() > 3 || sr.getHeight() > 3))))).orElse(false));
		
		registry.removeEmiStacks(EmiStack.of(YBlocks.DUST));
		registry.removeEmiStacks(EmiStack.of(YBlocks.RAFTER));
		registry.removeEmiStacks(EmiStack.of(YBlocks.GIANT_COBBLESTONE));
		registry.removeEmiStacks(EmiStack.of(YItems.LOOTBOX_OF_CONTINUITY));
		registry.removeEmiStacks(EmiStack.of(YItems.SPATULA));
		
		Function<Comparison, Comparison> compareNbt = c -> c.copy().nbt(true).build();
		registry.setDefaultComparison(YItems.MERCURIAL_POTION, compareNbt);
		registry.setDefaultComparison(YItems.MERCURIAL_SPLASH_POTION, compareNbt);
		registry.setDefaultComparison(YItems.LAZOR_EMITTER, compareNbt);
		registry.setDefaultComparison(YItems.LAMP, compareNbt);
		registry.setDefaultComparison(YItems.FIXTURE, compareNbt);
		registry.setDefaultComparison(YItems.CAGE_LAMP, compareNbt);
		registry.setDefaultComparison(YItems.PANEL, compareNbt);
	}

}
