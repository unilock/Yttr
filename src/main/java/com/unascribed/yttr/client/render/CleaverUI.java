package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.content.item.CleaverItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Plane;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class CleaverUI extends IHasAClient {

	private static final Identifier TEX = Yttr.id("textures/gui/cleaver_ui.png");
	
	public static boolean render(WorldRenderContext wrc, BlockOutlineContext boc) {
		ItemStack held = mc.player.getStackInHand(Hand.MAIN_HAND);
		if (held.getItem() instanceof CleaverItem) {
			CleaverItem ci = (CleaverItem)held.getItem();
			HitResult tgt = mc.crosshairTarget;
			if (tgt instanceof BlockHitResult && (!ci.requiresSneaking() || boc.entity().isSneaking())) {
				BlockPos cleaving = ci.getCleaveBlock(held);
				if (cleaving != null || tgt.getPos().squaredDistanceTo(boc.cameraX(), boc.cameraY(), boc.cameraZ()) <= 2*2) {
					BlockPos pos = cleaving == null ? boc.blockPos() : cleaving;
					BlockState bs = wrc.world().getBlockState(pos);
					if (CleaverItem.canCleave(wrc.world(), mc.player, held, pos, bs)) {
						var ms = wrc.matrixStack();
						ms.push();
						var dX = pos.getX()-boc.cameraX();
						var dY = pos.getY()-boc.cameraY();
						var dZ = pos.getZ()-boc.cameraZ();
						ms.translate(dX, dY, dZ);
						float scale = 3;
						int sd = CleaverItem.SUBDIVISIONS;
						Vec3d cleaveStart = ci.getCleaveStart(held);
						Vec3d cleaveCorner = ci.getCleaveCorner(held);
						boolean anySelected = false;
						float selectedX = 0;
						float selectedY = 0;
						float selectedZ = 0;
						var tess = Tessellator.getInstance();
						var vc = tess.getBufferBuilder();
						RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
						RenderSystem.setShaderTexture(0, TEX);
						RenderSystem.setShaderColor(1, 1, 1, 1);
						RenderSystem.disableCull();
						RenderSystem.disableDepthTest();
						for (int p = 0; p < 3; p++) {
							vc.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
							for (int x = 0; x <= sd; x++) {
								for (int y = 0; y <= sd; y++) {
									for (int z = 0; z <= sd; z++) {
										if ((x > 0 && x < sd) &&
												(y > 0 && y < sd) &&
												(z > 0 && z < sd)) {
											continue;
										}
										
										float wX = x/(float)sd;
										float wY = y/(float)sd;
										float wZ = z/(float)sd;
										boolean highlight = (cleaveStart != null && cleaveStart.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05) || (cleaveCorner != null && cleaveCorner.squaredDistanceTo(wX, wY, wZ) < 0.05*0.05);
										boolean selected = false;
										
										int wantedPass = 2;
										if (highlight) wantedPass = 1;
										if (selected) wantedPass = 0;

										if (p != wantedPass) continue;
										
										float a;
										if (!highlight) {
											double dist = tgt.getPos().squaredDistanceTo(pos.getX()+wX, pos.getY()+wY, pos.getZ()+wZ);
											final double maxDist = 0.75;
											if (dist > maxDist*maxDist) continue;
											selected = dist < 0.1*0.1;
											double distSq = Math.sqrt(dist);
											a = (float)((maxDist-distSq)/maxDist);
										} else {
											a = 1;
										}
										float r = 1;
										float g = 1;
										float b = 1;
										float size = a*10;
										if (highlight) {
											size = 10;
											g = 0;
											b = 0;
										} else if (selected) {
											size = 10;
											b = 0;
											anySelected = true;
											selectedX = wX;
											selectedY = wY;
											selectedZ = wZ;
										}
										int which = 0;
										if (selected) {
											which = 1;
										} else if (highlight) {
											which = 2;
										}
										float minU = which/3f;
										float minV = 0;
										float maxU = (which+1)/3f;
										float maxV = 1;
										size *= scale;
										size /= 300;
										ms.push();
											ms.translate(wX, wY, wZ);
											ms.scale(size, size, size);
											ms.multiply(wrc.camera().getRotation());
											var mat = ms.peek().getModel();
											vc.vertex(mat, -1, -1, 0).uv(minU, minV).color(r, g, b, a).next();
											vc.vertex(mat,  1, -1, 0).uv(maxU, minV).color(r, g, b, a).next();
											vc.vertex(mat,  1,  1, 0).uv(maxU, maxV).color(r, g, b, a).next();
											vc.vertex(mat, -1,  1, 0).uv(minU, maxV).color(r, g, b, a).next();
										ms.pop();
									}
								}
							}
							tess.draw();
						}
						RenderSystem.enableDepthTest();
						RenderSystem.enableCull();
						if (anySelected && cleaveStart != null && cleaveCorner != null) {
							final float TAU = (float)(Math.PI*2);
							float t = (wrc.world().getTime()+wrc.tickDelta())/5;
							float d = (MathHelper.sin(t%TAU)+1)/2;
							float r = 1;
							float g = 0.25f;
							float b = 0;
							float a = 0.1f+(d*0.3f);
							RenderSystem.enableBlend();
							RenderSystem.defaultBlendFunc();
							RenderSystem.enablePolygonOffset();
							RenderSystem.disableCull();
							RenderSystem.polygonOffset(-3, -3);
							var shape = CleaverItem.getShape(wrc.world(), pos);
							Plane plane = new Plane(cleaveStart, cleaveCorner, new Vec3d(selectedX, selectedY, selectedZ));
							var cleave = CleaverItem.performCleave(plane, shape, true);
							RenderSystem.setShader(GameRenderer::getPositionColorShader);
							vc.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
							var mat = ms.peek().getModel();
							for (Polygon p : cleave) {
								drawPolygon(mat, vc, p, r, g, b, a);
							}
							tess.draw();
							RenderSystem.disablePolygonOffset();
							a = 0.05f+(d*0.1f);
							RenderSystem.disableDepthTest();
							vc.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
							for (Polygon p : cleave) {
								drawPolygon(mat, vc, p, r, g, b, a);
							}
							tess.draw();
							RenderSystem.enableCull();
							RenderSystem.enableDepthTest();
						}
						ms.pop();
					}
				}
			}
		}
		if (boc.blockState().getBlock() == YBlocks.CLEAVED_BLOCK) {
			if (mc.options.debugEnabled) return true;
			BlockEntity be = wrc.world().getBlockEntity(boc.blockPos());
			if (be instanceof CleavedBlockEntity) {
				wrc.matrixStack().push();
				BlockPos pos = boc.blockPos();
				wrc.matrixStack().translate(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
				var polys = ((CleavedBlockEntity)be).getPolygons();
				VertexConsumer vc = wrc.consumers().getBuffer(RenderLayer.getLines());
				int i = 0;
				for (Polygon pg : polys) {
					// skip the "joiner" polygon to avoid an ugly line down the middle of the joined face
					// TODO why does the line happen? is the joiner polygon invalid?
					// I think it *is*, because enabling multi-cuts causes corrupted models if the cut intersects the joiner polygon
					if (i == polys.size()-2) break;
					pg.forEachDEdge((de) -> {
						YttrClient.addLine(wrc.matrixStack(), vc,
								(float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z,
								(float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z,
								0, 0, 0, 0.4f,
								0, 0, 0, 0.4f);
					});
					i++;
				}
				wrc.matrixStack().pop();
				return false;
			}
		}
		return true;
	}

	private static void drawPolygon(Matrix4f mat, BufferBuilder vc, Polygon p, float r, float g, float b, float a) {
		Vec3d origin = p.first().srcPoint();
		for (DEdge de : p) {
			// naive triangulation
			if (de == p.first()) continue;
			vc.vertex(mat, (float)origin.x, (float)origin.y, (float)origin.z).color(r, g, b, a).next();
			vc.vertex(mat, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z).color(r, g, b, a).next();
			vc.vertex(mat, (float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z).color(r, g, b, a).next();
		}
	}

}
