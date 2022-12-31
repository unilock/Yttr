package com.unascribed.yttr.compat.emi.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.emi.emi.api.widget.TextureWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ColoredTextureWidget extends TextureWidget {

	private final int color;
	
	public ColoredTextureWidget(Identifier texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
		super(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
		this.color = color;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(NativeImage.getBlue(color)/255f, NativeImage.getGreen(color)/255f, NativeImage.getRed(color)/255f, NativeImage.getAlpha(color)/255f);
		RenderSystem.setShaderTexture(0, this.texture);
		DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

}
