package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(VertexBuffer.class)
public interface AccessorVertexBuffer {

	@Accessor("vertexCount")
	int yttr$getVertexCount();
	
}
