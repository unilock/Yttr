package com.unascribed.yttr.client;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.decor.BloqueBlockEntity.Adjacency;
import com.unascribed.yttr.content.block.decor.BloqueBlockEntity.RenderData;
import com.unascribed.yttr.util.math.Vec2i;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockRenderView;

import static com.unascribed.yttr.content.block.decor.BloqueBlock.*;

public class BloqueModel implements UnbakedModel, BakedModel, FabricBakedModel {

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		Object attachment = ((RenderAttachedBlockView)blockView).getBlockEntityRenderAttachment(pos);
		if (attachment instanceof RenderData data) {
			DyeColor[] colors = data.colors();
			Sprite top = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_top"));
			Sprite side = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_side"));
			Sprite bottom = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_bottom"));
			Sprite welded = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_welded"));
			Sprite weldedSide = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_welded_side"));
			Sprite weldedTop = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_welded_top"));
			QuadEmitter qe = context.getEmitter();
			RenderMaterial mat = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.SOLID).find();
			for (int y = 0; y < YSIZE; y++) {
				for (int x = 0; x < XSIZE; x++) {
					for (int z = 0; z < ZSIZE; z++) {
						int slot = getSlot(x, y, z);
						DyeColor color = colors[slot];
						if (color != null) {
							Adjacency a = data.adjacency()[slot];
							Box box = VOXEL_SHAPES[slot].getBoundingBox();
							for (Direction d : Direction.values()) {
								if (a != null && a.skipFace(d)) {
									continue;
								}
								Sprite sprite = side;
								float minX = (float)box.minX;
								float minY = (float)box.minY;
								float minZ = (float)box.minZ;
								float maxX = (float)box.maxX;
								float maxY = (float)box.maxY;
								float maxZ = (float)box.maxZ;
								Vec3f[] vertices = new Vec3f[4];
								switch (d) {
									case UP:
										sprite = top;
										vertices[3] = new Vec3f(minX, maxY, minZ);
										vertices[2] = new Vec3f(maxX, maxY, minZ);
										vertices[1] = new Vec3f(maxX, maxY, maxZ);
										vertices[0] = new Vec3f(minX, maxY, maxZ);
										break;
									case DOWN:
										sprite = bottom;
										vertices[0] = new Vec3f(minX, minY, minZ);
										vertices[1] = new Vec3f(maxX, minY, minZ);
										vertices[2] = new Vec3f(maxX, minY, maxZ);
										vertices[3] = new Vec3f(minX, minY, maxZ);
										break;
									case NORTH:
										vertices[3] = new Vec3f(minX, minY, minZ);
										vertices[2] = new Vec3f(maxX, minY, minZ);
										vertices[1] = new Vec3f(maxX, maxY, minZ);
										vertices[0] = new Vec3f(minX, maxY, minZ);
										break;
									case SOUTH:
										vertices[0] = new Vec3f(minX, minY, maxZ);
										vertices[1] = new Vec3f(maxX, minY, maxZ);
										vertices[2] = new Vec3f(maxX, maxY, maxZ);
										vertices[3] = new Vec3f(minX, maxY, maxZ);
										break;
									case WEST:
										vertices[3] = new Vec3f(minX, minY, maxZ);
										vertices[2] = new Vec3f(minX, minY, minZ);
										vertices[1] = new Vec3f(minX, maxY, minZ);
										vertices[0] = new Vec3f(minX, maxY, maxZ);
										break;
									case EAST:
										vertices[0] = new Vec3f(maxX, minY, maxZ);
										vertices[1] = new Vec3f(maxX, minY, minZ);
										vertices[2] = new Vec3f(maxX, maxY, minZ);
										vertices[3] = new Vec3f(maxX, maxY, maxZ);
										break;
								}
								int packedColor = color.getFireworkColor();
								if (data.welded() && a != null) {
									boolean adjacentN = false;
									boolean adjacentE = false;
									boolean adjacentS = false;
									boolean adjacentW = false;
									boolean invert = false;
									switch (d) {
										case DOWN:
											adjacentN = a.south();
											adjacentE = a.east();
											adjacentS = a.north();
											adjacentW = a.west();
											break;
										case UP:
											adjacentN = a.north();
											adjacentE = a.east();
											adjacentS = a.south();
											adjacentW = a.west();
											break;
										case NORTH:
											adjacentN = a.up();
											adjacentE = a.west();
											adjacentS = a.down();
											adjacentW = a.east();
											invert = true;
											break;
										case SOUTH:
											adjacentN = a.up();
											adjacentE = a.east();
											adjacentS = a.down();
											adjacentW = a.west();
											break;
										case WEST:
											adjacentN = a.up();
											adjacentE = a.south();
											adjacentS = a.down();
											adjacentW = a.north();
											invert = true;
											break;
										case EAST:
											adjacentN = a.up();
											adjacentE = a.north();
											adjacentS = a.down();
											adjacentW = a.south();
											break;
									}
									boolean showStuds = !data.doubleWelded();
									if (a.merged(d)) {
										adjacentN = adjacentE = adjacentS = adjacentW = true;
										showStuds = false;
									}
									float uScale = 2;
									float vScale = 2;
									sprite = weldedSide;
									if (d == Direction.UP) {
										sprite = showStuds ? weldedTop : welded;
									} else if (d == Direction.DOWN) {
										sprite = welded;
									}
									int kind;
									if (!adjacentN && !adjacentE && !adjacentS && !adjacentW) {
										kind = 1;
									} else if (adjacentN && adjacentE && adjacentS && adjacentW) {
										kind = 2;
									} else {
										kind = 0;
									}
									if (kind != 0) {
										qe.nominalFace(d);
										for (int i = 0; i < 4; i++) {
											qe.normal(i, d.getUnitVector());
											qe.pos(i, vertices[i]);
										}
										int u = 0;
										int v = 0;
										if (kind == 2) {
											u = 1;
											v = 1;
										}
										float minU = u/uScale;
										float maxU = (u+1)/uScale;
										float minV = v/vScale;
										float maxV = (v+1)/vScale;
										if (invert) {
											float swap = minV;
											minV = maxV;
											maxV = swap;
											swap = minU;
											minU = maxU;
											maxU = swap;
										}
										qe.sprite(0, 0, minU, maxV);
										qe.sprite(1, 0, maxU, maxV);
										qe.sprite(2, 0, maxU, minV);
										qe.sprite(3, 0, minU, minV);
										qe.spriteBake(0, sprite, QuadEmitter.BAKE_NORMALIZED);
										qe.spriteColor(0, packedColor, packedColor, packedColor, packedColor);
										qe.material(mat);
										qe.colorIndex(0);
										qe.emit();
									} else {
										Vec2i qNW, qNE, qSW, qSE;
										if (adjacentN && adjacentW) {
											qNW = new Vec2i(1, 1);
										} else if (adjacentN && !adjacentW) {
											qNW = new Vec2i(1, 0);
										} else if (!adjacentN && adjacentW) {
											qNW = new Vec2i(0, 1);
										} else { // !adjacentUp && !adjacentLeft
											qNW = new Vec2i(0, 0);
										}
										if (adjacentN && adjacentE) {
											qNE = new Vec2i(1, 1);
										} else if (adjacentN && !adjacentE) {
											qNE = new Vec2i(1, 0);
										} else if (!adjacentN && adjacentE) {
											qNE = new Vec2i(0, 1);
										} else { // !adjacentUp && !adjacentRight
											qNE = new Vec2i(0, 0);
										}
										if (adjacentS && adjacentW) {
											qSW = new Vec2i(1, 1);
										} else if (adjacentS && !adjacentW) {
											qSW = new Vec2i(1, 0);
										} else if (!adjacentS && adjacentW) {
											qSW = new Vec2i(0, 1);
										} else { // !adjacentDown && !adjacentLeft
											qSW = new Vec2i(0, 0);
										}
										if (adjacentS && adjacentE) {
											qSE = new Vec2i(1, 1);
										} else if (adjacentS && !adjacentE) {
											qSE = new Vec2i(1, 0);
										} else if (!adjacentS && adjacentE) {
											qSE = new Vec2i(0, 1);
										} else { // !adjacentDown && !adjacentRight
											qSE = new Vec2i(0, 0);
										}
										Vec2i[] quadrantUVs;
										switch (d) {
											case WEST:
											case NORTH:
												quadrantUVs = new Vec2i[] {qNE, qNW, qSW, qSE};
												break;
											default:
												quadrantUVs = new Vec2i[] {qSW, qSE, qNE, qNW};
												break;
										}
										for (int i = 0; i < vertices.length; i++) {
											qe.nominalFace(d);
											for (int j = 0; j < 4; j++) {
												qe.normal(j, d.getUnitVector());
											}
											qe.pos(i, vertices[i]);
											for (int j = 0; j < vertices.length; j++) {
												if (j != i) {
													Vec3f v = vertices[j].copy();
													v.lerp(vertices[i], 0.5f);
													qe.pos(j, v);
												}
											}
											Vec2i uvs = quadrantUVs[i];
											float minU = (uvs.x);
											float maxU = (uvs.x+.5f);
											float minV = (uvs.z);
											float maxV = (uvs.z+.5f);
											if (uvs == qNE || uvs == qSE) {
												minU += .5f;
												maxU += .5f;
											}
											if (uvs == qSW || uvs == qSE) {
												minV += .5f;
												maxV += .5f;
											}
											minU /= uScale;
											maxU /= uScale;
											minV /= vScale;
											maxV /= vScale;
											if (invert) {
												float swap = minV;
												minV = maxV;
												maxV = swap;
												swap = minU;
												minU = maxU;
												maxU = swap;
											}
											if (uvs == null) {
												float swap = minV;
												minV = maxV;
												maxV = swap;
											}
											if (uvs == null) {
												float swap = minU;
												minU = maxU;
												maxU = swap;
											}
											qe.sprite(0, 0, minU, maxV);
											qe.sprite(1, 0, maxU, maxV);
											qe.sprite(2, 0, maxU, minV);
											qe.sprite(3, 0, minU, minV);
											qe.spriteBake(0, sprite, QuadEmitter.BAKE_NORMALIZED);
											qe.spriteColor(0, packedColor, packedColor, packedColor, packedColor);
											qe.material(mat);
											qe.colorIndex(0);
											qe.emit();
										}
									}
								} else {
									qe.nominalFace(d);
									for (int i = 0; i < 4; i++) {
										qe.normal(i, d.getUnitVector());
										qe.pos(i, vertices[i]);
									}
									qe.spriteBake(0, sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
									qe.spriteColor(0, packedColor, packedColor, packedColor, packedColor);
									qe.material(mat);
									qe.colorIndex(0);
									qe.emit();
								}
							}
						}
					}
				}
			}
		} else {
			context.fallbackConsumer().accept(MinecraftClient.getInstance().getBakedModelManager().getMissingModel());
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {

	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return true;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getParticleSprite() {
		return MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_top"));
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelTransformation.NONE;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return ModelOverrideList.EMPTY;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return Collections.emptyList();
	}

	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return this;
	}

}
