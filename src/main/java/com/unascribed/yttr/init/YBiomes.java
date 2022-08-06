package com.unascribed.yttr.init;

import java.util.function.Consumer;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.LatchHolder;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.TheNetherBiomeCreator;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.OrePlacedFeatures;

public class YBiomes {

	public static final Biome SCORCHED_SUMMIT = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.temperature(2)
			.downfall(0)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x3F76E4)
					.waterFogColor(0x50533)
					.fogColor(0x330808)
					.skyColor(0x220000)
					.music(new MusicSound(YSounds.DESERT_HEAT, 12000, 24000, false))
					.particleConfig(new BiomeParticleConfig(ParticleTypes.FLAME, 0.0015f))
					.build())
			.spawnSettings(TheNetherBiomeCreator.createNetherWastes().getSpawnSettings())
			.generationSettings(new GenerationSettings.Builder().build())
			.build();

	public static final Biome SCORCHED_TERMINUS = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.temperature(2)
			.downfall(0)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x3F76E4)
					.waterFogColor(0x50533)
					.fogColor(0x000000)
					.skyColor(0x000000)
					.music(new MusicSound(YSounds.DESERT_HEAT, 12000, 24000, false))
					.particleConfig(new BiomeParticleConfig(ParticleTypes.FLAME, 0.003f))
					.build())
			.spawnSettings(TheNetherBiomeCreator.createNetherWastes().getSpawnSettings())
			.generationSettings(new GenerationSettings.Builder().build())
			.build();

	public static final Biome WASTELAND = new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.temperature(1.2f)
			.downfall(0.5f)
			.effects(new BiomeEffects.Builder()
					.waterColor(0x403E16)
					.waterFogColor(0x403E16)
					.fogColor(0x6A7053)
					.skyColor(0x848970)
					.grassColor(0x58503F)
					.foliageColor(0x58503F)
					.moodSound(BiomeMoodSound.CAVE)
					.music(new MusicSound(YSounds.MEMORANDUM, 3000, 6000, true))
					.build())
			.spawnSettings(modify(new SpawnSettings.Builder(),
						DefaultBiomeFeatures::addCaveMobs,
						b -> DefaultBiomeFeatures.addMonsters(b, 0, 40, 120, false))
					.build())
			.generationSettings(modify(new GenerationSettings.Builder(),
						DefaultBiomeFeatures::addLandCarvers,
						b -> b
							.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIRT)
							.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRAVEL),
						DefaultBiomeFeatures::addSprings,
						DefaultBiomeFeatures::addDefaultOres,
						DefaultBiomeFeatures::addDefaultDisks)
					.feature(GenerationStep.Feature.VEGETAL_DECORATION, YWorldGen.WASTELAND_GRASS_PLACED_HOLDER)
					.build()
				)
			.build();
	
	public static final LatchHolder<Biome> WASTELAND_HOLDER = LatchHolder.unset();
	public static final LatchHolder<Biome> SCORCHED_SUMMIT_HOLDER = LatchHolder.unset();
	public static final LatchHolder<Biome> SCORCHED_TERMINUS_HOLDER = LatchHolder.unset();
	
	public static void init() {
		Yttr.autoRegister(BuiltinRegistries.BIOME, YBiomes.class, Biome.class);
	}

	@SafeVarargs
	private static <T> T modify(T obj, Consumer<T>... steps) {
		for (Consumer<T> step : steps) {
			step.accept(obj);
		}
		return obj;
	}
	
}
