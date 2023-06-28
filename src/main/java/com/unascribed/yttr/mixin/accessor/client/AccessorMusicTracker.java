package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;

@Environment(EnvType.CLIENT)
@Mixin(MusicTracker.class)
public interface AccessorMusicTracker {

	@Accessor("currentMusic")
	SoundInstance yttr$getCurrent();
	
}
