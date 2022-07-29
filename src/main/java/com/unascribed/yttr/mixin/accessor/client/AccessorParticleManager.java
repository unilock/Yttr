package com.unascribed.yttr.mixin.accessor.client;

import java.util.Map;
import java.util.Queue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public interface AccessorParticleManager {

	@Accessor("particles")
	Map<ParticleTextureSheet, Queue<Particle>> yttr$getParticles();
	
	@Accessor("factories")
	Int2ObjectMap<ParticleFactory<?>> yttr$getFactories();
	
	@Accessor("spriteAwareFactories")
	Map<Identifier, Object> yttr$getSpriteAwareFactories();
	
}
