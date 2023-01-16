package com.unascribed.yttr.compat.trinkets;

import com.unascribed.yttr.client.render.AmmoPackRenderer;
import com.unascribed.yttr.client.render.CuprosteelCoilRenderer;
import com.unascribed.yttr.client.render.PlatformsRenderer;
import com.unascribed.yttr.init.YItems;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;

public class YttrTrinketsCompatClient {

	public static void init() {
		TrinketRendererRegistry.registerRenderer(YItems.CUPROSTEEL_COIL.get(), CuprosteelCoilRenderer::render);
		TrinketRendererRegistry.registerRenderer(YItems.AMMO_PACK.get(), AmmoPackRenderer::render);
		TrinketRendererRegistry.registerRenderer(YItems.PLATFORMS.get(), PlatformsRenderer::render);
	}
	
}
