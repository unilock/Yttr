package com.unascribed.yttr.mixin.note;

import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.DelayedTask;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.note.AltNoteBlock;
import com.unascribed.yttr.mixinsupport.Bogged;

import net.minecraft.block.NoteBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Holder;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(value=NoteBlock.class, priority=900)
public class MixinNoteBlock {

	@ModifyArg(at=@At(value="INVOKE", target="net/minecraft/world/World.playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/Holder;Lnet/minecraft/sound/SoundCategory;FFJ)V"),
			method="onSyncedBlockEvent", index=4)
	private Holder<SoundEvent> changeSoundEvent(Holder<SoundEvent> se) {
		Object self = this;
		if (self instanceof AltNoteBlock) {
			return ((AltNoteBlock)self).remap(se);
		}
		return se;
	}
	
	private boolean yttr$dontBog = false;
	
	@Shadow
	private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {}
	
	@Inject(at=@At("HEAD"), method="playNote", cancellable=true)
	private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos, CallbackInfo ci) {
		if (yttr$dontBog) return;
		Object self = this;
		if (self instanceof Bogged && world.getServer() != null) {
			Yttr.delayedServerTasks.add(new DelayedTask(1, () -> {
				try {
					yttr$dontBog = true;
					playNote(entity, state, world, pos);
				} finally {
					yttr$dontBog = false;
				}
			}, true));
			ci.cancel();
		}
	}
	
	
}
