package com.unascribed.yttr.mixin.subgroup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.ItemSubGroup;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mixinsupport.ItemGroupParent;
import com.unascribed.yttr.mixinsupport.SubTabLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> implements SubTabLocation {
	
	public MixinCreativeInventoryScreen(CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow
	private float scrollPosition;
	
	@Shadow
	public abstract int getSelectedTab();
	
	private int yttr$x, yttr$y, yttr$w, yttr$h;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/gui/screen/ingame/CreativeInventoryScreen.drawMouseoverTooltip(Lnet/minecraft/client/util/math/MatrixStack;II)V"),
			method="render")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ItemGroup selected = ItemGroup.GROUPS[getSelectedTab()];
		ItemGroupParent parent = (ItemGroupParent)selected;
		if (parent.yttr$getChildren() != null && !parent.yttr$getChildren().isEmpty()) {
			if (!selected.shouldRenderName()) {
				ItemGroup child = parent.yttr$getSelectedChild();
				float x = textRenderer.draw(matrices, selected.getTranslationKey(), this.x+8, this.y+6, 4210752);
				if (child != null) {
					x = textRenderer.draw(matrices, " ", x, this.y+6, 4210752);
					x = textRenderer.draw(matrices, child.getTranslationKey(), x, this.y+6, 4210752);
				}
			}
			int ofs = 5;
			int x = this.x-ofs;
			int y = this.y+6;
			int tw = 56;
			yttr$x = x-tw;
			yttr$y = y;
			for (ItemSubGroup child : parent.yttr$getChildren()) {
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.setShaderTexture(0, Yttr.id("textures/gui/subtab.png"));
				boolean childSelected = child == parent.yttr$getSelectedChild();
				int bgV = childSelected ? 11 : 0;
				drawTexture(matrices, x-tw, y, 0, bgV, tw+ofs, 11, 70, 22);
				drawTexture(matrices, this.x, y, 64, bgV, 6, 11, 70, 22);
				RenderSystem.setShaderTexture(0, Yttr.id("textures/gui/tinyfont.png"));
				String str = child.getTranslationKey().getString();
				for (int i = str.length()-1; i >= 0; i--) {
					char c = str.charAt(i);
					if (c > 0x7F) continue;
					int u = (c%16)*4;
					int v = (c/16)*6;
					RenderSystem.setShaderColor(0, 0, 0, 1);
					drawTexture(matrices, x, y+3, u, v, 4, 6, 64, 48);
					x -= 4;
				}
				x = this.x-ofs;
				y += 10;
			}
			yttr$w = tw+ofs;
			yttr$h = y-yttr$y;
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
	
	@Inject(at=@At("HEAD"), method="mouseClicked", cancellable=true)
	public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> ci) {
		ItemGroup selected = ItemGroup.GROUPS[getSelectedTab()];
		ItemGroupParent parent = (ItemGroupParent)selected;
		if (parent.yttr$getChildren() != null && !parent.yttr$getChildren().isEmpty()) {
			int x = yttr$x;
			int y = yttr$y;
			int w = yttr$w;
			for (ItemSubGroup child : parent.yttr$getChildren()) {
				if (mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+11) {
					parent.yttr$setSelectedChild(child);
					handler.itemList.clear();
					selected.appendStacks(handler.itemList);
					this.scrollPosition = 0.0F;
					handler.scrollItems(0.0F);
					ci.setReturnValue(true);
					return;
				}
				y += 10;
			}
		}
	}

	@Override
	public int yttr$getX() {
		return yttr$x;
	}
	
	@Override
	public int yttr$getY() {
		return yttr$y;
	}
	
	@Override
	public int yttr$getW() {
		return yttr$w;
	}
	
	@Override
	public int yttr$getH() {
		return yttr$h;
	}
	
}
