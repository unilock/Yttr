package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

@Mixin(MultiNoiseUtil.ParameterRangeList.class)
public class MixinMultiNoiseUtilParameterRangeList implements ScorchedEnablement {

	private Holder<Biome> yttr$scorchedSummit = null;
	private Holder<Biome> yttr$scorchedTerminus = null;
	
	@Override
	public void yttr$setScorchedBiomes(Holder<Biome> summit, Holder<Biome> terminus) {
		System.out.println(this+" has received biomes: "+summit+" "+terminus);
		yttr$scorchedSummit = summit;
		yttr$scorchedTerminus = terminus;
	}
	
	@Override
	public void yttr$copyTo(ScorchedEnablement other) {
		System.out.println(this+" sending biomes to "+other);
		other.yttr$setScorchedBiomes(yttr$scorchedSummit, yttr$scorchedTerminus);
	}

}
