package com.unascribed.yttr.mixin.diffractor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.Stipple;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(value=EntityRenderDispatcher.class, priority=200000000)
@Environment(EnvType.CLIENT)
public class MixinEntityRenderDispatcher {

	@Inject(at=@At("HEAD"), method="render", cancellable=true)
	public void yttr$setupStipple(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (vertexConsumers instanceof Immediate imm && entity instanceof DiffractorPlayer dp && dp.yttr$getCloakTime() > 0) {
			boolean isLocalPlayer = entity == MinecraftClient.getInstance().player;
			if (!isLocalPlayer && dp.yttr$isFullyCloaked()) {
				ci.cancel();
				return;
			}
			imm.draw();
			Stipple.enable();
			int max = 100;
			if (isLocalPlayer) {
				max = 90;
			}
			Stipple.grey(100-((MathHelper.clamp(dp.yttr$getCloakTime(), 0, DiffractorPlayer.WARMUP_TIME)*max)/DiffractorPlayer.WARMUP_TIME));
		}
	}

	@Inject(at=@At("TAIL"), method="render")
	public void yttr$cleanupStipple(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (vertexConsumers instanceof Immediate imm && entity instanceof DiffractorPlayer dp && dp.yttr$getCloakTime() > 0) {
			imm.draw();
			Stipple.disable();
		}
	}
	
}
