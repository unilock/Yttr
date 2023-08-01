package com.unascribed.yttr.content.block.note;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class LowNoteBlock extends AltNoteBlock {

	public LowNoteBlock(Settings settings) {
		super(settings);
	}

	@Override
	public void buildRemap(Builder<Holder<SoundEvent>, Holder<SoundEvent>> builder) {
		builder
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, YSounds.LOW_NOTE_BANJO_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, YSounds.LOW_NOTE_BASEDRUM_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BASS, YSounds.LOW_NOTE_BASS_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BELL, YSounds.LOW_NOTE_BELL_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_BIT, YSounds.LOW_NOTE_BIT_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, YSounds.LOW_NOTE_CHIME_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, YSounds.LOW_NOTE_COW_BELL_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, YSounds.LOW_NOTE_DIDGERIDOO_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, YSounds.LOW_NOTE_FLUTE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, YSounds.LOW_NOTE_GUITAR_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HARP, YSounds.LOW_NOTE_HARP_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_HAT, YSounds.LOW_NOTE_HAT_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, YSounds.LOW_NOTE_IRON_XYLOPHONE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_PLING, YSounds.LOW_NOTE_PLING_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, YSounds.LOW_NOTE_SNARE_HOLDER)
			.put(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, YSounds.LOW_NOTE_XYLOPHONE_HOLDER);
	}

	@Override
	public int getOctaveOffset() {
		return -2;
	}

}
