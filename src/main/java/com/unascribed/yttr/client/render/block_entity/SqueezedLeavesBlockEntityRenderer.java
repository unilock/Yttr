package com.unascribed.yttr.client.render.block_entity;

import static com.unascribed.yttr.client.RenderBridge.*;

import org.lwjgl.system.Platform;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlock;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
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
			boolean texhack = YConfig.Client.openglCompatibility.resolve(Platform.get() != Platform.MACOSX);
			Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/squeeze_leaves"));
			float w = sprite.getMaxU()-sprite.getMinU();
			float h = sprite.getMaxV()-sprite.getMinV();
			if (texhack) {
				// make sure there's no junk in the buffer before we do weird stuff
				imm.draw();
				glMatrixMode(GL_TEXTURE);
				glPushMatrix();
				glTranslatef(sprite.getMinU(), sprite.getMinV(), 0);
				glTranslatef(w/2, h/2, 0);
				glScalef(a, a, a);
				glTranslatef(-w/2, -h/2, 0);
				glTranslatef(-sprite.getMinU(), -sprite.getMinV(), 0);
				glMatrixMode(GL_MODELVIEW);
			}
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
			if (texhack) {
				glMatrixMode(GL_TEXTURE);
				glPopMatrix();
				glMatrixMode(GL_MODELVIEW);
			}
		} else {
			entity.squeezeBegin = -1;
		}
	}

}
