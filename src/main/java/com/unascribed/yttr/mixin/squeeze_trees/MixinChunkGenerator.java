package com.unascribed.yttr.mixin.squeeze_trees;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator {
	
	@Inject(at=@At("TAIL"), method="generateFeatures")
	public void generateFeatures(ChunkRegion region, StructureAccessor accessor, CallbackInfo ci) {
		if (!YConfig.WorldGen.squeezeTrees) return;
		ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(region.getSeed()));
		chunkRandom.setPopulationSeed(region.getSeed(), region.getCenterPos().x, region.getCenterPos().z);
		if (chunkRandom.nextInt(40) == 0) {
			int x = region.getCenterPos().getStartX()+chunkRandom.nextInt(16);
			int z = region.getCenterPos().getStartZ()+chunkRandom.nextInt(16);
			chunkRandom.setPopulationSeed(region.getSeed(), x, z);
			RegistryEntry<Biome> b = region.getBiome(new BlockPos(x, 0, z));
			if (Biome.getCategory(b) == Category.OCEAN && b.getKey().get().getValue().getPath().contains("deep")) {
				int y = region.getTopY(Type.OCEAN_FLOOR_WG, x, z);
				int waterSurface = region.getTopY(Type.WORLD_SURFACE_WG, x, z);
				if (waterSurface - y > 20) {
					new SqueezeSaplingGenerator().generate(region, new BlockPos(x, y, z), chunkRandom);
				}
			}
		}
	}
	
}
