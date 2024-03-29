package com.unascribed.yttr.mixin.ultrapure.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.unascribed.yttr.client.YttrClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class MixinArmorFeatureRenderer<T extends LivingEntity> {
    @Unique
    private T entity;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    private void captureLivingEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        this.entity = livingEntity;
    }

    @ModifyReturnValue(method = "getArmorTexture", at = @At("RETURN"))
    private Identifier getArmorTexture(Identifier original, ArmorItem item, boolean legs, String overlay) {
        return YttrClient.getArmorTexture(entity, entity.getEquippedStack(item.getPreferredSlot()), item.getPreferredSlot(), legs, overlay, original);
    }
}
