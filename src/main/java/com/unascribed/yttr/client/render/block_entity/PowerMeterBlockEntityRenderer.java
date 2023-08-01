package com.unascribed.yttr.client.render.block_entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.device.PowerMeterBlock;
import com.unascribed.yttr.content.block.device.PowerMeterBlockEntity;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PowerMeterBlockEntityRenderer implements BlockEntityRenderer<PowerMeterBlockEntity> {

	private static final Identifier LCD = Yttr.id("textures/lcd.png");
	
	@Override
	public void render(PowerMeterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		long time = System.currentTimeMillis()-entity.readoutTime;
		if (time < 5200) {
			BlockState bs = entity.getCachedState();
			if (bs.getBlock() != YBlocks.POWER_METER) return;
			float a = 1;
			if (time < 200) {
				a = time/200f;
			} else if (time > 5000) {
				a = (5200-time)/500f;
			}
			String readout = Integer.toString(entity.readout);
			VertexConsumer vc = vertexConsumers.getBuffer(a < 1 ? RenderLayer.getEntityTranslucent(LCD) : RenderLayer.getEntityCutout(LCD));
			matrices.push();
			matrices.translate(0.5f, 0.5f, 0.5f);
			float ang = 0;
			switch (bs.get(PowerMeterBlock.FACING)) {
				case NORTH:
				default:
					ang = 0;
					break;
				case WEST:
					ang = 90;
					break;
				case SOUTH:
					ang = 180;
					break;
				case EAST:
					ang = 270;
					break;
			}
			matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(ang));
			matrices.translate(-0.5f, -0.5f, -0.5f);
			matrices.translate(1/16f, 0.5f, 0.89825f);
			matrices.translate(0, 0, -0.01f);
			for (int i = readout.length()-1; i >= 0; i--) {
				char c = readout.charAt(i);
				float u = Character.digit(c, 10)/10f;
				Matrix4f mat = matrices.peek().getModel();
				Matrix3f nrm = matrices.peek().getNormal();
				vc.vertex(mat, 0, 7/16f, 0).color(1f, 1f, 1f, a).uv(u+0.1f, 0).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 4/16f, 7/16f, 0).color(1f, 1f, 1f, a).uv(u, 0).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 4/16f, 0, 0).color(1f, 1f, 1f, a).uv(u, 1).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				vc.vertex(mat, 0, 0, 0).color(1f, 1f, 1f, a).uv(u+0.1f, 1).overlay(overlay).light(light).normal(nrm, 0, 0, 1).next();
				matrices.translate(5/16f, 0, 0);
			}
			matrices.pop();
		}
	}

}
