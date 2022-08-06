package com.unascribed.yttr.client.particle;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.unascribed.yttr.Yttr;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class VoidBallParticle extends BillboardParticle {
	
	private static final Identifier TEXTURE = Yttr.id("textures/particle/void_ball.png");
	
	private static VertexBuffer buf1, buf2, buf3;
	
	public VoidBallParticle(ClientWorld world, double x, double y, double z, float r) {
		super(world, x, y, z);
		this.scale = r;
		maxAge = 40;
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ONE, DstFactor.ONE_MINUS_SRC_ALPHA);
		if (!MinecraftClient.isFancyGraphicsOrBetter()) {
			if (buf1 != null) {
				buf1.close();
				buf2.close();
				buf3.close();
				buf1 = buf2 = buf3 = null;
			}
			RenderSystem.setShaderTexture(0, TEXTURE);
			Tessellator.getInstance().getBufferBuilder().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
			setColorAlpha(age < 10 ? 1 : 1-((age-10)/(float)(maxAge-10)));
			super.buildGeometry(vertexConsumer, camera, tickDelta);
			Tessellator.getInstance().draw();
			return;
		}
		if (buf1 == null) {
			buf1 = new VertexBuffer();
			buf2 = new VertexBuffer();
			buf3 = new VertexBuffer();
			BufferBuilder bb = Tessellator.getInstance().getBufferBuilder();
			
			final float PI = (float)Math.PI;
			int slices = 40;
			int stacks = 40;
			float radius = 1;
			
			// ported from LWJGL2 GLU Sphere https://github.com/LWJGL/lwjgl/blob/master/src/java/org/lwjgl/util/glu/Sphere.java
		
			float x, y, z;
			float rho, drho, theta, dtheta;
			int i, j, k, l, imin, imax;
			float nsign;

			nsign = 1.0f;

			drho = PI / stacks;
			dtheta = 2.0f * PI / slices;
			
			// draw +Z end as a triangle fan
			bb.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
			bb.vertex(0.0f, 0.0f, nsign * radius).next();
			for (j = 0; j <= slices; j++) {
				theta = (j == slices) ? 0.0f : j * dtheta;
				x = -MathHelper.sin(theta) * MathHelper.sin(drho);
				y = MathHelper.cos(theta) * MathHelper.sin(drho);
				z = nsign * MathHelper.cos(drho);
				bb.vertex(x * radius, y * radius, z * radius).next();
			}
			bb.end();
			buf1.upload(bb);
			
			imin = 1;
			imax = stacks - 1;

			// draw intermediate stacks as quad strips
			// actually quads, mojang ate quad strips in 1.17
			bb.begin(DrawMode.QUADS, VertexFormats.POSITION);
			for (i = imin; i < imax; i++) {
				rho = i * drho;
				for (j = 0; j < slices; j++) {
					int[][] orders = {
						{0, 1},
						{1, 0}
					};
					for (k = 0; k < 2; k++) {
						l = j+k;
						theta = (l == slices) ? 0.0f : l * dtheta;
						for (int w : orders[k]) {
							if (w == 0) {
								x = -MathHelper.sin(theta) * MathHelper.sin(rho);
								y = MathHelper.cos(theta) * MathHelper.sin(rho);
								z = nsign * MathHelper.cos(rho);
								bb.vertex(x * radius, y * radius, z * radius).next();
							}
							if (w == 1) {
								x = -MathHelper.sin(theta) * MathHelper.sin(rho + drho);
								y = MathHelper.cos(theta) * MathHelper.sin(rho + drho);
								z = nsign * MathHelper.cos(rho + drho);
								bb.vertex(x * radius, y * radius, z * radius).next();
							}
						}
					}
				}
			}
			bb.end();
			buf2.upload(bb);
			
			// draw -Z end as a triangle fan
			bb.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
			bb.vertex(0.0f, 0.0f, -radius * nsign).next();
			rho = PI - drho;
			for (j = slices; j >= 0; j--) {
				theta = (j == slices) ? 0.0f : j * dtheta;
				x = -MathHelper.sin(theta) * MathHelper.sin(rho);
				y = MathHelper.cos(theta) * MathHelper.sin(rho);
				z = nsign * MathHelper.cos(rho);
				bb.vertex(x * radius, y * radius, z * radius).next();
			}
			bb.end();
			buf3.upload(bb);
		}
		
		Vec3d cam = camera.getPos();
		float ox = (float)(MathHelper.lerp(tickDelta, prevPosX, x) - cam.getX());
		float oy = (float)(MathHelper.lerp(tickDelta, prevPosY, y) - cam.getY());
		float oz = (float)(MathHelper.lerp(tickDelta, prevPosZ, z) - cam.getZ());
		MatrixStack ms = RenderSystem.getModelViewStack();
		ms.push();
		ShaderProgram old = RenderSystem.getShader();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0, 0, 0, age < 10 ? 1 : 1-((age-10)/(float)(maxAge-10)));
		ms.translate(ox, oy, oz);
		ms.scale(scale-0.5f, scale-0.5f, scale-0.5f);
		RenderSystem.depthMask(false);
		RenderSystem.disableCull();
		
		buf1.setShader(ms.peek().getPosition(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
		buf2.setShader(ms.peek().getPosition(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
		buf3.setShader(ms.peek().getPosition(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
		
		VertexBuffer.unbind();
		ms.pop();
		RenderSystem.setShader(() -> old);
		RenderSystem.depthMask(true);
	}

	@Override
	protected float getMinU() {
		return 0;
	}

	@Override
	protected float getMaxU() {
		return 1;
	}

	@Override
	protected float getMinV() {
		return 0;
	}

	@Override
	protected float getMaxV() {
		return 1;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.CUSTOM;
	}


}
