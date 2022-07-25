package com.unascribed.yttr.mixinsupport;

import net.minecraft.util.Holder;
import net.minecraft.world.biome.Biome;

public interface ScorchedEnablement {

	void yttr$setScorchedBiomes(Holder<Biome> summit, Holder<Biome> terminus);
	
	void yttr$copyTo(ScorchedEnablement other);
	
}
