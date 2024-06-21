package com.unascribed.yttr.mixin.worldgen;

import com.llamalad7.mixinextras.sugar.Local;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBiomes;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.AccessorChunkGeneratorSettings;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = "createWorlds", at = @At("RETURN"))
    private void yttr$onCreateWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci, @Local Registry<DimensionOptions> registry) {
        if (!YConfig.WorldGen.wasteland) return;
        for (World world : worlds.values()) {
            if (World.OVERWORLD.equals(world.getRegistryKey())) {
                if (registry.get(DimensionOptions.OVERWORLD).getChunkGenerator() instanceof NoiseChunkGenerator noiseChunkGenerator) {
                    ChunkGeneratorSettings chunkGeneratorSettings = noiseChunkGenerator.method_41541().value();

                    ((AccessorChunkGeneratorSettings) (Object) chunkGeneratorSettings).yttr$setSurfaceRule(
                            SurfaceRules.sequence(
                                    SurfaceRules.condition(
                                            SurfaceRules.biome(YBiomes.WASTELAND),
                                            SurfaceRules.block(YBlocks.WASTELAND_DIRT.getDefaultState())
                                    ),
                                    chunkGeneratorSettings.surfaceRule()
                            )
                    );
                }
            }
        }
    }
}
