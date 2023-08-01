package com.unascribed.yttr.client.render;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.lib39.util.api.DelegatingVertexConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mixin.accessor.client.AccessorItemRenderer;
import com.unascribed.yttr.util.YRandom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;

public class RifleItemRenderer extends IHasAClient {
	
	private static final Identifier CHAMBER_TEXTURE = Yttr.id("textures/rifle_chamber.png");
	
	private static final ModelIdentifier RIFLE_BASE_MODEL = new ModelIdentifier("yttr", "rifle_base", "inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_MODEL = new ModelIdentifier("yttr", "rifle_chamber", "inventory");
	private static final ModelIdentifier RIFLE_CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr", "rifle_chamber_glass", "inventory");
	
	private static final UVObserver uvo = new UVObserver();

	public static void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		float tickDelta = mc.getTickDelta();
		matrices.pop();
		boolean fp = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND;
		boolean inUse = fp && mc.player != null && mc.player.isUsingItem();
		float useTime = inUse ? ((RifleItem)stack.getItem()).calcAdjustedUseTime(stack, mc.player.getItemUseTimeLeft()-tickDelta) : 0;
		if (useTime > 80) {
			float a = (useTime-80)/40f;
			a = a*a;
			if (stack.getItem() == YItems.RIFLE_REINFORCED) {
				a /= 3;
			}
			float f = 50;
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			matrices.translate((tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a);
		}
		BakedModel base = mc.getBakedModelManager().getModel(RIFLE_BASE_MODEL);
		BakedModel chamber = mc.getBakedModelManager().getModel(RIFLE_CHAMBER_MODEL);
		BakedModel chamberGlass = mc.getBakedModelManager().getModel(RIFLE_CHAMBER_GLASS_MODEL);
		boolean leftHanded = mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND;
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, base);
		if (fp) {
			if (inUse) {
				RenderLayer layer = RenderLayer.getEntityCutoutNoCull(CHAMBER_TEXTURE);
				uvo.reset();
				for (BakedQuad quad : chamber.getQuads(null, null, YRandom.get())) {
					uvo.bakedQuad(matrices.peek(), quad, 1, 1, 1, 1, 1);
				}
				float minU = uvo.getMinU();
				float minV = uvo.getMinV();
				float maxU = uvo.getMaxU();
				float maxV = uvo.getMaxV();
				int frame = useTime < 70 ? (int)((useTime/70f)*36) : 34+(int)(mc.world.getTime()%2);
				renderItem(stack, mode, leftHanded, matrices, junk -> new DelegatingVertexConsumer(vertexConsumers.getBuffer(layer)) {
					@Override
					public VertexConsumer uv(float u, float v) {
						if (u >= minU && u <= maxU) {
							u = ((u-minU)/(maxU-minU));
						} else {
							System.out.println("U?? "+u+"; "+minU+"/"+maxU);
							u = 0;
						}
						if (v >= minV && v <= maxV) {
							v = ((frame*3)/108f)+(((v-minV)/(maxV-minV))*(3/108f));
						} else {
							System.out.println("V?? "+v+"; "+minV+"/"+maxV);
							v = 0;
						}
						return super.uv(u, v);
					}
					
					@Override
					public VertexConsumer color(int red, int green, int blue, int alpha) {
						int c = ((RifleItem)stack.getItem()).getMode(stack).color;
						return super.color((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF, 255);
					}
				}, light, overlay, chamber);
				if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw(layer);
			}
		}
		RenderSystem.disableCull();
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, chamberGlass);
		if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		RenderSystem.enableCull();
		matrices.push();
	}

	public static void registerModels(Consumer<Identifier> out) {
		out.accept(RIFLE_BASE_MODEL);
		out.accept(RIFLE_CHAMBER_MODEL);
		out.accept(RIFLE_CHAMBER_GLASS_MODEL);
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
	


	private static void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
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
