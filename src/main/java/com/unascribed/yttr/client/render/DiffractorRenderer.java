package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.util.YRandom;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class DiffractorRenderer {
	
	public static void render(ItemStack is, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
			MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, LivingEntity entity,
			float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
			float headPitch) {
		if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE) return;
		if (model instanceof BipedEntityModel<?> bep && entity instanceof DiffractorPlayer dp) {
			BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr", "diffractor_model", "inventory"));
			matrices.push();
				bep.body.rotate(matrices);
				matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(180));
				matrices.translate(-7/16f, -13/16f, 2/16f);
				VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutoutNoCull(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
				for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), null, YRandom.get())) {
					vc.bakedQuad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
				}
				float ct;
				if (dp.yttr$isCloaked()) {
					ct = dp.yttr$getCloakTime()+tickDelta;
				} else {
					ct = (100-MathHelper.clamp(dp.yttr$getUncloakTime()+tickDelta, 0, 100))/3f;
				}
				float t = (entity.age+tickDelta)-(ct*ct*ct);
				matrices.push();
					matrices.translate(7/16f, 7/16f, 0);
					matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(t/2));
					matrices.translate(-7/16f, -7/16f, 0);
					for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), Direction.NORTH, YRandom.get())) {
						vc.bakedQuad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
					}
				matrices.pop();
				matrices.push();
					matrices.translate(7/16f, 7/16f, 0);
					matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(t/2));
					matrices.translate(-7/16f, -7/16f, 0);
					for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), Direction.SOUTH, YRandom.get())) {
						vc.bakedQuad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
					}
				matrices.pop();
			matrices.pop();
		}
	}
	
}
