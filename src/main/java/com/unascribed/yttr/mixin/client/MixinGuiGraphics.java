package com.unascribed.yttr.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YttrClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.item.ItemRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {

	@Inject(at=@At("HEAD"), method="drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V")
	private void innerRenderInGuiHead(CallbackInfo ci) {
		YttrClient.renderingGui = true;
	}
	
	@Inject(at=@At("RETURN"), method="drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V")
	private void innerRenderInGuiReturn(CallbackInfo ci) {
		YttrClient.renderingGui = false;
	}
	
}
