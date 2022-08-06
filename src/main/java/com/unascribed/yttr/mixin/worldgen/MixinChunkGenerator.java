package com.unascribed.yttr.mixin.worldgen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.world.ScorchedGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(ChunkGenerator.class)
public abstract class MixinChunkGenerator {
	
	@Inject(at=@At("TAIL"), method="generateFeatures")
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureManager structureAccessor, CallbackInfo ci) {
		ScorchedGenerator.generateTerminus(world.getSeed(), (ChunkRegion)world, structureAccessor);
	}
	
}
