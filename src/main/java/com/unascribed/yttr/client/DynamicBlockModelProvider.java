package com.unascribed.yttr.client;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.YLog;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class DynamicBlockModelProvider implements ModelResourceProvider {

	private static final Map<Identifier, Class<? extends UnbakedModel>> IDS = Map.of(
			Yttr.id("builtin/cleaved_block"), CleavedBlockModel.class,
			Yttr.id("builtin/bloque"), BloqueModel.class
		);
	
	public static void init() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new DynamicBlockModelProvider());
	}

	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
		if (IDS.containsKey(resourceId)) {
			if (!RendererAccess.INSTANCE.hasRenderer()) {
	 			YLog.warn("No implementation of the Fabric Rendering API was detected. Some blocks likely won't render, and may crash the game!");
			}
			try {
				return IDS.get(resourceId).getConstructor().newInstance();
			} catch (Exception e) {
				throw new ModelProviderException("Failed to instance "+IDS.get(resourceId), e);
			}
		}
		return null;
	}

}
