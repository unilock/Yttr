package com.unascribed.yttr.mixin.client;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.GuiParticleManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorHandledScreen;
import com.unascribed.yttr.mixinsupport.ParticleScreen;
import com.unascribed.yttr.util.DummyClientWorld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
@Mixin(Screen.class)
public class MixinScreen implements ParticleScreen {

	private DummyClientWorld yttr$particleWorld;
	private ParticleManager yttr$particleManager;
	private Camera yttr$dummyCamera;
	private boolean yttr$hasRenderedParticles;

	@Inject(at=@At("RETURN"), method="render")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		renderParticles(matrices, delta);
		yttr$hasRenderedParticles = false;
	}

	@Inject(at=@At("RETURN"), method="tick")
	public void tick(CallbackInfo ci) {
		if (yttr$particleManager != null) {
			yttr$particleManager.tick();
		}
	}
	
	@Inject(at=@At("HEAD"), method="renderTooltipFromComponents")
	public void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, CallbackInfo ci) {
		renderParticles(matrices, MinecraftClient.getInstance().getTickDelta());
	}
	
	@Unique
	private void renderParticles(MatrixStack matrices, float delta) {
		if (yttr$particleManager != null && !yttr$hasRenderedParticles) {
			var mc = MinecraftClient.getInstance();
			if (mc.options.debugEnabled) {
				mc.textRenderer.draw(matrices, yttr$particleManager.getDebugString()+"gp", 2, 2, 0xFFFFCC00);
			}
			matrices.push();
			Object self = this;
			if (self instanceof AccessorHandledScreen hs) {
				matrices.translate(hs.yttr$getX(), hs.yttr$getY(), 0);
			}
			matrices.translate(0, 0, 80);
			matrices.scale(16, 16, 0.001f);
			var mvs = RenderSystem.getModelViewStack();
			mvs.push();
			mvs.loadIdentity();
			yttr$particleManager.renderParticles(matrices, mc.getBufferBuilders().getEntityVertexConsumers(),
					mc.gameRenderer.getLightmapTextureManager(), yttr$dummyCamera, delta);
			mvs.pop();
			matrices.pop();
		}
		yttr$hasRenderedParticles = true;
	}

	@Override
	public ParticleManager yttr$getParticleManager() {
		if (yttr$particleManager == null) {
			yttr$dummyCamera = new Camera() {
				{
					setRotation(0, 0);
					setPos(Vec3d.ZERO);
				}
			};
			yttr$particleWorld = DummyClientWorld.create();
			yttr$particleManager = new GuiParticleManager(yttr$particleWorld,
					MinecraftClient.getInstance().getTextureManager());
			yttr$particleWorld.setParticleManager(yttr$particleManager);
		}
		return yttr$particleManager;
	}

	@Override
	public DummyClientWorld yttr$getParticleWorld() {
		yttr$getParticleManager();
		return yttr$particleWorld;
	}
	
}
