package com.unascribed.yttr.client.render;

import java.util.List;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.mixin.accessor.client.AccessorItemRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.World;

public class LowSodiumItemRenderer {

	public static void renderItem(ItemStack item, ModelTransformationMode modelTransformationMode, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int seed) {
		renderItem(item, modelTransformationMode, false, matrices, vertexConsumers, light, overlay, MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, world, null, seed));
	}
	
	// Copies of vanilla methods (with irrelevant paths removed) so that Sodium doesn't break them.
	public static void renderItem(
			ItemStack stack,
			ModelTransformationMode modelTransformationMode,
			boolean leftHanded,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			int overlay,
			BakedModel model) {
		if (!stack.isEmpty()) {
			matrices.push();
	
			model.getTransformation().getTransformation(modelTransformationMode).apply(leftHanded, matrices);
			matrices.translate(-0.5F, -0.5F, -0.5F);
			boolean bl2 = true;
	
			RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
			VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
	
			renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
	
			matrices.pop();
		}
	}

	static void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
		RandomGenerator randomGenerator = RandomGenerator.createLegacy();
	
		for(Direction direction : Direction.values()) {
			randomGenerator.setSeed(42L);
			renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, randomGenerator), stack, light, overlay);
		}
	
		randomGenerator.setSeed(42L);
		renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, randomGenerator), stack, light, overlay);
	}

	private static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
		var colors = ((AccessorItemRenderer)MinecraftClient.getInstance().getItemRenderer()).yttr$getColors();
		boolean bl = !stack.isEmpty();
		MatrixStack.Entry entry = matrices.peek();
	
		for(BakedQuad bakedQuad : quads) {
			int i = -1;
			if (bl && bakedQuad.hasColor()) {
				i = colors.getColor(stack, bakedQuad.getColorIndex());
			}
	
			float f = (i >> 16 & 0xFF) / 255.0F;
			float g = (i >> 8 & 0xFF) / 255.0F;
			float h = (i & 0xFF) / 255.0F;
			vertices.bakedQuad(entry, bakedQuad, f, g, h, light, overlay);
		}
	}

}
