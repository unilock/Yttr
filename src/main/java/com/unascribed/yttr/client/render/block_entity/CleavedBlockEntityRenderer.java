package com.unascribed.yttr.client.render.block_entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class CleavedBlockEntityRenderer implements BlockEntityRenderer<CleavedBlockEntity> {

	// THIS IS NOT REGISTERED ANYMORE
	
	@Override
	public void render(CleavedBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (!MinecraftClient.getInstance().options.debugEnabled) return;
		Random rand = new Random(entity.hashCode());
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		VertexConsumer lines = vertexConsumers.getBuffer(RenderLayer.getLines());
		for (Polygon polygon : entity.getPolygons()) {
			float cX = 0;
			float cY = 0;
			float cZ = 0;
			for (DEdge de : polygon) {
				cX += de.srcPoint().x;
				cY += de.srcPoint().y;
				cZ += de.srcPoint().z;
				
				YttrClient.addLine(matrices, lines,
						de.srcPoint().x, de.srcPoint().y, de.srcPoint().z,
						de.dstPoint().x, de.dstPoint().y, de.dstPoint().z,
						r, g, b, 1,
						r, g, b, 1);
			}
			cX /= polygon.nPoints();
			cY /= polygon.nPoints();
			cZ /= polygon.nPoints();
			Vec3d normal = polygon.plane().normal();
			float nX = (float)normal.x/2;
			float nY = (float)normal.y/2;
			float nZ = (float)normal.z/2;
			
			YttrClient.addLine(matrices, lines,
					cX, cY, cZ,
					cX+nX, cY+nY, cZ+nZ,
					r, g, b, 1,
					r, g, b, 1);
		}
	}

}
