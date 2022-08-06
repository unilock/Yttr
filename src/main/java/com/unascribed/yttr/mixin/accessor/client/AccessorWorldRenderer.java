package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.WorldRenderer.ChunkInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public interface AccessorWorldRenderer {

	@Accessor("needsFullBuiltChunkUpdate")
	void yttr$setNeedsTerrainUpdate(boolean b);
	
	@Accessor("chunkInfoList")
	ObjectArrayList<ChunkInfo> yttr$getChunkInfos();
	
}
