package com.unascribed.yttr.compat.emi.recipe;

import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.compat.emi.YttrEmiPlugin;
import com.unascribed.yttr.compat.emi.widget.RuinedInputWidget;
import com.unascribed.yttr.compat.emi.widget.RuinedOutputWidget;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.util.Identifier;

public class EmiForgottenRecipe implements EmiRecipe {

	private final Identifier id;
	private final Set<Integer> emptySlots;
	private final EmiStack result;
	
	public EmiForgottenRecipe(Identifier id, Set<Integer> emptySlots, EmiStack result) {
		this.id = id;
		this.emptySlots = emptySlots;
		this.result = result;
	}
	
	@Override
	public EmiRecipeCategory getCategory() {
		return YttrEmiPlugin.FORGOTTEN_CRAFTING;
	}
	
	@Override
	public int getDisplayHeight() {
		return 58;
	}
	
	@Override
	public int getDisplayWidth() {
		return 118;
	}
	
	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	public List<EmiIngredient> getInputs() {
		return List.of();
	}
	
	@Override
	public List<EmiStack> getOutputs() {
		return List.of(result);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int x = widgets.getWidth()/2;
		int y = 2;
		double aspect = 696/324D;
		int h = 54;
		int w = (int)(h*aspect);
		widgets.addDrawable(0, 0, w, h, (ctx, mouseX, mouseY, delta) -> {
			var matrices = ctx.getMatrices();
			var tex = new Identifier(id.getNamespace(), "textures/gui/ruined_recipe/"+id.getPath()+".png");
			var overlay = new Identifier("yttr", "textures/gui/ruined_recipe/overlay.png");
			var border = new Identifier("yttr", "textures/gui/ruined_recipe/border.png");
			matrices.push();
			matrices.translate(x-(w/2f), y, 0);
			ctx.drawTexture(tex, 0, 0, 0, 0, w, h, w, h);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(SourceFactor.ZERO, DestFactor.SRC_COLOR); // multiply
			ctx.drawTexture(overlay, 0, 0, 0, 0, w, h, w, h);
			
			double wFactor = 872/696D;
			double hFactor = 500/324D;
			
			matrices.push();
			matrices.translate(w/2f, h/2f, 0);
			matrices.scale((float)wFactor, (float)hFactor, 1);
			matrices.translate(-w/2f, -h/2f, 0);
			RenderSystem.defaultBlendFunc();
			ctx.drawTexture(border, 0, 0, 0, 0, w, h, w, h);
			matrices.pop();
			matrices.pop();
		});
		int bX = x-(w/2);
		int bY = y;
		widgets.add(new RuinedOutputWidget(result, bX+90, bY+14).recipeContext(this)
				.large(true)
				.drawBack(false));
		for (int i = 0; i < 9; i++) {
			widgets.add(new RuinedInputWidget(bX+((i%3)*18), bY+((i/3)*18))
					.disableTooltip(emptySlots.contains(i))
					.drawBack(false));
		}
	}
	
}
