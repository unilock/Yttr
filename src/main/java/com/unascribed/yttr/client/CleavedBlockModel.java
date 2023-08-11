package com.unascribed.yttr.client;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.unascribed.yttr.client.cache.CleavedBlockMeshes;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity.CleavedMeshTarget;
import com.unascribed.yttr.util.YLog;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.resource.Material;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;

public class CleavedBlockModel implements UnbakedModel, BakedModel, FabricBakedModel {

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
		if (!(blockView instanceof RenderAttachedBlockView)) return;
		Object attachment = ((RenderAttachedBlockView)blockView).getBlockEntityRenderAttachment(pos);
		try {
			if (attachment instanceof Mesh m) {
				m.outputTo(context.getEmitter());
			} else if (attachment instanceof CleavedMeshTarget tgt) {
				Mesh mesh = (Mesh)tgt.cachedMesh;
				if (mesh == null || tgt.era != CleavedBlockMeshes.era) {
					mesh = CleavedBlockMeshes.getMesh(tgt.key);
					tgt.cachedMesh = mesh;
					tgt.era = CleavedBlockMeshes.era;
				} else {
				}
				mesh.outputTo(context.getEmitter());
			} else {
				MinecraftClient.getInstance().getBakedModelManager().getMissingModel().emitBlockQuads(blockView, state, pos, randomSupplier, context);
			}
		} catch (ClassCastException e) {
			// XXX Temporary BC23 workaround for a crash in Too Many Origins with Create
			YLog.warn("Caught an exception while meshing a cleaved block. This isn't good! Please go see what's happening at "+pos+" (but that's probably a fake coordinate inside a Create contraption...) Good luck!", e);
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomGenerator> randomSupplier, RenderContext context) {
		
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, RandomGenerator random) {
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
		return MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new Identifier("minecraft", "block/soul_sand"));
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
	public void resolveParents(Function<Identifier, UnbakedModel> models) {
	}

	@Override
	public BakedModel bake(ModelBaker modelBaker, Function<Material, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
		return this;
	}

}
