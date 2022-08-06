package com.unascribed.yttr.client;

import com.mojang.blaze3d.platform.GlStateManager.class_4534;
import com.mojang.blaze3d.platform.GlStateManager.class_4535;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

// extends RenderPhase for access to protected fields
public class YRenderLayers extends RenderPhase {

	private static final RenderPhase.Transparency ADDITIVE_WITH_ALPHA_TRANSPARENCY = new RenderPhase.Transparency("yttr_additive_transparency_with_alpha", () -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(class_4535.SRC_ALPHA, class_4534.ONE, class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA);
		}, () -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
	
	private static final RenderLayer LAMP_HALO = RenderLayer.of("yttr_lamp_halo",
					VertexFormats.POSITION_TEXTURE_COLOR_NORMAL,
					DrawMode.QUADS, 256, false, true,
			RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true))
				.transparency(ADDITIVE_WITH_ALPHA_TRANSPARENCY)
				.writeMaskState(new RenderPhase.WriteMaskState(true, false))
				.shader(new RenderPhase.Shader(GameRenderer::getPositionTexColorNormalShader))
				.build(false));
	
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
	
	public static RenderLayer getLampHalo() {
		return LAMP_HALO;
	}
	
	private YRenderLayers() {
		super(null, null, null);
	}
	
}
