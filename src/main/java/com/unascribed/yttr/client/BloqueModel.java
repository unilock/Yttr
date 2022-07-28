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
		if (attachment instanceof DyeColor[] colors) {
			Sprite top = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_top"));
			Sprite side = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_side"));
			Sprite bottom = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(Yttr.id("block/bloque_bottom"));
			QuadEmitter qe = context.getEmitter();
			RenderMaterial mat = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.SOLID).find();
			for (int y = 0; y < YSIZE; y++) {
				for (int x = 0; x < XSIZE; x++) {
					for (int z = 0; z < ZSIZE; z++) {
						int slot = getSlot(x, y, z);
						DyeColor color = colors[slot];
						if (color != null) {
							Box box = VOXEL_SHAPES[slot].getBoundingBox();
							for (Direction d : Direction.values()) {
								Sprite sprite = side;
								qe.nominalFace(d);
								float minX = (float)box.minX;
								float minY = (float)box.minY;
								float minZ = (float)box.minZ;
								float maxX = (float)box.maxX;
								float maxY = (float)box.maxY;
								float maxZ = (float)box.maxZ;
								switch (d) {
									case UP:
										sprite = top;
										qe.pos(3, minX, maxY, minZ);
										qe.pos(2, maxX, maxY, minZ);
										qe.pos(1, maxX, maxY, maxZ);
										qe.pos(0, minX, maxY, maxZ);
										break;
									case DOWN:
										sprite = bottom;
										qe.pos(0, minX, minY, minZ);
										qe.pos(1, maxX, minY, minZ);
										qe.pos(2, maxX, minY, maxZ);
										qe.pos(3, minX, minY, maxZ);
										break;
									case NORTH:
										qe.pos(3, minX, minY, minZ);
										qe.pos(2, maxX, minY, minZ);
										qe.pos(1, maxX, maxY, minZ);
										qe.pos(0, minX, maxY, minZ);
										break;
									case SOUTH:
										qe.pos(0, minX, minY, maxZ);
										qe.pos(1, maxX, minY, maxZ);
										qe.pos(2, maxX, maxY, maxZ);
										qe.pos(3, minX, maxY, maxZ);
										break;
									case WEST:
										qe.pos(3, minX, minY, maxZ);
										qe.pos(2, minX, minY, minZ);
										qe.pos(1, minX, maxY, minZ);
										qe.pos(0, minX, maxY, maxZ);
										break;
									case EAST:
										qe.pos(0, maxX, minY, maxZ);
										qe.pos(1, maxX, minY, minZ);
										qe.pos(2, maxX, maxY, minZ);
										qe.pos(3, maxX, maxY, maxZ);
										break;
								}
								for (int i = 0; i < 4; i++) {
									qe.normal(i, d.getUnitVector());
								}
								qe.spriteBake(0, sprite, QuadEmitter.BAKE_LOCK_UV | QuadEmitter.BAKE_NORMALIZED);
								int packedColor = color.getFireworkColor();
								qe.spriteColor(0, packedColor, packedColor, packedColor, packedColor);
								qe.material(mat);
								qe.colorIndex(0);
								qe.emit();
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
