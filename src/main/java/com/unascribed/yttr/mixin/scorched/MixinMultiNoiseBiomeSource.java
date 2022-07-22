package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinMultiNoiseBiomeSource implements ScorchedEnablement {

	private RegistryEntry<Biome> yttr$scorchedSummit = null;
	private RegistryEntry<Biome> yttr$scorchedTerminus = null;
	
	@Inject(at=@At("HEAD"), method="getBiome", cancellable=true)
	public void getBiomeForNoiseGen(int bX, int bY, int bZ, MultiNoiseSampler noise, CallbackInfoReturnable<RegistryEntry<Biome>> ci) {
		if (!YConfig.WorldGen.scorched) return;
		if (yttr$scorchedSummit != null) {
			if (bY > (192>>2) && yttr$scorchedTerminus != null) {
				ci.setReturnValue(yttr$scorchedTerminus);
			} else if (bY > (128>>2)) {
				ci.setReturnValue(yttr$scorchedSummit);
			}
		}
	}
	
	@Override
	public void yttr$setScorchedBiomes(RegistryEntry<Biome> summit, RegistryEntry<Biome> terminus) {
		yttr$scorchedSummit = summit;
		yttr$scorchedTerminus = terminus;
	}

}
