package com.unascribed.yttr.compat.emi.stack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tessellator;

import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Axis;

public class BlockEmiStack extends ItemEmiStack {

	public BlockEmiStack(Block block) {
		super(new ItemStack(block));
	}

	@Override
	public void render(GuiGraphics ctx, int x, int y, float delta, int flags) {
		var matrices = ctx.getMatrices();
		MinecraftClient client = MinecraftClient.getInstance();
		ItemStack stack = getItemStack();
		if ((flags & 1) != 0) {
			VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().getBufferBuilder());
			int light = LightmapTextureManager.pack(15, 15);
			int overlay = OverlayTexture.DEFAULT_UV;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			matrices.push();
			matrices.translate(x+8, y+8, 0);
			matrices.scale(16, -16, 16);
			matrices.multiply(Axis.X_POSITIVE.rotationDegrees(-90));
			matrices.push();
			MinecraftClient.getInstance().getItemRenderer().renderItem(getItemStack(), ModelTransformationMode.NONE, light, overlay, matrices, vcp, client.world, 0);
			matrices.pop();
			vcp.draw();
			matrices.pop();
		}

		if ((flags & 2) != 0) {
			ctx.drawItemInSlot(client.textRenderer, stack, x, y, "");
		}

		if ((flags & 8) != 0) {
			EmiRender.renderRemainderIcon(this, ctx, x, y);
		}
	}
	
	@Override
	public boolean isUnbatchable() {
		return true;
	}

}
