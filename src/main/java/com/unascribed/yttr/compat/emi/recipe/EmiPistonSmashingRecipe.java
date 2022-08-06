package com.unascribed.yttr.compat.emi.recipe;

import java.util.List;

import org.apache.commons.compress.utils.Lists;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.compat.emi.YttrEmiPlugin;
import com.unascribed.yttr.compat.emi.stack.BlockEmiStack;
import com.unascribed.yttr.compat.emi.widget.ColoredTextureWidget;
import com.unascribed.yttr.compat.emi.widget.RotatedTextureWidget;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.component.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class EmiPistonSmashingRecipe implements EmiRecipe {

	private final Identifier id;
	private final List<Block> input;
	private final List<Block> catalysts;
	private final EmiStack output;
	private final int cloudColor;
	private final EmiStack cloudOutput;
	
	public EmiPistonSmashingRecipe(PistonSmashingRecipe r) {
		this.id = r.getId();
		this.input = r.getInput().getMatchingBlocks();
		this.catalysts = r.getCatalyst().getMatchingBlocks();
		this.output = EmiStack.of(r.getOutput());
		this.cloudColor = r.getCloudColor();
		ItemStack multCloudOutput = r.getCloudOutput().copy();
		multCloudOutput.setCount(multCloudOutput.getCount()*r.getCloudSize());
		this.cloudOutput = EmiStack.of(multCloudOutput);
	}
	
	public EmiPistonSmashingRecipe(Identifier id, List<Block> input, List<Block> catalysts, EmiStack output, int cloudColor, EmiStack cloudOutput) {
		this.id = id;
		this.input = input;
		this.catalysts = catalysts;
		this.output = output;
		this.cloudColor = cloudColor;
		this.cloudOutput = cloudOutput;
	}
	
	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.PISTON_SMASHING;
	}
	
	@Override
	public int getDisplayHeight() {
		return 43;
	}
	
	@Override
	public int getDisplayWidth() {
		return 118;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		List<EmiIngredient> li = Lists.newArrayList();
		if (!cloudOutput.isEmpty()) li.add(EmiStack.of(Items.GLASS_BOTTLE, cloudOutput.getAmount()));
		li.add(EmiIngredient.of(input.stream().map(EmiStack::of).toList()));
		return li;
	}
	
	@Override
	public List<EmiIngredient> getCatalysts() {
		return List.of(EmiIngredient.of(catalysts.stream().map(EmiStack::of).toList()));
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		List<EmiStack> li = Lists.newArrayList();
		if (!cloudOutput.isEmpty()) li.add(cloudOutput);
		if (!output.isEmpty()) li.add(output);
		return li;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int x = (widgets.getWidth()/2)-24;
		widgets.addSlot(EmiIngredient.of(input.stream().map(BlockEmiStack::new).toList()), x, 24)
			.drawBack(false);
		widgets.addSlot(EmiIngredient.of(catalysts.stream().map(BlockEmiStack::new).toList()), x-16, 24)
			.drawBack(false);
		widgets.addSlot(EmiIngredient.of(catalysts.stream().map(BlockEmiStack::new).toList()), x+16, 24)
			.drawBack(false);
		widgets.add(new RotatedTextureWidget(new Identifier("textures/block/piston_side.png"), x-31, 25, 16, 16, 0, 0, 16, 16, 16, 16,
				Vec3f.POSITIVE_Z.getDegreesQuaternion(90)));
		x += 8;
		widgets.addTexture(Yttr.id("textures/gui/curved_arrow.png"), x, 3, 16, 16, 0, 0, 16, 16, 16, 16);
		x += 20;
		if (!output.isEmpty()) {
			widgets.addSlot(output, x, 0)
				.recipeContext(this);
			x += 22;
		}
		if (!cloudOutput.isEmpty()) {
			widgets.add(new ColoredTextureWidget(new Identifier("textures/particle/effect_4.png"), x, 6, 8, 8, 0, 0, 8, 8, 8, 8, cloudColor));
			x += 12;
			widgets.addSlot(cloudOutput, x, 0)
				.recipeContext(this)
				.appendTooltip(new TranslatableComponent("emi.category.yttr.piston_smashing.cloud_output_hint"));
			x += 20;
		}
	}

}
