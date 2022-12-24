package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
public interface AccessorParticle {

	@Accessor("collidesWithWorld")
	void yttr$setCollidesWithWorld(boolean collidesWithWorld);
	@Accessor("gravityStrength")
	void yttr$setGravityStrength(float gravityStrength);
	
	@Accessor("x")
	double yttr$getX();
	@Accessor("y")
	double yttr$getY();
	@Accessor("z")
	double yttr$getZ();
	
}
