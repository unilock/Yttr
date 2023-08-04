package com.unascribed.yttr.client.render;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.util.math.Interp;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class EffectorRenderer extends IHasAClient {

	public static final List<EffectorHole> effectorHoles = Lists.newArrayList();
	
	public static void render(WorldRenderContext wrc) {
		if (effectorHoles.isEmpty()) return;
		ClientWorld w = wrc.world();
		w.unmaskPhasedBlocks();
		try {
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(GL_LEQUAL);
			Vec3d cam = wrc.camera().getPos();
			MatrixStack ms = wrc.matrixStack();
			ms.translate(-cam.x, -cam.y, -cam.z);
			BlockPos.Mutable mut = new BlockPos.Mutable();
			List<Axis> axes = Arrays.asList(Direction.Axis.values());
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBufferBuilder();
			for (EffectorHole hole : effectorHoles) {
				Axis axisZ = hole.dir.getAxis();
				Axis axisX = Iterables.find(axes, a -> a != axisZ);
				Axis axisY = Iterables.find(Lists.reverse(axes), a -> a != axisZ);
				float t = hole.age+wrc.tickDelta();
				float a;
				if (t <= 4) {
					a = 1-Interp.sCurve5(1-(t/4));
				} else if (t >= 130) {
					a = Interp.sCurve5((150-t)/20);
				} else {
					a = 1;
				}
				if (a < 0.05) a = 0;
				if (a > 0.95) a = 1;
				if (a != 1) {
					drawVoidCap(w, ms, mut, hole.length, axisX, axisY, a, hole.start, hole.dir);
					drawVoidCap(w, ms, mut, 0, axisX, axisY, a, hole.start.offset(hole.dir, hole.length-1), hole.dir.getOpposite());
				}
				RenderSystem.setShader(GameRenderer::getRenderTypeCutoutShader);
				RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
				bb.begin(DrawMode.QUADS, RenderLayer.getCutout().getVertexFormat());
				ms.push();
				var trans = hole.dir.getUnitVector();
				trans.mul(hole.length/2f);
				ms.translate(hole.start.getX(), hole.start.getY(), hole.start.getZ());
				ms.translate(trans.x(), trans.y(), trans.z());
				float s = (hole.length-0.01f)/hole.length;
				switch (axisZ) {
					case X -> ms.scale(s, 1, 1);
					case Y -> ms.scale(1, s, 1);
					case Z -> ms.scale(1, 1, s);
				}
				ms.translate(-trans.x(), -trans.y(), -trans.z());
				ms.translate(-hole.start.getX(), -hole.start.getY(), -hole.start.getZ());
				for (int z = 0; z < hole.length; z++) {
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -2);
					EffectorItem.move(mut, axisX, -1);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisY, AxisDirection.POSITIVE));
						EffectorItem.move(mut, axisX, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, 2);
					EffectorItem.move(mut, axisX, -1);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisY, AxisDirection.NEGATIVE));
						EffectorItem.move(mut, axisX, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -1);
					EffectorItem.move(mut, axisX, -2);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisX, AxisDirection.POSITIVE));
						EffectorItem.move(mut, axisY, 1);
					}
					mut.set(hole.start).move(hole.dir, z);
					EffectorItem.move(mut, axisY, -1);
					EffectorItem.move(mut, axisX, 2);
					for (int i = 0; i < 3; i++) {
						drawVoidFace(w, ms, bb, mut, Direction.from(axisX, AxisDirection.NEGATIVE));
						EffectorItem.move(mut, axisY, 1);
					}
				}
				ms.pop();
				RenderSystem.depthMask(false);
				RenderSystem.enablePolygonOffset();
				RenderSystem.polygonOffset(-1, 1);
				tess.draw();
				RenderSystem.depthMask(true);
				RenderSystem.depthFunc(GL11.GL_LESS);
				RenderSystem.disablePolygonOffset();
			}
		} finally {
			w.remaskPhasedBlocks();
		}
	}
	
	private static void drawVoidCap(ClientWorld w, MatrixStack ms, BlockPos.Mutable mut, int l, Axis axisX, Axis axisY, float a, BlockPos pos, Direction dir) {
		RenderSystem.setShader(GameRenderer::getPositionShader);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBufferBuilder();
		ms.push();
		ms.translate(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
		ms.translate(dir.getOffsetX()*-0.5, dir.getOffsetY()*-0.5, dir.getOffsetZ()*-0.5);
		ms.multiply(dir.getRotationQuaternion());
		Matrix4f mat = ms.peek().getModel();
		if (a != 0) {
			float s = a*1.5f;
			if (l > 0) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.setShaderColor(0, 0, 0, a > 0.75f ? (1-a)*4 : 1);
				RenderSystem.depthMask(false);
				bb.begin(DrawMode.QUADS, VertexFormats.POSITION);
				bb.vertex(mat,  s, 0,  s).next();
				bb.vertex(mat,  s, l,  s).next();
				bb.vertex(mat,  s, l, -s).next();
				bb.vertex(mat,  s, 0, -s).next();
	
				bb.vertex(mat, -s, 0, -s).next();
				bb.vertex(mat, -s, l, -s).next();
				bb.vertex(mat, -s, l,  s).next();
				bb.vertex(mat, -s, 0,  s).next();
				
				bb.vertex(mat, -s, 0,  s).next();
				bb.vertex(mat, -s, l,  s).next();
				bb.vertex(mat,  s, l,  s).next();
				bb.vertex(mat,  s, 0,  s).next();
	
				bb.vertex(mat,  s, 0, -s).next();
				bb.vertex(mat,  s, l, -s).next();
				bb.vertex(mat, -s, l, -s).next();
				bb.vertex(mat, -s, 0, -s).next();
				tess.draw();
				RenderSystem.disableBlend();
				RenderSystem.depthMask(true);
			}
			RenderSystem.colorMask(false, false, false, false);
			RenderSystem.enablePolygonOffset();
			RenderSystem.polygonOffset(-3, -3);
			RenderSystem.disableCull();
			bb.begin(DrawMode.QUADS, VertexFormats.POSITION);
			bb.vertex(mat, -s, 0, -s).next();
			bb.vertex(mat,  s, 0, -s).next();
			bb.vertex(mat,  s, 0,  s).next();
			bb.vertex(mat, -s, 0,  s).next();
			tess.draw();
			RenderSystem.disablePolygonOffset();
			RenderSystem.colorMask(true, true, true, true);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.enableCull();
		}
		ms.pop();
		RenderSystem.setShader(GameRenderer::getRenderTypeCutoutShader);
		RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		bb.begin(DrawMode.QUADS, RenderLayer.getCutout().getVertexFormat());
		mc.gameRenderer.getLightmapTextureManager().enable();
		RandomGenerator r = RandomGenerator.createLegacy();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				mut.set(pos);
				EffectorItem.move(mut, axisX, x);
				EffectorItem.move(mut, axisY, y);
				mut.move(dir, -1);
				int sky = w.getLightLevel(LightType.SKY, mut);
				int block = w.getLightLevel(LightType.BLOCK, mut);
				int light = LightmapTextureManager.pack(block, sky);
				mut.move(dir, 1);
				BlockState state = mc.world.getBlockState(mut);
				BakedModel model = mc.getBlockRenderManager().getModel(state);
				if (model == null) continue;
				r.setSeed(state.getRenderingSeed(mut));
				Iterable<BakedQuad> quads = model.getQuads(state, dir.getOpposite(), r);
				if (quads == null) continue;
				ms.push();
				ms.translate(mut.getX(), mut.getY(), mut.getZ());
				// can't get the lighting to work. just fake it
				float b = switch (dir.getOpposite()) {
					case UP -> w.getSkyProperties().isDarkened() ? 0.9f : 1f;
					case DOWN -> w.getSkyProperties().isDarkened() ? 0.9f : 0.5f;
					case NORTH -> 0.8f;
					case EAST -> 0.6f;
					case SOUTH -> 0.8f;
					case WEST -> 0.6f;
				};
				for (BakedQuad q : quads) {
					int color = q.hasColor() ? mc.getBlockColors().getColor(state, w, pos, q.getColorIndex()) : -1;
					bb.bakedQuad(ms.peek(), q, (((color >> 16)&0xFF)/255f)*b, (((color >> 8)&0xFF)/255f)*b, ((color&0xFF)/255f)*b, light, OverlayTexture.DEFAULT_UV);
				}
				ms.pop();
			}
		}
		tess.draw();
		mc.gameRenderer.getLightmapTextureManager().disable();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.disableCull();
		bb.begin(DrawMode.QUADS, VertexFormats.POSITION);
		bb.vertex(mat, -1.5f, 0, -1.5f).next();
		bb.vertex(mat,  1.5f, 0, -1.5f).next();
		bb.vertex(mat,  1.5f, 0,  1.5f).next();
		bb.vertex(mat, -1.5f, 0,  1.5f).next();
		tess.draw();
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableCull();
	}
	
	private static void drawVoidFace(World w, MatrixStack ms, VertexConsumer vc, BlockPos pos, Direction face) {
		if (w.isPhased(pos)) return;
		if (w.isAir(pos) || w.isAir(pos.offset(face))) return;
		var bs = mc.world.getBlockState(pos);
		BakedModel model = mc.getBlockRenderManager().getModel(bs);
		if (model == null) return;
		Iterable<BakedQuad> quads = model.getQuads(bs, face, mc.world.random);
		if (quads == null) return;
		ms.push();
		ms.translate(pos.getX(), pos.getY(), pos.getZ());
		for (BakedQuad q : quads) {
			vc.bakedQuad(ms.peek(), q, 0, 0, 0, LightmapTextureManager.pack(0, 0), OverlayTexture.DEFAULT_UV);
		}
		ms.pop();
	}

	public static void tick() {
		Iterator<EffectorHole> iter = effectorHoles.iterator();
		while (iter.hasNext()) {
			if (iter.next().age++ > 150) {
				iter.remove();
			}
		}
	}

	private static class EffectorHole {

		public final BlockPos start;
		public final Direction dir;
		public final int length;
		
		public int age;
		
		public EffectorHole(BlockPos start, Direction dir, int length) {
			this.start = start;
			this.dir = dir;
			this.length = length;
		}
		
	}

	public static void addHole(BlockPos pos, Direction dir, int length) {
		effectorHoles.add(new EffectorHole(pos, dir, length));
	}
	
}
