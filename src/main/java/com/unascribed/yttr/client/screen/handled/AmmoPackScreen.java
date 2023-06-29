package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.inventory.AmmoPackScreenHandler;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AmmoPackScreen extends HandledScreen<AmmoPackScreenHandler> {

	private static final Identifier BG = Yttr.id("textures/gui/ammo_pack.png");
	
	private final Screen parent;
	
	private final PlayerInventory playerInventory;
	
	public AmmoPackScreen(AmmoPackScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		parent = MinecraftClient.getInstance().currentScreen;
		playerInventory = inventory;
		backgroundWidth = 176;
		backgroundHeight = 163;
		playerInventoryTitleY = 70;
	}
	
	@Override
	public void closeScreen() {
		super.closeScreen();
		this.client.setScreen(parent);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
		drawMouseoverTooltip(graphics, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		graphics.drawTexture(BG, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
		
		for (Slot s : handler.slots) {
			if (s.inventory != playerInventory && !s.hasStack()) {
				graphics.drawTexture(BG, x+s.x, y+s.y, 176, 0, 16, 16, 256, 256);
			}
		}
		
	}
	
}
