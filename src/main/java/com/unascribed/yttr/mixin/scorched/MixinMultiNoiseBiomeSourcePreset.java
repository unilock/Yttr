package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.Holder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource.Preset;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

@Mixin(value=MultiNoiseBiomeSource.Preset.class, priority=5000)
public class MixinMultiNoiseBiomeSourcePreset {
	
	@Inject(at=@At("RETURN"), method="getBiomeSource(Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource$Instance;Z)Lnet/minecraft/world/biome/source/MultiNoiseBiomeSource;",
			locals=LocalCapture.CAPTURE_FAILHARD)
	private void getBiomeSource(MultiNoiseBiomeSource.Instance instance, boolean useInstance, CallbackInfoReturnable<MultiNoiseBiomeSource> ci,
			MultiNoiseUtil.ParameterRangeList<Holder<Biome>> entries) {
		Object self = this;
		if (self == Preset.NETHER) {
			var registry = instance.biomeRegistry();
			((ScorchedEnablement)ci.getReturnValue()).yttr$setScorchedBiomes(
					registry.getOrCreateHolder(RegistryKey.of(Registry.BIOME_KEY, Yttr.id("scorched_summit"))),
					registry.getOrCreateHolder(RegistryKey.of(Registry.BIOME_KEY, Yttr.id("scorched_terminus")))
				);
		}
	}
	
}
