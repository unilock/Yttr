package com.unascribed.yttr.client.render;

import java.util.Map;
import java.util.WeakHashMap;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.lib39.util.api.DelegatingVertexConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.YRandom;
import com.unascribed.yttr.util.math.Interp;

import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.MathHelper;

public class PlatformsRenderer {
	
	private static final Identifier TEXTURE = Yttr.id("textures/block/continuous_platform_0.png");
	private static final Map<AbstractClientPlayerEntity, Integer> sneakTimes = new WeakHashMap<>();

	public static void render(ItemStack is, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
			MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, LivingEntity entity,
			float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
			float headPitch) {
		if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() == YItems.SUIT_BOOTS) return;
		boolean hasBoots = entity instanceof PlayerEntity ep && Yttr.earsAccess.isVisuallyWearingBoots(ep);
		if (model instanceof BipedEntityModel<?> bep) {
			VertexConsumer vc = vertexConsumer.getBuffer(TexturedRenderLayers.getEntityCutout());
			renderPlatform(vc, matrices, bep.leftLeg, hasBoots, light, true);
			renderPlatform(vc, matrices, bep.rightLeg, hasBoots, light, false);
		}
	}
	
	public static void renderWorld(WorldRenderContext wrc) {
		var matrices = wrc.matrixStack();
		var cam = wrc.camera();
		var cpos = cam.getPos();
		var layer = RenderLayer.getEntityTranslucent(TEXTURE, false);
		var vrc = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer vc = new DelegatingVertexConsumer(vrc.getBuffer(layer)) {
			private double vx, vy, vz;
			@Override
			public VertexConsumer vertex(double x, double y, double z) {
				vx = x;
				vy = y;
				vz = z;
				super.vertex(x, y, z);
				return this;
			}
			@Override
			public VertexConsumer color(float red, float green, float blue, float alpha) {
				double worldX = vx+cpos.x;
				double worldY = vy+cpos.y;
				double worldZ = vz+cpos.z;
				int rgb = calcColor((float)(vx+vz*40), worldX, worldY, worldZ);
				red = ((rgb>>16)&0xFF)/255f;
				green = ((rgb>>8)&0xFF)/255f;
				blue = (rgb&0xFF)/255f;
				super.color(red, green, blue, alpha);
				return this;
			}
		};
		matrices.push();
			matrices.translate(-cpos.x, -cpos.y, -cpos.z);
			for (var player : wrc.world().getPlayers()) {
				int sneakTime = sneakTimes.getOrDefault(player, 0);
				if (player.isSneaking() && sneakTime <= 0) {
					sneakTime = player.age;
					sneakTimes.put(player, sneakTime);
				}
				if (!player.isSneaking() && sneakTime > 0) {
					sneakTime = -player.age;
					sneakTimes.put(player, sneakTime);
				}
				float t = sneakTime;
				if (t < 0) t = player.age+t+wrc.tickDelta();
				else t = player.age-t+wrc.tickDelta();
				float p;
				if (player.isSneaking()) {
					p = MathHelper.clamp(t/10f, 0, 1);
				} else {
					p = 1-MathHelper.clamp(t/5f, 0, 1);
				}
				if (Yttr.isWearingPlatforms(player) && p > 0) {
					var pos = player.getLerpedPos(wrc.tickDelta());
					float a = Interp.sCurve5(p);
					renderDisc(matrices, vc, pos.x, pos.y, pos.z, a);
				}
			}
		matrices.pop();
		vrc.draw(layer);
	}
	
	public static void renderDisc(MatrixStack matrices, VertexConsumer vc, double x, double y, double z, float a0) {
		int overlay = OverlayTexture.DEFAULT_UV;
		int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;
		var mat = matrices.peek().getModel();
		var nmat = matrices.peek().getNormal();
		float TAU = (float)(Math.PI*2);
		int sides = 12;
		float sidesf = sides;
		float x0 = (float)x;
		float y0 = (float)(y+0.01);
		float z0 = (float)z;
		for (int i = 0; i < sides; i++) {
			float i2 = i+.5f;
			float i3 = i+1;
			float x1 = (float)x+MathHelper.sin((i/sidesf)*TAU);
			float z1 = (float)z+MathHelper.cos((i/sidesf)*TAU);
			float x2 = (float)x+MathHelper.sin((i2/sidesf)*TAU);
			float z2 = (float)z+MathHelper.cos((i2/sidesf)*TAU);
			float x3 = (float)x+MathHelper.sin((i3/sidesf)*TAU);
			float z3 = (float)z+MathHelper.cos((i3/sidesf)*TAU);
			float am = 1f;
			float x1d = x1-x0;
			float z1d = z1-z0;
			float x2d = x2-x0;
			float z2d = z2-z0;
			float x3d = x3-x0;
			float z3d = z3-z0;
			float a1 = (am-MathHelper.sqrt((x1d*x1d)+(z1d*z1d)))*a0;
			float a2 = (am-MathHelper.sqrt((x2d*x2d)+(z2d*z2d)))*a0;
			float a3 = (am-MathHelper.sqrt((x3d*x3d)+(z3d*z3d)))*a0;
			vc.vertex(mat, x0, y0, z0).color(1, 1, 1, a0).uv(0.5f-x0, 0.5f-z0).overlay(overlay).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mat, x1, y0, z1).color(1, 1, 1, a1).uv(0.5f-x1, 0.5f-z1).overlay(overlay).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mat, x2, y0, z2).color(1, 1, 1, a2).uv(0.5f-x2, 0.5f-z2).overlay(overlay).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mat, x3, y0, z3).color(1, 1, 1, a3).uv(0.5f-x3, 0.5f-z3).overlay(overlay).light(light).normal(nmat, 0, 1, 0).next();
		}
	}

	private static void renderPlatform(VertexConsumer vc, MatrixStack matrices, ModelPart part, boolean hasBoots, int light, boolean flip) {
		var mc = MinecraftClient.getInstance();
		var cam = mc.gameRenderer.getCamera();
		var cpos = cam.getPos();
		vc = new DelegatingVertexConsumer(vc) {
			@Override
			public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
				double worldX = x+cpos.x;
				double worldY = y+cpos.y;
				double worldZ = z+cpos.z;
				int rgb = calcColor((x*30)+((mc.player.age+mc.getTickDelta())/4), worldX, worldY, worldZ);
				red = ((rgb>>16)&0xFF)/255f;
				green = ((rgb>>8)&0xFF)/255f;
				blue = (rgb&0xFF)/255f;
				super.vertex(x, y, z, red, green, blue, alpha, u, v, overlay, light, normalX, normalY, normalZ);
			}
		};
		matrices.push();
			part.rotate(matrices);
			matrices.translate(0, 13.5f/16f, 0);
			float s = 4.05f/16f;
			if (hasBoots) {
				s = 4.75f/16f;
				matrices.translate(0.5f/16f*(flip?1:-1), 1/16f, 0);
			}
			matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(180));
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(180));
			matrices.scale(s, s, s);
			matrices.translate(-0.5, 0, -0.5);
			BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr", "platforms_model", "inventory"));
			for (var bq : bm.getQuads(null, null, YRandom.get())) {
				vc.bakedQuad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
			}
		matrices.pop();
	}

	private static int calcColor(float ofs, double worldX, double worldY, double worldZ) {
		float h1 = (float) ((worldX+worldY+worldZ+ofs)/40f)%1;
		if (h1 < 0) h1 += 1;
		int rgb = MathHelper.hsvToRgb(h1, 0.3f, 1f);
		return rgb;
	}
	
}
