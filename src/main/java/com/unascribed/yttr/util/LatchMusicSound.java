package com.unascribed.yttr.util;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LatchMusicSound extends MusicSound {

	private final LatchReference<SoundEvent> sound;
	
	public LatchMusicSound(LatchReference<SoundEvent> sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
		super(null, minDelay, maxDelay, replaceCurrentMusic);
		this.sound = sound;
	}

	@Override
	public SoundEvent getSound() {
		return sound.orElse(SoundEvents.ENTITY_WOLF_HOWL);
	}
	
}
