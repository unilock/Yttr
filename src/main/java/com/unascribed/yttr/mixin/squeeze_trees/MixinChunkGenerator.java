package com.unascribed.yttr.mixin.squeeze_trees;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.minecraft.util.Holder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator {
	
	@Inject(at=@At("TAIL"), method="generateFeatures")
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci) {
		if (!YConfig.WorldGen.squeezeTrees) return;
		ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(world.getSeed()));
		chunkRandom.setPopulationSeed(world.getSeed(), chunk.getPos().x, chunk.getPos().z);
		if (chunkRandom.nextInt(40) == 0) {
			int x = chunkRandom.nextInt(16);
			int z = chunkRandom.nextInt(16);
			chunkRandom.setPopulationSeed(world.getSeed(), x, z);
			Holder<Biome> b = world.getBiome(new BlockPos(chunk.getPos().getStartX()+x, 0, chunk.getPos().getStartZ()+z));
			if (Biome.getCategory(b) == Category.OCEAN && b.getKey().get().getValue().getPath().contains("deep")) {
				int y = chunk.sampleHeightmap(Type.OCEAN_FLOOR_WG, x, z);
				int waterSurface = chunk.sampleHeightmap(Type.WORLD_SURFACE_WG, x, z);
				if (waterSurface - y > 20) {
					new SqueezeSaplingGenerator().generate(world, chunk.getPos().getStartPos().add(x, y, z), chunkRandom);
				}
			}
		}
	}
	
}
