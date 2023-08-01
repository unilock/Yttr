package com.unascribed.yttr.content.block.note;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.block.NoteBlock;
import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;

public abstract class AltNoteBlock extends NoteBlock {

	protected final ImmutableMap<Holder<SoundEvent>, Holder<SoundEvent>> remap;
	
	public AltNoteBlock(Settings settings) {
		super(settings);
		Builder<Holder<SoundEvent>, Holder<SoundEvent>> bldr = ImmutableMap.builder();
		buildRemap(bldr);
		this.remap = bldr.build();
	}

	public Holder<SoundEvent> remap(Holder<SoundEvent> event) {
		return remap.getOrDefault(event, event);
	}
	
	public abstract void buildRemap(Builder<Holder<SoundEvent>, Holder<SoundEvent>> builder);
	public abstract int getOctaveOffset();
	
}
