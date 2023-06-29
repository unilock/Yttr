package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.inventory.CanFillerScreenHandler;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CanFillerScreen extends HandledScreen<CanFillerScreenHandler> {

	private static final Identifier BG = Yttr.id("textures/gui/can_filler.png");
	private final PlayerInventory playerInventory;
	
	public CanFillerScreen(CanFillerScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		playerInventory = inventory;
		backgroundWidth = 176;
		backgroundHeight = 201;
		playerInventoryTitleY = 107;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
		drawMouseoverTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, BG);
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		graphics.drawTexture(BG, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		
		for (Slot s : handler.slots) {
			if (s.inventory != playerInventory && !s.hasStack()) {
				graphics.drawTexture(BG, x+s.x, y+s.y, 176, s.id*16, 16, 16, 256, 256);
			}
		}
		
		float prog = handler.getWorkTime()/(float)handler.getMaxWorkTime();
		int p = Math.round(77*prog);
		int pStart = Math.min(p, 11);
		drawShadowedTexture(graphics, x+39, y+38, 0, 201, 4, pStart, 256, 256);
		drawShadowedTexture(graphics, x+133, y+38, 94, 201, 4, pStart, 256, 256);
		if (p > 11) {
			int pMid = Math.min(p-11, 36);
			int pMidr = 36-pMid;
			drawShadowedTexture(graphics, x+43, y+45, 4, 210, pMid, 4, 256, 256);
			drawShadowedTexture(graphics, x+97+pMidr, y+45, 58+pMidr, 210, pMid, 4, 256, 256);
		}
		if (p > 57) {
			int pEnd = Math.min(p-57, 20);
			drawShadowedTexture(graphics, x+80, y+56, 41, 222, 16, pEnd, 256, 256);
		}
	}

	private void drawShadowedTexture(GuiGraphics graphics, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		graphics.setShaderColor(0.4f, 0.4f, 0.4f, 1);
		graphics.drawTexture(BG, x, y+1, u, v, width, height, textureWidth, textureHeight);
		graphics.setShaderColor(1, 1, 1, 1);
		graphics.drawTexture(BG, x, y, u, v, width, height, textureWidth, textureHeight);
	}
	
}
