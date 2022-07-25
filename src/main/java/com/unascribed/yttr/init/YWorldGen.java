package com.unascribed.yttr.init;

import java.util.List;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.InSquarePlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public class YWorldGen {

	public static final ConfiguredFeature<OreFeatureConfig, Feature<OreFeatureConfig>> GADOLINITE_OVERWORLD = new ConfiguredFeature<>
			(Feature.ORE,
					new OreFeatureConfig(
						List.of(
							OreFeatureConfig.createTarget(
								OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
								YBlocks.GADOLINITE.getDefaultState()
							),
							OreFeatureConfig.createTarget(
								OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
								YBlocks.DEEPSLATE_GADOLINITE.getDefaultState()
							)
						),
					9));

	public static final PlacedFeature GADOLINITE_OVERWORLD_MAIN = new PlacedFeature(
			Holder.createDirect(GADOLINITE_OVERWORLD),
			List.of(
					CountPlacementModifier.create(10),
					InSquarePlacementModifier.getInstance(),
					HeightRangePlacementModifier.createUniform(YOffset.fixed(20), YOffset.fixed(96))
			));
	public static final PlacedFeature GADOLINITE_OVERWORLD_DEEP = new PlacedFeature(
			Holder.createDirect(GADOLINITE_OVERWORLD),
			List.of(
					CountPlacementModifier.create(5),
					InSquarePlacementModifier.getInstance(),
					HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(20), YOffset.fixed(10))
			));

	public static final ConfiguredFeature<OreFeatureConfig, Feature<OreFeatureConfig>> BROOKITE_ORE_OVERWORLD = new ConfiguredFeature<>
			(Feature.ORE,
					new OreFeatureConfig(
						List.of(
							OreFeatureConfig.createTarget(
								OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
								YBlocks.BROOKITE_ORE.getDefaultState()
							),
							OreFeatureConfig.createTarget(
								OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
								YBlocks.DEEPSLATE_BROOKITE_ORE.getDefaultState()
							)
						),
					5));

	public static final PlacedFeature BROOKITE_ORE_OVERWORLD_MAIN = new PlacedFeature(
			Holder.createDirect(BROOKITE_ORE_OVERWORLD),
			List.of(
					CountPlacementModifier.create(4),
					InSquarePlacementModifier.getInstance(),
					HeightRangePlacementModifier.createUniform(YOffset.fixed(0), YOffset.fixed(32))
			));
	public static final PlacedFeature BROOKITE_ORE_OVERWORLD_DEEP = new PlacedFeature(
			Holder.createDirect(BROOKITE_ORE_OVERWORLD),
			List.of(
					CountPlacementModifier.create(6),
					InSquarePlacementModifier.getInstance(),
					HeightRangePlacementModifier.trapezoid(YOffset.getBottom(), YOffset.fixed(4))
			));

//	public static final ConfiguredFeature<?, ?> WASTELAND_GRASS = Feature.RANDOM_PATCH
//			.configure(new RandomPatchFeatureConfig.Builder(
//					new SimpleBlockStateProvider(YBlocks.WASTELAND_GRASS.getDefaultState()), SimpleBlockPlacer.INSTANCE)
//				.tries(4)
//				.build())
//			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
//			.repeat(4);
//
//	public static final ConfiguredSurfaceBuilder<?> WASTELAND_SURFACE = SurfaceBuilder.DEFAULT
//			.withConfig(new TernarySurfaceConfig(YBlocks.WASTELAND_DIRT.getDefaultState(), YBlocks.WASTELAND_DIRT.getDefaultState(), Blocks.STONE.getDefaultState()));
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_FEATURE, YWorldGen.class, ConfiguredFeature.class);
		Yttr.autoRegister(BuiltinRegistries.PLACED_FEATURE, YWorldGen.class, PlacedFeature.class);
		if (YConfig.WorldGen.gadolinite) {
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("gadolinite_overworld_main"));
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("gadolinite_overworld_deep"));
		}
		if (YConfig.WorldGen.brookite) {
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("brookite_ore_overworld_main"));
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("brookite_ore_overworld_deep"));
		}
	}

	public static RegistryKey<PlacedFeature> key(String path) {
		return RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier("yttr", path));
	}

}
