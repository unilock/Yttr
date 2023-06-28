package com.unascribed.yttr.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.SplashTextRenderer;
import net.minecraft.util.math.Axis;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
@Mixin(SplashTextRenderer.class)
public class MixinSplashTextRenderer {

	@Shadow
	private String splashText;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"), method = "render")
	public void render(GuiGraphics graphics, int screenWidth, TextRenderer textRenderer, int alpha, CallbackInfo ci) {
		if ("Vertical!".equals(splashText)) {
			graphics.getMatrices().multiply(Axis.Z_POSITIVE.rotationDegrees(90));
		}
	}
	
}
