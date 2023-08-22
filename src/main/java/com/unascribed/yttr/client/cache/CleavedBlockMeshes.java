package com.unascribed.yttr.client.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.internal.UnsafeAllocator;
import com.mojang.blaze3d.texture.NativeImage;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.util.math.partitioner.DEdge;
import com.unascribed.yttr.util.math.partitioner.Plane;
import com.unascribed.yttr.util.math.partitioner.Polygon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.resource.metadata.FrameSize;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.random.Xoroshiro128PlusPlusRandom;

public class CleavedBlockMeshes {

	private static final MatrixStack IDENTITY = new MatrixStack();
	
	private static final ThreadLocal<UVObserver> uvo = ThreadLocal.withInitial(UVObserver::new);
	
	public record UniqueShapeKey(BlockState donor, ImmutableSet<Polygon> polys) {}
	
	private static final ConcurrentMap<UniqueShapeKey, Mesh> sharedMeshCache = new ConcurrentHashMap<>();
	
	public static volatile int era = 0;
	
	private static class DummySprite extends Sprite {

		private static final UnsafeAllocator UA = UnsafeAllocator.INSTANCE;
		
		private float minU, minV, maxU, maxV;
		
		protected DummySprite() {
			super(null, new SpriteContents(null, new FrameSize(0, 0), new NativeImage(0, 0, false), null), 0, 0, 0, 0);
			// NOT CALLED
		}
		
		public static DummySprite create(float minU, float minV, float maxU, float maxV) {
			try {
				DummySprite ds = UA.newInstance(DummySprite.class);
				ds.minU = minU;
				ds.minV = minV;
				ds.maxU = maxU;
				ds.maxV = maxV;
				return ds;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public float getMinU() {
			return minU;
		}
		
		@Override
		public float getMinV() {
			return minV;
		}
		
		@Override
		public float getMaxU() {
			return maxU;
		}
		
		@Override
		public float getMaxV() {
			return maxV;
		}
		
	}
	
	public static void clearCache() {
		era++;
		sharedMeshCache.clear();
	}
	
	public static Mesh getMesh(UniqueShapeKey usk) {
		Mesh cached = sharedMeshCache.get(usk);
		if (cached != null) return cached;
		if (!RendererAccess.INSTANCE.hasRenderer()) return null;
		MinecraftClient.getInstance().getProfiler().push("yttr:cleaved_modelgen");
		BlendMode bm = BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(usk.donor()));
		BakedModel donor = MinecraftClient.getInstance().getBlockRenderManager().getModel(usk.donor());
		Renderer r = RendererAccess.INSTANCE.getRenderer();
		RenderMaterial mat = r.materialFinder().blendMode(bm).find();
		MeshBuilder bldr = r.meshBuilder();
		QuadEmitter qe = bldr.getEmitter();
		RandomGenerator rand = new Xoroshiro128PlusPlusRandom(7);
		BakedQuad firstNullQuad = Iterables.getFirst(donor.getQuads(usk.donor(), null, rand), null);
		Sprite particle = donor.getParticleSprite();
		for (Polygon p : usk.polys()) {
			Plane plane = p.plane();
			Direction face = findClosestFace(plane.normal());
			BakedQuad firstQuad = Iterables.getFirst(donor.getQuads(usk.donor(), face, rand), firstNullQuad);
			Sprite sprite;
			int tintIndex = -1;
			if (firstQuad == null) {
				sprite = particle;
			} else {
				var uvo = CleavedBlockMeshes.uvo.get();
				uvo.reset();
				uvo.bakedQuad(IDENTITY.peek(), firstQuad, 0, 0, 0, 0, 0);
				sprite = DummySprite.create(uvo.getMinU(), uvo.getMinV(), uvo.getMaxU(), uvo.getMaxV());
				tintIndex = firstQuad.getColorIndex();;
			}
			if (p.nPoints() <= 2) {
				// ???
			} else if (p.nPoints() == 3) {
				// trivial case: triangle. make a degenerate quad
				buildTrivial(sprite, qe, p, false);
				qe.material(mat);
				qe.colorIndex(tintIndex);
				qe.emit();
			} else if (p.nPoints() == 4) {
				// ideal case: it's already a quad
				buildTrivial(sprite, qe, p, false);
				qe.material(mat);
				qe.colorIndex(tintIndex);
				qe.emit();
			} else {
				// worst case: need to triangulate
				// this isn't Optimal™, it's a trivial convex-only triangulation
				// but hey, it works, and doesn't make my head hurt
				Vec3d origin = p.first().srcPoint();
				int c = -1;//0x0000FF;
				for (DEdge de : p) {
					if (de == p.first()) continue;
					// each triangle is a degenerate quad
					// it'd be nice to find a solution that doesn't involve doing this, but whatever
					// is quadrangulation a thing??
					qe.nominalFace(face);
					qe.pos(0, (float)origin.x, (float)origin.y, (float)origin.z);
					qe.pos(1, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z);
					qe.pos(2, (float)de.dstPoint().x, (float)de.dstPoint().y, (float)de.dstPoint().z);
					qe.pos(3, (float)origin.x, (float)origin.y, (float)origin.z);
					for (int i = 0; i < 4; i++) {
						qe.normal(i, (float)plane.normal().x, (float)plane.normal().y, (float)plane.normal().z);
					}
					qe.spriteBake(sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
					qe.color(c, c, c, c);
					qe.material(mat);
					qe.colorIndex(tintIndex);
					qe.emit();
				}
			}
		}
		Mesh mesh = bldr.build();
		MinecraftClient.getInstance().getProfiler().pop();
		sharedMeshCache.putIfAbsent(usk, mesh);
		return mesh;
	}

	private static void buildTrivial(Sprite sprite, QuadEmitter qe, Polygon p, boolean invert) {
		Plane plane = p.plane();
		Direction face = findClosestFace(plane.normal());
		qe.nominalFace(face);
		if (invert) plane = new Plane(plane.normal().negate(), 0);
		int i = invert ? 3 : 0;
		for (DEdge de : p) {
			emit(sprite, qe, plane, de, i);
			i += (invert ? -1 : 1);
		}
		if (p.nPoints() == 3) emit(sprite, qe, plane, p.first(), i);
		qe.spriteBake(sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
		int c = -1;//p.nPoints() == 3 ? 0x00FFFF : 0x00FF00;
		qe.color(c, c, c, c);
	}

	private static void emit(Sprite sprite, QuadEmitter qe, Plane plane, DEdge de, int i) {
		qe.pos(i, (float)de.srcPoint().x, (float)de.srcPoint().y, (float)de.srcPoint().z);
		qe.normal(i, (float)plane.normal().x, (float)plane.normal().y, (float)plane.normal().z);
	}
	
	private static Direction findClosestFace(Vec3d normal) {
		return Direction.getFacing(normal.x, normal.y, normal.z);
	}
	
}
