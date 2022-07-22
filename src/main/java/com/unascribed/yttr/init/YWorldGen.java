package com.unascribed.yttr.init;

@SuppressWarnings("deprecation")
public class YWorldGen {

//	public static final ConfiguredFeature<?, ?> GADOLINITE_OVERWORLD = Feature.ORE
//			.configure(new OreFeatureConfig(
//					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
//					YBlocks.GADOLINITE.getDefaultState(),
//					9))
//			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(
//					20,
//					0,
//					96)))
//			.spreadHorizontally()
//			.repeat(8);
//
//	public static final ConfiguredFeature<?, ?> BROOKITE_OVERWORLD = Feature.ORE
//			.configure(new OreFeatureConfig(
//					OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
//					YBlocks.BROOKITE_ORE.getDefaultState(),
//					5))
//			.rangeOf(32)
//			.spreadHorizontally()
//			.repeat(4);
//
//	public static final LatchReference<ConfiguredFeature<?, ?>> COPPER_OVERWORLD = YLatches.create();
//
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
//		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_FEATURE, YWorldGen.class, ConfiguredFeature.class);
//		Yttr.autoRegister(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, YWorldGen.class, ConfiguredSurfaceBuilder.class);
//		if (YConfig.WorldGen.gadolinite) {
//			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("gadolinite_overworld"));
//		}
//		if (YConfig.WorldGen.brookite) {
//			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, key("brookite_overworld"));
//		}
	}

//	public static RegistryKey<ConfiguredFeature<?, ?>> key(String path) {
//		return RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("yttr", path));
//	}

}
