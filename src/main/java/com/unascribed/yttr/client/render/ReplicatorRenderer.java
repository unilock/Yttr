package com.unascribed.yttr.client.render;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.ReplicatorShapes;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlockEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.math.Interp;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import org.joml.Quaternionf;

public class ReplicatorRenderer extends IHasAClient {

	public static final Set<ReplicatorBlockEntity> replicators = Sets.newLinkedHashSet();
	public static final Set<ReplicatorBlockEntity> removing = Sets.newLinkedHashSet();
	private static final List<ReplicatorBlockEntity> renderList = Lists.newArrayList();

	private static final Random rand = new Random();
	
	public static boolean renderOutline(WorldRenderContext wrc, BlockOutlineContext boc) {
		if (boc.blockState().isOf(YBlocks.REPLICATOR)) {
			BlockEntity be = wrc.world().getBlockEntity(boc.blockPos());
			if (be instanceof ReplicatorBlockEntity) {
				ReplicatorBlockEntity rbe = (ReplicatorBlockEntity)be;
				MatrixStack matrices = wrc.matrixStack();
				matrices.push();
				matrices.translate(boc.blockPos().getX()-boc.cameraX(), boc.blockPos().getY()-boc.cameraY(), boc.blockPos().getZ()-boc.cameraZ());
				matrices.translate(0.5, 0.5, 0.5);
				rand.setSeed(((ReplicatorBlockEntity) be).seed);
				
				if (rbe.clientAge+wrc.tickDelta() < 5) {
					float a = Interp.sCurve5((rbe.clientAge+wrc.tickDelta())/5f);
					matrices.scale(a, a, a);
				}
				
				float t = rand.nextInt(200)+rbe.clientAge+wrc.tickDelta();
				
				int shape = rand.nextInt(ReplicatorShapes.ALL.size());
				rand.nextInt(ReplicatorShapes.ALL.size());
				
				matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				matrices.multiply(Axis.X_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				matrices.scale(1.4f-(rand.nextFloat()*0.65f), 1.4f-(rand.nextFloat()*0.65f), 1.4f-(rand.nextFloat()*0.65f));
		
				VertexConsumer vc = wrc.consumers().getBuffer(RenderLayer.getLines());
				switch (shape) {
					case 0:
						matrices.scale(0.5f, 0.5f, 0.5f);
						ReplicatorShapes.octahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
					case 1:
						matrices.scale(0.509165f, 0.509165f, 0.509165f);
						ReplicatorShapes.dodecahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
					case 2:
						matrices.scale(0.5f, 0.5f, 0.5f);
						ReplicatorShapes.icosahedronOutline(matrices, vc, 0, 0, 0, 0.4f);
						break;
				}
				matrices.pop();
				return false;
			}
		}
		return true;
	}
	
	public static void render(MatrixStack matrices, float tickDelta, int seed, ItemStack item, BlockPos pos, int ticks, Camera cam, int pass, float detail) {
		if (pass < 2) {
			matrices.push();
			matrices.translate(0.5, 0.5, 0.5);
			if (ticks+tickDelta < 5) {
				float a = Interp.sCurve5((ticks+tickDelta)/5f);
				matrices.scale(a, a, a);
			}
			
			rand.setSeed(seed);
			
			float t = rand.nextInt(200)+ticks+tickDelta;
			
			if (pass == 0 || pass == -1) {
				matrices.push();
				ItemRenderer ir = mc.getItemRenderer();
				BakedModel model = ir.getModels().getModel(item);
				if (pass == -1) {
					// bigger items in GUI so you can tell what they are
					// the clipping evident when an item exceeds the bounds of the inner shape is less obvious in a gui
					// also make them face the right direction
					BakedModel ours = ir.getModels().getModel(YItems.REPLICATOR);
					Transformation undo = ours.getTransformation().gui;
					matrices.scale(1/undo.scale.x(), 1/undo.scale.y(), 1/undo.scale.z());
					Quaternionf q = new Quaternionf().rotationXYZ(undo.rotation.x()*MathHelper.RADIANS_PER_DEGREE, undo.rotation.y()*MathHelper.RADIANS_PER_DEGREE, undo.rotation.z()*MathHelper.RADIANS_PER_DEGREE);
					q.conjugate();
					matrices.multiply(q);
					matrices.translate(-undo.translation.x(), -undo.translation.y(), -undo.translation.z());
					model.getTransformation().gui.apply(false, matrices);
					matrices.scale(0.65f, 0.65f, 0.65f);
				} else {
					matrices.scale(0.35f, 0.35f, 0.35f);
				}
				if (cam != null) {
					matrices.multiply(cam.getRotation());
				}
				matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(MathHelper.sin((t/6)%((float)Math.PI*2))*10));

				VertexConsumerProvider.Immediate imm = mc.getBufferBuilders().getEntityVertexConsumers();
				ir.renderItem(item, ModelTransformationMode.NONE, false, matrices, imm, LightmapTextureManager.pack(15, 15), OverlayTexture.DEFAULT_UV, model);
				imm.draw();
				matrices.pop();
			}
			
			if (pass == 1 && YttrClient.positionNormalShader != null) {
				var solid1 = ReplicatorShapes.ALL.get(rand.nextInt(ReplicatorShapes.ALL.size()));
				var solid2 = ReplicatorShapes.ALL.get(rand.nextInt(ReplicatorShapes.ALL.size()));

				RenderSystem.depthMask(false);
				RenderSystem.enableDepthTest();
				RenderSystem.depthFunc(GL_LESS);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.disableCull();
				matrices.push();
				
				matrices.multiply(Axis.Y_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				matrices.multiply(Axis.X_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(t*(rand.nextFloat()*2)));
				
				float xSo = (rand.nextFloat()*0.65f);
				float ySo = (rand.nextFloat()*0.65f);
				float zSo = (rand.nextFloat()*0.65f);
				if (cam == null) {
					xSo = ySo = zSo = 0;
				}
				matrices.scale(1.4f-xSo, 1.4f-ySo, 1.4f-zSo);
				matrices.scale(0.5f, 0.5f, 0.5f);
		
				RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				float r = rand.nextFloat()/2;
				float g = rand.nextFloat()/2;
				float b = (0.25f+(rand.nextFloat()*0.75f))/2;
				var mv = RenderSystem.getModelViewStack();
				mv.push();
				mv.multiplyMatrix(matrices.peek().getModel());
				mv.push();
				for (int i = 0; i < Math.ceil((MinecraftClient.isFancyGraphicsOrBetter() ? 6 : 1)*detail); i++) {
					RenderSystem.setShaderColor(r, g, b, i == 0 ? 0.2f : (0.025f/detail));
					solid1.bind();
					solid1.draw(mv.peek().getModel(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionShader());
					float s = 1+(0.05f/detail);
					mv.scale(s, s, s);
				}
				mv.pop();
				
				mv.scale(0.9f-(rand.nextFloat()*0.25f), 0.9f-(rand.nextFloat()*0.25f), 0.9f-(rand.nextFloat()*0.25f));

				RenderSystem.defaultBlendFunc();
				RenderSystem.setShaderColor(rand.nextFloat(), 0.25f+(rand.nextFloat()*0.75f), rand.nextFloat(), 0.125f);
				solid2.bind();
				solid2.draw(mv.peek().getModel(), RenderSystem.getProjectionMatrix(), YttrClient.positionNormalShader);
				mv.pop();
				
				VertexBuffer.unbind();
				
				RenderSystem.disableBlend();
				RenderSystem.setShaderColor(1, 1, 1, 1);
				RenderSystem.depthFunc(GL_LEQUAL);
				RenderSystem.depthMask(true);
				RenderSystem.enableCull();
				matrices.pop();
			}
			matrices.pop();
		}

		// completely broken on 1.20
//		if (pass == 2 && !item.isEmpty() && !mc.options.hudHidden && mc.crosshairTarget.getType() == Type.BLOCK) {
//			BlockHitResult bhr = (BlockHitResult)mc.crosshairTarget;
//			if (bhr.getBlockPos().equals(pos)) {
//				RenderSystem.enableBlend();
//				RenderSystem.defaultBlendFunc();
//				matrices.push();
//				matrices.translate(0.5, 0.5, 0.5);
//				if (cam != null) matrices.multiply(cam.getRotation());
//				matrices.scale(-0.0125F, -0.0125F, 0.5f);
//				matrices.translate(0, -50, -400);
//
//				List<OrderedText> tip = Lists.transform(Screen.getTooltipFromItem(mc, item), Text::asOrderedText);
//				int width = 0;
//				int height = tip.size()*8;
//				for (OrderedText ot : tip) {
//					width = Math.max(width, mc.textRenderer.getWidth(ot));
//				}
//				matrices.translate(-(width+16)/2f, -height, 0);
//				GuiGraphics graphics = setupGraphicsState(matrices);
//				graphics.drawOrderedTooltip(mc.textRenderer, tip, 0, 0);
//
//				matrices.pop();
//				RenderSystem.disableBlend();
//			}
//		}
	}

	private static GuiGraphics setupGraphicsState(MatrixStack stack) {
		return new GuiGraphics(mc, stack, mc.getBufferBuilders().getEntityVertexConsumers());
	}
	
	public static void render(WorldRenderContext wrc) {
		wrc.profiler().swap("yttr:replicators");
		if (!replicators.isEmpty() || !removing.isEmpty()) {
			renderList.clear();
			renderList.addAll(removing);
			wrc.profiler().push("prepare");
			for (ReplicatorBlockEntity rbe : replicators) {
				if (rbe.clientAge < 1) continue;
				double dist = rbe.getPos().getSquaredDistanceToCenter(wrc.camera().getPos());
				if (dist < 64*64 && wrc.frustum().isVisible(new Box(rbe.getPos()))) {
					rbe.distTmp = dist;
					renderList.add(rbe);
				}
			}
			wrc.profiler().swap("sort");
			Collections.sort(renderList, (a, b) -> Double.compare(b.distTmp, a.distTmp));
			wrc.profiler().swap("render");
			MatrixStack matrices = wrc.matrixStack();
			matrices.push();
			Vec3d cam = wrc.camera().getPos();
			matrices.translate(-cam.x, -cam.y, -cam.z);
			for (int pass = 0; pass < 2; pass++) {
				wrc.profiler().push("pass"+pass);
				for (ReplicatorBlockEntity rbe : renderList) {
					matrices.push();
					matrices.translate(rbe.getPos().getX(), rbe.getPos().getY(), rbe.getPos().getZ());
					if (rbe.isRemoved()) {
						float a = Interp.sCurve5(Math.max(0, 1-((rbe.removedTicks+wrc.tickDelta())/10f)));
						matrices.translate(0.5, 0.5, 0.5);
						matrices.scale(a, a, a);
						matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(a*720));
						matrices.translate(-0.5, -0.5, -0.5);
					}
					float detail = 1;
					if (rbe.distTmp < 2*2) {
						detail = 2;
					} else if (rbe.distTmp > 12*12) {
						detail = 0.5f;
					} else if (rbe.distTmp > 32*32) {
						detail = 0.1f;
					}
					render(matrices, wrc.tickDelta(), rbe.seed, rbe.item, rbe.getPos(), rbe.clientAge, wrc.camera(), pass, detail);
					matrices.pop();
				}
				wrc.profiler().pop();
			}
			matrices.pop();
			wrc.profiler().pop();
		}
		RenderSystem.setShaderColor(1, 1, 1, 1);
		wrc.profiler().swap("particles");
	}
	
	public static void notifyCreated(ReplicatorBlockEntity rbe) {
		replicators.add(rbe);
	}
	
	public static void tick() {
		Profiler p = mc.getProfiler();
		p.push("main");
		Set<ReplicatorBlockEntity> valid = Sets.newHashSet();
		if (mc.world != null) {
			for (ReplicatorBlockEntity rbe : replicators) {
				if (!rbe.isRemoved() && rbe.getWorld() == mc.world) {
					valid.add(rbe);
				}
			}
		} else {
			replicators.clear();
			removing.clear();
		}
		replicators.clear();
		replicators.addAll(valid);
		p.swap("removed");
		Iterator<ReplicatorBlockEntity> iter = removing.iterator();
		while (iter.hasNext()) {
			ReplicatorBlockEntity rbe = iter.next();
			rbe.clientTick();
			if (!rbe.isRemoved() || rbe.removedTicks > 10) {
				rbe.removedTicks = 0;
				iter.remove();
			}
		}
		p.pop();
	}

}
