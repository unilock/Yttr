package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

@Mixin(value=MultiNoiseBiomeSource.Preset.class, priority=5000)
public class MixinMultiNoiseBiomeSourcePreset {

	// nether lambda
	@Inject(at=@At("RETURN"), method="method_31088(Lnet/minecraft/util/registry/Registry;)Lnet/minecraft/world/biome/source/util/MultiNoiseUtil$Entries;")
	private static void presetSetup(Registry<Biome> registry, CallbackInfoReturnable<MultiNoiseUtil.Entries<?>> ci) {
		((ScorchedEnablement)ci.getReturnValue()).yttr$setScorchedBiomes(
				registry.getOrCreateEntry(RegistryKey.of(Registry.BIOME_KEY, new Identifier("yttr", "scorched_summit"))),
				registry.getOrCreateEntry(RegistryKey.of(Registry.BIOME_KEY, new Identifier("yttr", "scorched_terminus")))
			);
	}
	
	@Inject(at=@At("RETURN"), method="getBiomeSource(Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$Instance;Z)Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;",
			locals=LocalCapture.CAPTURE_FAILHARD)
	private void getBiomeSource(@Coerce Object instance, boolean useInstance, CallbackInfoReturnable<MultiNoiseBiomeSource> ci,
			MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries) {
		((ScorchedEnablement)entries).yttr$copyTo((ScorchedEnablement)ci.getReturnValue());
	}
	
}
