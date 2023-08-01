package com.unascribed.yttr.content.block.note;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class HighNoteBlock extends AltNoteBlock {

	public HighNoteBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void buildRemap(Builder<Holder<SoundEvent>, Holder<SoundEvent>> builder) {
		builder
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, YSounds.HIGH_NOTE_BANJO_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, YSounds.HIGH_NOTE_BASEDRUM_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASS, YSounds.HIGH_NOTE_BASS_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BELL, YSounds.HIGH_NOTE_BELL_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BIT, YSounds.HIGH_NOTE_BIT_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, YSounds.HIGH_NOTE_CHIME_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, YSounds.HIGH_NOTE_COW_BELL_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, YSounds.HIGH_NOTE_DIDGERIDOO_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, YSounds.HIGH_NOTE_FLUTE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, YSounds.HIGH_NOTE_GUITAR_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HARP, YSounds.HIGH_NOTE_HARP_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HAT, YSounds.HIGH_NOTE_HAT_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, YSounds.HIGH_NOTE_IRON_XYLOPHONE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_PLING, YSounds.HIGH_NOTE_PLING_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, YSounds.HIGH_NOTE_SNARE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, YSounds.HIGH_NOTE_XYLOPHONE_HOLDER);
	}

	@Override
	public int getOctaveOffset() {
		return 2;
	}

}
