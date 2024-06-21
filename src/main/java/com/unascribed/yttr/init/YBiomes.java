package com.unascribed.yttr.init;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;

public class YBiomes {
    public static final RegistryKey<Biome> SCORCHED_SUMMIT = key("scorched_summit");
    public static final RegistryKey<Biome> SCORCHED_TERMINUS = key("scorched_terminus");
    public static final RegistryKey<Biome> WASTELAND = key("wasteland");

    public static final SurfaceRules.MaterialRule WASTELAND_RULE = SurfaceRules.condition(
            SurfaceRules.biome(YBiomes.WASTELAND),
            SurfaceRules.block(YBlocks.WASTELAND_DIRT.getDefaultState())
    );

    // This ensures the biomes will never generate naturally
    private static final MultiNoiseUtil.NoiseHypercube OUT_OF_RANGE = MultiNoiseUtil.createNoiseHypercube(3.01f, 3.01f, 3.01f, 3.01f, 3.01f, 3.01f, 3.01f);

    public static void init() {
        if (YConfig.WorldGen.scorched) {
            NetherBiomes.addNetherBiome(SCORCHED_SUMMIT, OUT_OF_RANGE);
            NetherBiomes.addNetherBiome(SCORCHED_TERMINUS, OUT_OF_RANGE);
        }
    }

    public static RegistryKey<Biome> key(String path) {
        return RegistryKey.of(RegistryKeys.BIOME, Yttr.id(path));
    }
}
