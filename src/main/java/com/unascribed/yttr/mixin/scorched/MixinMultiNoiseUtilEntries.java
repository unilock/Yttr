package com.unascribed.yttr.mixin.scorched;

import org.spongepowered.asm.mixin.Mixin;
import com.unascribed.yttr.mixinsupport.ScorchedEnablement;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

@Mixin(MultiNoiseUtil.Entries.class)
public class MixinMultiNoiseUtilEntries implements ScorchedEnablement {

	private RegistryEntry<Biome> yttr$scorchedSummit = null;
	private RegistryEntry<Biome> yttr$scorchedTerminus = null;
	
	@Override
	public void yttr$setScorchedBiomes(RegistryEntry<Biome> summit, RegistryEntry<Biome> terminus) {
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
