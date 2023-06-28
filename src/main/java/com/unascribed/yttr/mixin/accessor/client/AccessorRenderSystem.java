package com.unascribed.yttr.mixin.accessor.client;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(RenderSystem.class)
public interface AccessorRenderSystem {

	@Accessor("shaderLightDirections")
	static Vector3f[] yttr$getShaderLightDirections() { throw new AbstractMethodError(); }
	
}
