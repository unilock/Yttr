package com.unascribed.yttr.mixin.diffractor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.Stipple;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
@Mixin(value=HeldItemRenderer.class, priority=200000000)
public class MixinHeldItemRenderer {

	@Inject(at=@At("HEAD"), method="renderFirstPersonItem", cancellable=true)
	private void yttr$setupStipple(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vcp, int light, CallbackInfo ci) {
		if (vcp instanceof Immediate imm && player instanceof DiffractorPlayer dp && dp.yttr$getCloakTime() > 0) {
			imm.draw();
			Stipple.enable();
			Stipple.grey(100-((MathHelper.clamp(dp.yttr$getCloakTime(), 0, DiffractorPlayer.WARMUP_TIME)*80)/DiffractorPlayer.WARMUP_TIME));
		}
	}
	
	@Inject(at=@At("TAIL"), method="renderFirstPersonItem")
	private void yttr$cleanupStipple(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vcp, int light, CallbackInfo ci) {
		if (vcp instanceof Immediate imm && player instanceof DiffractorPlayer dp && dp.yttr$getCloakTime() > 0) {
			imm.draw();
			Stipple.disable();
		}
	}
	
}
