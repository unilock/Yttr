package com.unascribed.yttr.client;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.mixin.accessor.client.AccessorParticle;
import com.unascribed.yttr.mixin.accessor.client.AccessorParticleManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vector4f;
import net.minecraft.util.profiler.Profiler;

public class GuiParticleManager extends ParticleManager {

	public GuiParticleManager(ClientWorld world, TextureManager textureManager) {
		super(world, new UnregisterableTextureManager(textureManager));
		this.particleAtlasTexture = MinecraftClient.getInstance().particleManager.particleAtlasTexture;
	}
	
	@Override
	protected void registerDefaultFactories() {
		var pm = MinecraftClient.getInstance().particleManager;
		((AccessorParticleManager)this).yttr$getFactories().putAll(((AccessorParticleManager)pm).yttr$getFactories());
		((AccessorParticleManager)this).yttr$getSpriteAwareFactories().putAll(((AccessorParticleManager)pm).yttr$getSpriteAwareFactories());
	}
	
	@Override
	public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		Particle p = super.addParticle(parameters, x/16, (MinecraftClient.getInstance().getWindow().getScaledHeight()-y)/16, z, velocityX, velocityY, velocityZ);
		((AccessorParticle)p).yttr$setCollidesWithWorld(false);
		return p;
	}
	
	@Override
	public void renderParticles(MatrixStack matrices, Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
		matrices.push();
		matrices.translate(0, MinecraftClient.getInstance().getWindow().getScaledHeight()/16, 0);
		matrices.scale(1, -1, 1);
		RenderSystem.disableCull();
		RenderSystem.enableDepthTest();
		
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bldr = tess.getBufferBuilder();
		VertexConsumer vc = new DelegatingVertexConsumer(bldr) {
			@Override
			public VertexConsumer vertex(double x, double y, double z) {
				Vector4f vec = new Vector4f((float)x, (float)y, (float)z, 1);
				vec.transform(matrices.peek().getPosition());
				return super.vertex(vec.getX(), vec.getY(), vec.getZ());
			}
		};

		for (Map.Entry<ParticleTextureSheet, Queue<Particle>> en : ((AccessorParticleManager)this).yttr$getParticles().entrySet()) {
			if (en.getValue() != null) {
				RenderSystem.setShader(GameRenderer::getParticleShader);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				en.getKey().begin(bldr, MinecraftClient.getInstance().getTextureManager());

				for (Particle p : en.getValue()) {
					p.buildGeometry(vc, camera, tickDelta);
				}

				en.getKey().draw(tess);
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		matrices.pop();
	}

	public static class UnregisterableTextureManager extends TextureManager {

		private final TextureManager delegate;
		
		public UnregisterableTextureManager(TextureManager delegate) {
			super(MinecraftClient.getInstance().getResourceManager());
			this.delegate = delegate;
		}

		@Override
		public void registerTexture(Identifier id, AbstractTexture texture) {
			// no
		}

		@Override
		public void bindTexture(Identifier id) {
			delegate.bindTexture(id);
		}

		@Override
		public String getName() {
			return delegate.getName();
		}

		@Override
		public int hashCode() {
			return delegate.hashCode();
		}

		@Override
		public AbstractTexture getTexture(Identifier id) {
			return delegate.getTexture(id);
		}

		@Override
		public AbstractTexture getOrDefault(Identifier id, AbstractTexture fallback) {
			return delegate.getOrDefault(id, fallback);
		}

		@Override
		public boolean equals(Object obj) {
			return delegate.equals(obj);
		}

		@Override
		public Identifier registerDynamicTexture(String prefix, NativeImageBackedTexture texture) {
			return delegate.registerDynamicTexture(prefix, texture);
		}

		@Override
		public CompletableFuture<Void> loadTextureAsync(Identifier id, Executor executor) {
			return delegate.loadTextureAsync(id, executor);
		}

		@Override
		public void tick() {
			delegate.tick();
		}

		@Override
		public void destroyTexture(Identifier id) {
			delegate.destroyTexture(id);
		}

		@Override
		public void close() {
			delegate.close();
		}

		@Override
		public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
			return delegate.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

	}
	
}
