package com.unascribed.yttr.client.screen.handled;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.inventory.ProjectTableScreenHandler;
import com.unascribed.yttr.mixin.accessor.client.AccessorRecipeBookWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipe.book.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipe.book.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ProjectTableScreen extends HandledScreen<ProjectTableScreenHandler> implements RecipeBookProvider {
	private static final Identifier TEXTURE = Yttr.id("textures/gui/project_table.png");
	private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
	private final RecipeBookWidget recipeBook = new RecipeBookWidget();
	private boolean narrow;
	
	private int lastRevision = -1;

	public ProjectTableScreen(ProjectTableScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 176;
		this.backgroundHeight = 215;
		this.playerInventoryTitleY = this.backgroundHeight - 93;
	}

	@Override
	protected void init() {
		super.init();
		this.narrow = this.width < 379;
		this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
		this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
		addDrawable(this.recipeBook);
		this.setInitialFocus(this.recipeBook);
		this.addDrawableChild(new TexturedButtonWidget(this.x + 5, this.y + 34, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (buttonWidget) -> {
			this.recipeBook.reset();
			this.recipeBook.toggleOpen();
			this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
			buttonWidget.setPosition(this.x + 5, this.y + 34);
		}));
	}

	@Override
	public void handledScreenTick() {
		if (lastRevision != handler.getRevision()) {
			lastRevision = handler.getRevision();
			if (recipeBook.isOpen()) {
				((AccessorRecipeBookWidget)recipeBook).yttr$refreshInputs();
			}
		}
		this.recipeBook.update();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.renderBackground(graphics);
		if (this.recipeBook.isOpen() && this.narrow) {
			this.drawBackground(graphics, delta, mouseX, mouseY);
			this.recipeBook.render(graphics, mouseX, mouseY, delta);
		} else {
			this.recipeBook.render(graphics, mouseX, mouseY, delta);
			super.render(graphics, mouseX, mouseY, delta);
			this.recipeBook.drawGhostSlots(graphics, this.x, this.y, true, delta);
		}

		this.drawMouseoverTooltip(graphics, mouseX, mouseY);
		this.recipeBook.drawTooltip(graphics, this.x, this.y, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, TEXTURE);
		int x = this.x;
		int y = (this.height - this.backgroundHeight) / 2;
		graphics.drawTexture(TEXTURE, x+0, y+0, 0, 0, backgroundWidth, backgroundHeight);
	}
	
	@Override
	protected void drawForeground(GuiGraphics graphics, int mouseX, int mouseY) {
		graphics.drawText(textRenderer, title, (backgroundWidth-textRenderer.getWidth(title))/2, titleY, 4210752, false);
		graphics.drawText(textRenderer, playerInventoryTitle, playerInventoryTitleX, playerInventoryTitleY, 4210752, false);
		
//		for (Slot s : handler.slots) {
//			drawCenteredText(matrices, textRenderer, Integer.toString(s.id), s.x+8, s.y+4, 0xFFCC00);
//		}
	}

	@Override
	protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
		return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
			this.setFocusedChild(this.recipeBook);
			return true;
		} else {
			return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
		boolean bl = mouseX < left || mouseY < top || mouseX >= left + this.backgroundWidth || mouseY >= top + this.backgroundHeight;
		return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
	}

	@Override
	protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
		super.onMouseClick(slot, slotId, button, actionType);
		this.recipeBook.slotClicked(slot);
	}

	@Override
	public void refreshRecipeBook() {
		this.recipeBook.refresh();
	}

	@Override
	public void removed() {
		super.removed();
	}

	@Override
	public RecipeBookWidget getRecipeBookWidget() {
		return this.recipeBook;
	}
}
