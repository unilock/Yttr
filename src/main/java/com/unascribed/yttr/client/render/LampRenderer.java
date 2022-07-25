package com.unascribed.yttr.client.render;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.mechanics.HaloBlockEntity;
import com.unascribed.yttr.util.MysticSet;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class LampRenderer extends IHasAClient {

	private static final Map<BlockPos, BlockEntity> lampsByBlock = new Object2ObjectOpenHashMap<>();
	private static final Map<BlockEntity, Object> lastState = new Object2ObjectOpenHashMap<>();
	private static final Multimap<ChunkSectionPos, BlockEntity> lampsBySection = Multimaps.newSetMultimap(new Object2ObjectOpenHashMap<>(), ReferenceOpenHashSet::new);
	private static final Map<ChunkSectionPos, VertexBuffer> buffers = new Object2ObjectOpenHashMap<>();
	private static final Map<ChunkSectionPos, Box> boundingBoxes = new Object2ObjectOpenHashMap<>();

	public static void clearCache() {
		buffers.values().forEach(VertexBuffer::close);
		buffers.clear();
		boundingBoxes.clear();
	}

	public static void render(World world, MatrixStack matrices, VertexConsumer vc, BlockState state, int color, @Nullable Direction facing, @Nullable BlockPos pos) {
		if (color == 0) color = 0x222222;
		float r = ((color >> 16)&0xFF)/255f;
		float g = ((color >> 8)&0xFF)/255f;
		float b = (color&0xFF)/255f;
		BakedModel base = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		BakedModel bm;
		try {
			YttrClient.retrievingHalo = true;
			bm = base.getOverrides().apply(base, ItemStack.EMPTY, MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, 39);
		} finally {
			YttrClient.retrievingHalo = false;
		}
		if (bm == null) return;
		DelegatingVertexConsumer dvc = new DelegatingVertexConsumer(vc) {
			@Override
			public void vertex(
					float x, float y, float z,
					float red, float green, float blue, float alpha,
					float u, float v,
					int overlay, int light,
					float normalX, float normalY, float normalZ
				) {
					vertex(x, y, z);
					texture(u, v);
					color(red, green, blue, alpha);
					normal(normalX, normalY, normalZ);
					next();
				}
		};

		matrices.push();

		if (facing != null) {
			int x = 0;
			int y = 0;
			switch (facing) {
				case DOWN: break;
				case WEST: x = 90; y = 90; break;
				case NORTH: x = 90; break;
				case SOUTH: x = 90; y = 180; break;
				case EAST: x = 90; y = 270; break;
				case UP: x = 180; break;
			}
			matrices.translate(0.5, 0.5, 0.5);
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(y));
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(x));
			matrices.translate(-0.5, -0.5, -0.5);
		}

		for (BakedQuad bq : bm.getQuads(state, null, world.random)) {
			dvc.quad(matrices.peek(), bq, r, g, b, 0, 0);
		}
		for (Direction dir : Direction.values()) {
			if (pos == null || Block.shouldDrawSide(state, MinecraftClient.getInstance().world, pos, dir, pos.offset(dir))) {
				for (BakedQuad bq : bm.getQuads(state, dir, world.random)) {
					dvc.quad(matrices.peek(), bq, r, g, b, 0, 0);
				}
			}
		}

		matrices.pop();
	}

	public static void render(WorldRenderContext wrc) {
		wrc.profiler().swap("yttr:lamps");
		if (!lampsBySection.isEmpty()) {
			wrc.profiler().push("prepare");
			MysticSet<ChunkSectionPos> needsRebuild = MysticSet.of();
			for (BlockEntity be : lampsBySection.values()) {
				if (!(be instanceof HaloBlockEntity)) continue;
				Object s = ((HaloBlockEntity)be).getStateObject();
				ChunkSectionPos csp = ChunkSectionPos.from(be.getPos());
				if (lastState.get(be) != s || !buffers.containsKey(csp)) {
					lastState.put(be, s);
					needsRebuild = needsRebuild.add(csp);
				}
			}
			wrc.profiler().swap("rebuild");
			MatrixStack scratch = new MatrixStack();
			for (ChunkSectionPos csp : needsRebuild.mundane()) {
				Collection<BlockEntity> l = lampsBySection.get(csp);
				if (l.isEmpty()) {
					if (buffers.containsKey(csp)) {
						buffers.remove(csp).close();
						boundingBoxes.remove(csp);
					}
					continue;
				}
				Box bounds = null;
				// POSITION_TEXTURE_COLOR_NORMAL is one of the few generic shaders with fog support
				BufferBuilder vc = new BufferBuilder(24 * VertexFormats.POSITION_TEXTURE_COLOR_NORMAL.getVertexSize() * l.size());
				vc.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
				for (BlockEntity be : l) {
					if (!(be instanceof HaloBlockEntity) || !((HaloBlockEntity)be).shouldRenderHalo()) continue;
					scratch.push();
						scratch.translate(be.getPos().getX()-csp.getMinX(), be.getPos().getY()-csp.getMinY(), be.getPos().getZ()-csp.getMinZ());
						int color = ((HaloBlockEntity)be).getGlowColor();
						BlockState state = be.getCachedState();
						Direction facing = ((HaloBlockEntity)be).getFacing();
						render(mc.world, scratch, vc, state, color, facing, be.getPos());
						Box myBox = new Box(be.getPos()).expand(0.5);
						if (bounds == null) {
							bounds = myBox;
						} else {
							bounds = bounds.union(myBox);
						}
					scratch.pop();
				}
				vc.end();
				VertexBuffer vb = buffers.computeIfAbsent(csp, blah -> new VertexBuffer());
				vb.upload(vc);
				buffers.put(csp, vb);
				boundingBoxes.put(csp, bounds);
			}
			wrc.profiler().swap("render");
			MatrixStack matrices = wrc.matrixStack();
			matrices.push();
			Vec3d cam = wrc.camera().getPos();
			matrices.translate(-cam.x, -cam.y, -cam.z);
			for (ChunkSectionPos pos : buffers.keySet()) {
				Box box = boundingBoxes.get(pos);
				if (box != null && wrc.frustum().isVisible(box)) {
					matrices.push();
						matrices.translate(pos.getMinX(), pos.getMinY(), pos.getMinZ());
						VertexBuffer buf = buffers.get(pos);
						buf.bind();
						YRenderLayers.getLampHalo().startDrawing();
						buf.setShader(matrices.peek().getModel(), RenderSystem.getProjectionMatrix(), GameRenderer.getPositionTexColorNormalShader());
						YRenderLayers.getLampHalo().endDrawing();
						VertexBuffer.unbind();
					matrices.pop();
					if (mc.getEntityRenderDispatcher().shouldRenderHitboxes() && !mc.hasReducedDebugInfo()) {
						VertexConsumerProvider.Immediate vcp = mc.getBufferBuilders().getEntityVertexConsumers();
						WorldRenderer.drawBox(matrices, vcp.getBuffer(RenderLayer.getLines()), box, 1, 1, 0, 1);
						vcp.draw(RenderLayer.getLines());
					}
				}
			}
			matrices.pop();
			wrc.profiler().pop();
		}
		wrc.profiler().swap("particles");
	}

	public static void tick() {
		if (mc.world != null) {
			Iterator<BlockEntity> iter = lampsBySection.values().iterator();
			while (iter.hasNext()) {
				BlockEntity be = iter.next();
				if (be.isRemoved() || be.getWorld() != mc.world) {
					ChunkSectionPos cs = ChunkSectionPos.from(be.getPos());
					if (buffers.containsKey(cs)) {
						buffers.remove(cs).close();
					}
					lampsByBlock.remove(be.getPos(), be);
					iter.remove();
				}
			}
			for (BlockEntity be : YttrClient.getBlockEntities()) {
				if (be instanceof HaloBlockEntity) {
					ChunkSectionPos cs = ChunkSectionPos.from(be.getPos());
					if (!lampsBySection.containsEntry(cs, be)) {
						if (lampsByBlock.containsKey(be.getPos())) {
							BlockEntity other = lampsByBlock.remove(be.getPos());
							lampsBySection.remove(ChunkSectionPos.from(be.getPos()), other);
						}
						lampsByBlock.put(be.getPos(), be);
						lampsBySection.put(cs, be);
					}
				}
			}
		} else {
			lampsByBlock.clear();
			lampsBySection.clear();
			lastState.clear();
			clearCache();
		}
	}

}
