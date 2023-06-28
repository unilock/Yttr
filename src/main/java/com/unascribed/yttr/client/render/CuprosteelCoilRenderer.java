package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;

import dev.emi.trinkets.api.SlotReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CuprosteelCoilRenderer {
	
	private static final Identifier TEXTURE = Yttr.id("textures/entity/coil.png");

	public static void render(ItemStack is, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
			MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, LivingEntity entity,
			float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
			float headPitch) {
		if (entity.getEquippedStack(EquipmentSlot.FEET).getItem() == YItems.SUIT_BOOTS) return;
		VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
		boolean hasBoots = entity instanceof PlayerEntity ep && Yttr.earsAccess.isVisuallyWearingBoots(ep);
		if (model instanceof BipedEntityModel<?> bep) {
			renderCoil(vc, matrices, bep.leftLeg, hasBoots, light, true);
			renderCoil(vc, matrices, bep.rightLeg, hasBoots, light, false);
			if (YItems.CUPROSTEEL_COIL.is(is.getItem()) && is.hasGlint()) {
				vc = vertexConsumer.getBuffer(RenderLayer.getEntityGlint());
				renderCoil(vc, matrices, bep.leftLeg, hasBoots, light, true);
				renderCoil(vc, matrices, bep.rightLeg, hasBoots, light, false);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private static void renderCoil(VertexConsumer vc, MatrixStack matrices, ModelPart part, boolean hasBoots, int light, boolean flip) {
		matrices.push();
			part.rotate(matrices);
			matrices.translate(0, 12.1f/16f, 0);
			if (hasBoots) {
				matrices.translate(0.5f/16f*(flip?1:-1), 1/16f, 0);
			}
			matrices.scale(1.5f/16f, 1, 1.5f/16f);
			Matrix4f mmat = matrices.peek().getModel();
			Matrix3f nmat = matrices.peek().getNormal();
			vc.vertex(mmat, -1, 0,  1).color(1f, 1f, 1f, 1f).uv(flip ? 1 : 0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat,  1, 0,  1).color(1f, 1f, 1f, 1f).uv(flip ? 0 : 1, 1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat,  1, 0, -1).color(1f, 1f, 1f, 1f).uv(flip ? 0 : 1, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
			vc.vertex(mmat, -1, 0, -1).color(1f, 1f, 1f, 1f).uv(flip ? 1 : 0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(nmat, 0, 1, 0).next();
		matrices.pop();
	}
	
}
