package com.unascribed.yttr.client.render;

import com.unascribed.yttr.client.IHasAClient;
import com.unascribed.yttr.content.block.mechanism.VelresinBlock;
import com.unascribed.yttr.init.YBlocks;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.hit.BlockHitResult;

public class VelresinUI extends IHasAClient {

	public static boolean render(WorldRenderContext wrc, BlockOutlineContext boc) {
		if (boc.blockState().getBlock() == YBlocks.VELRESIN && mc.crosshairTarget instanceof BlockHitResult bhr) {
			var f = VelresinBlock.getTargetedFacing(bhr);
			if (f != null) {
				var model = mc.getBakedModelManager().getModel(new ModelIdentifier("yttr", "spread_"+f.asString(), "inventory"));
				var ms = wrc.matrixStack();
				var pos = boc.blockPos();
				ms.push();
				ms.translate(pos.getX()-boc.cameraX(), pos.getY()-boc.cameraY(), pos.getZ()-boc.cameraZ());
				var vc = wrc.consumers().getBuffer(RenderLayer.getTranslucent());
				for (var bq : model.getQuads(YBlocks.VELRESIN.getDefaultState(), null, mc.textRenderer.random)) {
					vc.bakedQuad(ms.peek(), bq, 1, 1, 1, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
				}
				ms.pop();
			}
		}
		return true;
	}

}
