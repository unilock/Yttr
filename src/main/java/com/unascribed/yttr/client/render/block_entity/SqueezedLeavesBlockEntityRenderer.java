package com.unascribed.yttr.client.render.block_entity;

import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlock;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class SqueezedLeavesBlockEntityRenderer implements BlockEntityRenderer<SqueezedLeavesBlockEntity> {

	@Override
	public void render(SqueezedLeavesBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.getCachedState().get(SqueezedLeavesBlock.SQUEEZING)) {
			Immediate imm = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			
			// this is done in this weird way so the block entity doesn't have to be tickable
			long ticks = MinecraftClient.getInstance().world.getTime();
			if (entity.squeezeBegin == -1) {
				entity.squeezeBegin = ticks;
			}
			float time = ((ticks-entity.squeezeBegin)+tickDelta)%4;
			final float TAU = (float)(Math.PI*2);
			float a = 0.75f+((MathHelper.sin((time/4)*TAU)+1)/8);
			matrices.push();
			matrices.translate(0.5, 0.5, 0.5);
			matrices.scale(a, a, a);
			matrices.translate(-0.5, -0.5, -0.5);
			BlockState state = entity.getCachedState().with(SqueezedLeavesBlock.SQUEEZING, false);
			RenderLayer layer = RenderLayers.getBlockLayer(state);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state,
					entity.getPos(), entity.getWorld(), matrices,
					imm.getBuffer(layer), true, entity.getWorld().random);
			imm.draw(layer);
			matrices.pop();
		} else {
			entity.squeezeBegin = -1;
		}
	}

}
