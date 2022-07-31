package com.unascribed.yttr.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.ItemEmiStack;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class BlockEmiStack extends ItemEmiStack {

	public BlockEmiStack(Block block) {
		super(new ItemStack(block));
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float delta, int flags) {
		MinecraftClient client = MinecraftClient.getInstance();
		ItemStack stack = getItemStack();
		if ((flags & 1) != 0) {
			VertexConsumerProvider.Immediate vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			int light = LightmapTextureManager.pack(15, 15);
			int overlay = OverlayTexture.DEFAULT_UV;
			RenderSystem.setShaderColor(1, 1, 1, 1);
			matrices.push();
			matrices.translate(x+8, y+8, 0);
			matrices.scale(16, -16, 16);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
			matrices.push();
			MinecraftClient.getInstance().getItemRenderer().renderItem(getItemStack(), Mode.NONE, light, overlay, matrices, vcp, 0);
			matrices.pop();
			vcp.draw();
			matrices.pop();
		}

		if ((flags & 2) != 0) {
			client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, x, y, "");
		}

		if ((flags & 8) != 0) {
			EmiRender.renderRemainderIcon(this, matrices, x, y);
		}
	}

	@Override
	public void renderForBatch(VertexConsumerProvider vcp, MatrixStack matrices, int x, int y, int z, float delta) {
		ItemStack stack = this.getItemStack();
		ItemRenderer ir = MinecraftClient.getInstance().getItemRenderer();
		BakedModel model = ir.getHeldItemModel(stack, null, null, 0);
		
		matrices.push();
		try {
			matrices.translate(x, y, 100.0F + z + (model.hasDepth() ? 50 : 0));
			matrices.translate(8.0D, 8.0D, 0.0D);
			matrices.scale(16.0F, 16.0F, 16.0F);
			ir.renderItem(stack, Mode.NONE, false, matrices, vcp, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		} finally {
			matrices.pop();
		}
	}

}
