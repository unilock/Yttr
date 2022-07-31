package com.unascribed.yttr.compat.emi;

import dev.emi.emi.api.widget.TextureWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

public class RotatedTextureWidget extends TextureWidget {

	private final Quaternion rot;
	
	public RotatedTextureWidget(Identifier texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Quaternion rot) {
		super(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
		this.rot = rot;
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		matrices.translate(x, y, 0);
		matrices.translate(width/2, height/2, 0);
		matrices.multiply(rot);
		matrices.translate(-width/2, -height/2, 0);
		matrices.translate(-x, -y, 0);
		super.render(matrices, mouseX, mouseY, delta);
		matrices.pop();
	}

}
