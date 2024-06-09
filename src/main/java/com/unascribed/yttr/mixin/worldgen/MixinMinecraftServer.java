package com.unascribed.yttr.mixin.worldgen;

import com.google.common.collect.Streams;
import com.llamalad7.mixinextras.sugar.Local;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
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
import java.util.stream.Stream;

// lower priority for TerraBlender compat
@Mixin(value = MinecraftServer.class, priority = 100)
public class MixinMinecraftServer {
    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = "createWorlds", at = @At("RETURN"))
    private void yttr$onCreateWorlds(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci, @Local Registry<DimensionOptions> registry) {
        SurfaceRules.MaterialRule[] rulesType = new SurfaceRules.MaterialRule[0];

        for (World world : worlds.values()) {
            if (World.OVERWORLD.equals(world.getRegistryKey())) {
                DimensionOptions dimensionOptions = registry.get(DimensionOptions.OVERWORLD);
                ChunkGenerator chunkGenerator = dimensionOptions.getChunkGenerator();

                if (chunkGenerator instanceof NoiseChunkGenerator noiseChunkGenerator) {
                    ChunkGeneratorSettings chunkGeneratorSettings = noiseChunkGenerator.method_41541().value();

                    ((AccessorChunkGeneratorSettings) (Object) chunkGeneratorSettings).yttr$setSurfaceRule(
                            SurfaceRules.sequence(Streams.concat(
                                    Stream.of(
                                            SurfaceRules.condition(
                                                    SurfaceRules.biome(YBiomes.WASTELAND),
                                                    SurfaceRules.block(YBlocks.WASTELAND_DIRT.getDefaultState())
                                            )
                                    ),
                                    Stream.of(
                                            chunkGeneratorSettings.surfaceRule()
                                    )
                            ).toList().toArray(rulesType))
                    );
                }
            }
        }
    }
}
