package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.MultiNoiseSampler;

@Mixin(MultiNoiseBiomeSource.class)
public class MixinMultiNoiseBiomeSource implements ScorchedEnablement {

	private Holder<Biome> yttr$scorchedSummit = null;
	private Holder<Biome> yttr$scorchedTerminus = null;
	
	@Inject(at=@At("HEAD"), method="getNoiseBiome", cancellable=true)
	public void getNoiseBiome(int bX, int bY, int bZ, MultiNoiseSampler noise, CallbackInfoReturnable<Holder<Biome>> ci) {
		if (!YConfig.WorldGen.scorched) return;
		if (yttr$scorchedSummit != null) {
			if (bY > (192>>2) && yttr$scorchedTerminus != null) {
				System.out.println("Terminus @ "+bY);
				ci.setReturnValue(yttr$scorchedTerminus);
			} else if (bY > (128>>2)) {
				System.out.println("Summit @ "+bY);
				ci.setReturnValue(yttr$scorchedSummit);
			}
		}
	}
	
	@Override
	public void yttr$setScorchedBiomes(Holder<Biome> summit, Holder<Biome> terminus) {
		System.out.println(this+" has received biomes: "+summit+" "+terminus);
		yttr$scorchedSummit = summit;
		yttr$scorchedTerminus = terminus;
	}
	
	@Override
	public void yttr$copyTo(ScorchedEnablement other) {
		other.yttr$setScorchedBiomes(yttr$scorchedSummit, yttr$scorchedTerminus);
	}

}
