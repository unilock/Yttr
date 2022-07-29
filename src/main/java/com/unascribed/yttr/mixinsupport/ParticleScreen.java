package com.unascribed.yttr.mixinsupport;

import com.unascribed.yttr.util.DummyClientWorld;

import net.minecraft.client.particle.ParticleManager;

public interface ParticleScreen {

	ParticleManager yttr$getParticleManager();
	DummyClientWorld yttr$getParticleWorld();
	
}
