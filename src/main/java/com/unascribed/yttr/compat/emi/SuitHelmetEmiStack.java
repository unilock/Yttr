package com.unascribed.yttr.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.screen.handled.SuitStationScreen;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;

import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class SuitHelmetEmiStack extends ItemEmiStack {

	public SuitHelmetEmiStack(ItemStack stack) {
		super(stack);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float delta) {
		super.render(matrices, x, y, delta);
		matrices.push();
			matrices.translate(0, 0, 200);
			RenderSystem.setShaderTexture(0, SuitStationScreen.BG);
			LampColor color = LampBlockItem.getColor(getItemStack());
			RenderSystem.setShaderColor(((color.glowColor>>16)&0xFF)/255f, ((color.glowColor>>8)&0xFF)/255f, (color.glowColor&0xFF)/255f, 1);
			DrawableHelper.drawTexture(matrices, x, y, 231, 0, 16, 16, 256, 256);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		matrices.pop();
	}
	
	@Override
	public boolean isUnbatchable() {
		return true;
	}

}
