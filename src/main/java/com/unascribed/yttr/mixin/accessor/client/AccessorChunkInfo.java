package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.WorldRenderer.ChunkInfo;
import net.minecraft.client.render.chunk.ChunkBuilder;

@Mixin(ChunkInfo.class)
public interface AccessorChunkInfo {

	@Accessor("chunk")
	ChunkBuilder.BuiltChunk yttr$getChunk();
	
}
