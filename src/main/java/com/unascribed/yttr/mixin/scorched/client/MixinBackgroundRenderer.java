package com.unascribed.yttr.mixin.scorched.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.world.dimension.DimensionTypes;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

	@ModifyVariable(at=@At("HEAD"), method="applyFog", argsOnly=true, index=3)
	private static boolean adjustUseThickFog(boolean orig) {
		if (orig) {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (mc.world != null && mc.world.getRegistryKey().getValue().equals(DimensionTypes.THE_NETHER_ID)) {
				var id = mc.world.getBiome(mc.player.getBlockPos()).getKey().map(e -> e.getValue()).orElse(null);
				if (id != null && id.getNamespace().equals("yttr") && id.getPath().startsWith("scorched_")) {
					return false;
				}
			}
		}
		return orig;
	}
	
}
