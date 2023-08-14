package com.unascribed.yttr.mixin.effector.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {
	
	@Inject(at=@At("TAIL"), method="drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void drawItemInSlot(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
		var ctx = (GuiGraphics)(Object)this;
		if (stack.getItem() == YItems.EFFECTOR) {
			float dmg = YItems.EFFECTOR.getFuel(stack);
			float maxDmg = EffectorItem.MAX_FUEL;
			int w = Math.round(dmg * 13 / maxDmg);
			ctx.fill(RenderLayer.getGuiOverlay(), x + 2, y + 13, x + 2 + 13, y + 14, 100, 0xFFFFFFFF);
			ctx.fill(RenderLayer.getGuiOverlay(), x + 2, y + 13, x + 2 + w, y + 14, 100, 0xFF000000);
		}
	}
	
}
