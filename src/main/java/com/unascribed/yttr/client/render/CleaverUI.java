package com.unascribed.yttr.client.render;

import static com.unascribed.yttr.client.RenderBridge.glDefaultBlendFunc;
import static com.unascribed.yttr.client.RenderBridge.glPopMCMatrix;
import static com.unascribed.yttr.client.RenderBridge.glPushMCMatrix;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonOffset;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.List;

import org.lwjgl.system.Platform;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.content.item.CleaverItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.util.math.partitioner.Plane;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CleaverUI extends IHasAClient {

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
					if (CleaverItem.canCleave(wrc.world(), pos, bs)) {
						if (!YConfig.Client.openglCompatibility.resolve(Platform.get() != Platform.MACOSX)) return true;
						glPushMCMatrix(wrc.matrixStack());
						glTranslated(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
						glDisable(GL_TEXTURE_2D);
						glDefaultBlendFunc();
						glEnable(GL_BLEND);
						glEnable(GL_POINT_SMOOTH);
						glEnable(GL_LINE_SMOOTH);
						float scale = (float)mc.getWindow().getScaleFactor();
						int sd = CleaverItem.SUBDIVISIONS;
						Vec3d cleaveStart = ci.getCleaveStart(held);
						Vec3d cleaveCorner = ci.getCleaveCorner(held);
						boolean anySelected = false;
						float selectedX = 0;
						float selectedY = 0;
						float selectedZ = 0;
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
										size = 8;
										g = 0;
										b = 0;
									} else if (selected) {
										size = 15;
										b = 0;
										anySelected = true;
										selectedX = wX;
										selectedY = wY;
										selectedZ = wZ;
									}
									glPointSize(size*scale);
									glColor4f(r, g, b, a);
									glBegin(GL_POINTS);
									glVertex3f(wX, wY, wZ);
									glEnd();
								}
							}
						}
						if (anySelected && cleaveStart != null && cleaveCorner != null) {
							final float TAU = (float)(Math.PI*2);
							float t = (wrc.world().getTime()+wrc.tickDelta())/5;
							float a = (MathHelper.sin(t%TAU)+1)/2;
							glColor4f(1, 0.25f, 0, 0.1f+(a*0.3f));
							glDisable(GL_CULL_FACE);
							glEnable(GL_DEPTH_TEST);
							glEnable(GL_POLYGON_OFFSET_FILL);
							glPolygonOffset(-3, -3);
							List<Polygon> shape = CleaverItem.getShape(wrc.world(), pos);
							Plane plane = new Plane(cleaveStart, cleaveCorner, new Vec3d(selectedX, selectedY, selectedZ));
							List<Polygon> cleave = CleaverItem.performCleave(plane, shape, true);
							for (Polygon polygon : cleave) {
								glBegin(GL_POLYGON);
								polygon.forEachDEdge((de) -> {
									glVertex3d(de.srcPoint().x, de.srcPoint().y, de.srcPoint().z);
								});
								glEnd();
							}
							glDisable(GL_POLYGON_OFFSET_FILL);
							glColor4f(1, 0.25f, 0, 0.05f+(a*0.1f));
							glDisable(GL_DEPTH_TEST);
							for (Polygon polygon : cleave) {
								glBegin(GL_POLYGON);
								polygon.forEachDEdge((de) -> {
									glVertex3d(de.srcPoint().x, de.srcPoint().y, de.srcPoint().z);
								});
								glEnd();
							}
							glEnable(GL_CULL_FACE);
						}
						glDisable(GL_BLEND);
						glPopMCMatrix();
						glEnable(GL_TEXTURE_2D);
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
				List<Polygon> polys = ((CleavedBlockEntity)be).getPolygons();
				// skip the "joiner" polygon to avoid an ugly line down the middle of the joined face
				// TODO why does the line happen? is the joiner polygon invalid?
				// I think it *is*, because enabling multi-cuts causes game crashes if the cut intersects the joiner polygon
				VertexConsumer vc = wrc.consumers().getBuffer(RenderLayer.getLines());
				for (Polygon pg : polys.subList(0, polys.size()-1)) {
					pg.forEachDEdge((de) -> {
						YttrClient.addLine(wrc.matrixStack(), vc,
								(float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z,
								(float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z,
								0, 0, 0, 0.4f,
								0, 0, 0, 0.4f);
					});
				}
				wrc.matrixStack().pop();
				return false;
			}
		}
		return true;
	}

}
