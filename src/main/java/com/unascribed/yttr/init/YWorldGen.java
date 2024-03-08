package com.unascribed.yttr.init;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class YWorldGen {
    public static void init() {
        if (YConfig.WorldGen.gadolinite) {
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("ore_gadolinite"));
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("ore_gadolinite_deep"));
        }
        if (YConfig.WorldGen.brookite) {
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("ore_brookite"));
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("ore_brookite_deep"));
        }
    }

    public static RegistryKey<PlacedFeature> key(String path) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Yttr.id(path));
    }
}
