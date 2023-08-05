package com.unascribed.yttr.client.render.block_entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.lib39.util.api.DelegatingVertexConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.render.LowSodiumItemRenderer;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.content.block.big.DSUBlock;
import com.unascribed.yttr.content.block.big.DSUBlockEntity;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.LightType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class DSUBlockEntityRenderer implements BlockEntityRenderer<DSUBlockEntity> {

	private static final Identifier TEX = Yttr.id("textures/block/dsu/front_inside_contents.png");
	
	// I tried to use ColorThief to imply these for resource pack support and such, but it makes bad decisions
	private static final ImmutableMap<Item, Integer> CUBE_COLORS = ImmutableMap.<Item, Integer>builder()
			.put(YItems.ULTRAPURE_CARBON, 0x393E46)
			.put(YItems.ULTRAPURE_CINNABAR, 0xD40901)
			.put(YItems.ULTRAPURE_GOLD, 0xF8D23F)
			.put(YItems.ULTRAPURE_IRON, 0xFFFFFF)
			.put(YItems.ULTRAPURE_LAZURITE, 0x345EC3)
			.put(YItems.ULTRAPURE_SILICA, 0xF7E7CF)
			.put(YItems.ULTRAPURE_YTTRIUM, 0x67A39D)
			.put(YItems.ULTRAPURE_NEODYMIUM, 0x6E7EA2)
			.put(YItems.ULTRAPURE_NETHERITE, 0x5A575A)
			.put(YItems.ULTRAPURE_WOLFRAM, 0x7E6059)
			.build();
	
	@Override
	public void render(DSUBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (entity.getController() != entity) return;
		BlockState bs = entity.getCachedState();
		Block block = bs.getBlock();
		if (!(block instanceof DSUBlock)) return;
		DSUBlock dsu = (DSUBlock)block;
		if (!bs.get(DSUBlock.OPEN).isTrue()) return;
		BlockPos.Mutable front = entity.getPos().mutableCopy();
		BlockState scan = entity.getWorld().getBlockState(front);
		Direction dir = bs.get(DSUBlock.FACING);
		while (scan.getBlock() == dsu) {
			front.move(dir);
			scan = entity.getWorld().getBlockState(front);
		}
		BlockPos.Mutable frontScan = front.mutableCopy();
		int maxBlockLight = 0;
		int maxSkyLight = 0;
		if (dir.getAxis() == Axis.X) {
			for (int y = 0; y < dsu.getYSize(); y++) {
				for (int z = 0; z < dsu.getZSize(); z++) {
					frontScan.move(0, y, z);
					maxBlockLight = Math.max(maxBlockLight, entity.getWorld().getLightLevel(LightType.BLOCK, frontScan));
					maxSkyLight = Math.max(maxSkyLight, entity.getWorld().getLightLevel(LightType.SKY, frontScan));
				}
			}
		} else if (dir.getAxis() == Axis.Z) {
			for (int y = 0; y < dsu.getYSize(); y++) {
				for (int x = 0; x < dsu.getXSize(); x++) {
					frontScan.move(x, y, 0);
					maxBlockLight = Math.max(maxBlockLight, entity.getWorld().getLightLevel(LightType.BLOCK, frontScan));
					maxSkyLight = Math.max(maxSkyLight, entity.getWorld().getLightLevel(LightType.SKY, frontScan));
				}
			}
		}
		light = LightmapTextureManager.pack(maxBlockLight, maxSkyLight);
		matrices.push();
			matrices.translate(dsu.getXSize()/2D, dsu.getYSize()/2D, dsu.getZSize()/2D);
			float ang = 0;
			switch (entity.getCachedState().get(DSUBlock.FACING)) {
				case NORTH:
					ang = 0;
					break;
				case EAST:
					ang = 270;
					break;
				case SOUTH:
					ang = 180;
					break;
				case WEST:
					ang = 90;
					break;
				default:
					ang = -30;
					break;
			}
			matrices.multiply(net.minecraft.util.math.Axis.Y_POSITIVE.rotationDegrees(ang));
			matrices.translate(-dsu.getXSize()/2D, -dsu.getYSize()/2D, -dsu.getZSize()/2D);
			matrices.translate(2, 2, 0);
			matrices.scale(-1/16f, -1/16f, 1/16f);
			Matrix4f mat = matrices.peek().getModel();
			Matrix3f nmat = matrices.peek().getNormal();
			matrices.translate(2.5, 2, -0.0025);
			for (int y = 0; y < 5; y++) {
				for (int x = 0; x < 9; x++) {
					ItemStack item = entity.getStack((y*9)+x);
					if (item.isEmpty()) continue;
					if (item.isIn(YTags.Item.ULTRAPURE_CUBES)) {
						int color;
						if (CUBE_COLORS.containsKey(item.getItem())) {
							color = CUBE_COLORS.get(item.getItem());
						} else {
							Identifier spriteId = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(item, null, null, 0).getParticleSprite().getId();
							Identifier id = new Identifier(spriteId.getNamespace(), "textures/"+spriteId.getPath()+".png");
							color = TextureColorThief.getPrimaryColor(id);
						}
						float a = (item.getCount()+64)/384f;
						if (a > 1) a = 1;
						float r = ((color >> 16)&0xFF)/255f;
						float g = ((color >>  8)&0xFF)/255f;
						float b = ((color >>  0)&0xFF)/255f;
						int minU = 2+(x*3);
						int minV = 2+(y*6);
						int maxU = 5+(x*3);
						int maxV = 5+(y*6);
						float w = 32;
						float h = 32;
						VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEX));
						vc.vertex(mat, (x*3)  , (y*6)  , 0).color(r, g, b, a).uv(minU/w, minV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)+3, (y*6)  , 0).color(r, g, b, a).uv(maxU/w, minV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)+3, (y*6)+3, 0).color(r, g, b, a).uv(maxU/w, maxV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
						vc.vertex(mat, (x*3)  , (y*6)+3, 0).color(r, g, b, a).uv(minU/w, maxV/h).overlay(overlay).light(light).normal(nmat, 0, 0, 1).next();
					} else {
						matrices.push();
							matrices.scale(3, -3, -3);
							Matrix3f nmat2 = matrices.peek().getNormal();
						matrices.pop();
						matrices.push();
							matrices.translate(x*3, y*6, 0);
							matrices.translate(1.5, 1.5, -0.015);
							matrices.scale(3, -3, -0.01f);
							LowSodiumItemRenderer.renderItem(item, ModelTransformationMode.GUI, light, overlay, matrices, layer -> new DelegatingVertexConsumer(vertexConsumers.getBuffer(layer)) {
								@Override
								public void bakedQuad(Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
									super.bakedQuad(new Entry(matrixEntry.getModel(), nmat2), quad, red, green, blue, light, overlay);
								}
							}, entity.getWorld(), 0);
						matrices.pop();
					}
				}
			}
		matrices.pop();
	}
	
}
