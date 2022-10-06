package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.inventory.SSDScreenHandler;
import com.unascribed.yttr.mixin.accessor.client.AccessorHandledScreen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SSDScreen extends HandledScreen<SSDScreenHandler> {

	private static final Identifier BG = Yttr.id("textures/gui/ssd.png");
	private final PlayerInventory playerInventory;

	private float lastSizeLag;
	private float sizeLag;
	
	public SSDScreen(SSDScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		playerInventory = inventory;
		backgroundHeight = 133;
		playerInventoryTitleY = backgroundHeight - 94;
		lastSizeLag = -1;
		sizeLag = -1;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		if (!dragging) {
			drawMouseoverTooltip(matrices, mouseX, mouseY);
		}
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, BG);
		int x = (width-backgroundWidth)/2;
		int y = (height-backgroundHeight)/2;
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
	}
	
	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		if (sizeLag == -1) {
			lastSizeLag = handler.props.get(0);
			sizeLag = handler.props.get(0);
		}
		
		for (int i = 0; i < handler.slots.size(); ++i) {
			Slot slot = handler.slots.get(i);
			if (!slot.isEnabled()) {
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				((AccessorHandledScreen)this).yttr$drawSlot(matrices, slot);
			}
		}
		
		super.drawForeground(matrices, mouseX, mouseY);
		
		int slotsMissing = 8-handler.props.get(0);
		int w = slotsMissing*18;

		RenderSystem.setShaderTexture(0, BG);
		drawTexture(matrices, 151-w, 19, 126-w, 153, w, 18, 256, 256);
		
		w = calcPistonWidth();
		drawTexture(matrices, 156-w, 19, 0, 133, w, 20, 256, 256);
		drawTexture(matrices, 155, 19, 130, 133, 16, 20, 256, 256);
	}
	
	private int calcPistonWidth() {
		return Math.round((8-MathHelper.lerp(client.getTickDelta(), lastSizeLag, sizeLag))*18)+5;
	}

	private boolean dragging;
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			int x = (width-backgroundWidth)/2;
			int y = (height-backgroundHeight)/2;
			int w = calcPistonWidth();
			if (mouseX >= x+(156-w) && mouseX < x+(156-w)+6
					&& mouseY >= y+19 && mouseY < y+39) {
				dragging = true;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (dragging) {
			int x = (width-backgroundWidth)/2;
			int slot = (int) Math.round(((mouseX-x-7)/144)*8);
			if (slot > 0 && slot < 9 && slot != handler.props.get(0)) {
				client.interactionManager.clickButton(handler.syncId, slot);
			}
		}
		super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) dragging = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}
	
	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		lastSizeLag = sizeLag;
		float d = handler.props.get(0)-sizeLag;
		sizeLag += d/2;
	}
	
}
