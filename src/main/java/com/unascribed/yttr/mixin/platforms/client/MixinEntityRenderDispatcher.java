package com.unascribed.yttr.mixin.platforms.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EntityRenderDispatcher.class)
@Environment(EnvType.CLIENT)
public class MixinEntityRenderDispatcher {

	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/render/entity/EntityRenderer.render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			shift=At.Shift.BEFORE), method="render")
	public void beforeRender(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo cin) {
		if (entity instanceof PlayerEntity pe && Yttr.isWearingPlatforms(pe)) {
			matrices.push();
			matrices.translate(0, 0.075f, 0);
		}
	}

	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/render/entity/EntityRenderer.render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			shift=At.Shift.AFTER), method="render")
	public void afterRender(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo cin) {
		if (entity instanceof PlayerEntity pe && Yttr.isWearingPlatforms(pe)) {
			matrices.pop();
		}
	}
	
}
