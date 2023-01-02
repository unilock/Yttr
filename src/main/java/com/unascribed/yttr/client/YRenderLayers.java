package com.unascribed.yttr.client;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;

// extends RenderPhase for access to protected fields
public class YRenderLayers extends RenderPhase {

	public static RenderLayer getArmorTranslucentNoCull(Identifier tex) {
		return RenderLayer.of("yttr_armor_translucent_no_cull",
					VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
					DrawMode.QUADS, 256, true, false,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(tex, false, false))
				.transparency(TRANSLUCENT_TRANSPARENCY)
				.cull(DISABLE_CULLING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.layering(VIEW_OFFSET_Z_LAYERING)
				.shader(ARMOR_CUTOUT_NO_CULL_SHADER)
				.build(true));
	}
	
	private YRenderLayers() {
		super(null, null, null);
	}
	
}
