package com.unascribed.yttr.client.render;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.AmmoCanItem;
import com.unascribed.yttr.content.item.AmmoPackItem;
import com.unascribed.yttr.init.YItems;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AmmoPackRenderer {
	
	public static void render(ItemStack is, SlotReference slotReference, EntityModel<? extends LivingEntity> model,
			MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, LivingEntity entity,
			float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
			float headPitch) {
		if (entity.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE) return;
		if (model instanceof BipedEntityModel<?> bep) {
			BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr:ammo_pack_model#inventory"));
			matrices.push();
				bep.body.rotate(matrices);
				matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
				matrices.translate(-8/16f, -12/16f, 2/16f);
				VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutoutNoCull(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
				for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
					vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
				}
				for (Direction d : Direction.values()) {
					int i = d.ordinal();
					ItemStack slot = ((AmmoPackItem)is.getItem()).getStack(is, i);
					if (!slot.isEmpty()) {
						int color = slot.getItem() instanceof AmmoCanItem ? ((AmmoCanItem)slot.getItem()).getColor(slot, 1) : 0xFF284946;
						float r = ((color>>16)&0xFF)/255f;
						float g = ((color>> 8)&0xFF)/255f;
						float b = ((color>> 0)&0xFF)/255f;
						for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), d, ThreadLocalRandom.current())) {
							vc.quad(matrices.peek(), bq, bq.hasColor() ? r : 1, bq.hasColor() ? g : 1, bq.hasColor() ? b : 1, light, OverlayTexture.DEFAULT_UV);
						}
					}
				}
				float chest = 0;
				if (entity instanceof PlayerEntity player) {
					try {
						chest = Yttr.earsAccess.getChestSize(player);
					} catch (Throwable t) {}
				}
				BakedModel seg = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr:ammo_pack_seg_model#inventory"));
				matrices.translate(3.5f/16f, 11f/16f, -4.5f/16f);
				if (chest > 0) {
					float ang = chest*45;
	
					matrices.push();
						matrices.push();
							for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
								vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
							}
						matrices.pop();
	
						matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(ang));
						matrices.translate(0, -5.5f/16f, 0);
						matrices.push();
							matrices.scale(1, 5.5f, 1);
							for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
								vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
							}
						matrices.pop();
	
						float scale = 5.5f;
						float angr = (float)Math.toRadians(ang);
						float len = (float)(scale * Math.tan(angr));
						float h = scale/MathHelper.cos(angr);
						matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
						matrices.translate(0, -len/16f, 0);
						matrices.push();
							matrices.scale(1, len, 1);
							for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
								vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
							}
						matrices.pop();
					matrices.pop();
	
					matrices.translate(0, -7.5f/16f, 0);
					matrices.push();
						matrices.scale(1, (12-h)-4.5f, 1);
						for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
							vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
						}
					matrices.pop();
				} else {
					for (int y = 0; y < 10; y++) {
						for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, ThreadLocalRandom.current())) {
							vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
						}
						matrices.translate(0, -1/16f, 0);
					}
				}
			matrices.pop();
		}
	}
	
}