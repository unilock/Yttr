package com.unascribed.yttr.mixin.worldgen;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.world.ScorchedGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.HolderSet;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.RandomState;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

@Mixin(NoiseChunkGenerator.class)
public abstract class MixinNoiseChunkGenerator extends ChunkGenerator {

	public MixinNoiseChunkGenerator(Registry<net.minecraft.world.gen.structure.StructureSet> registry, Optional<HolderSet<net.minecraft.world.gen.structure.StructureSet>> optional, BiomeSource biomeSource) {
		super(registry, optional, biomeSource);
	}

	@Inject(at=@At("TAIL"), method="buildSurface")
	public void buildSurface(ChunkRegion region, StructureManager structureManager, RandomState randomState, Chunk chunk, CallbackInfo ci) {
		ScorchedGenerator.generateSummit(region, chunk);
	}
	
}
