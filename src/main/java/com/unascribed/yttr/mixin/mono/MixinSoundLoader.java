package com.unascribed.yttr.mixin.mono;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.MonoAudioStream;
import com.unascribed.yttr.client.MonoIdentifier;
import com.unascribed.yttr.client.YttrClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(SoundLoader.class)
public class MixinSoundLoader {

	@Shadow @Final
	private ResourceFactory resourceManager;
	
	@Inject(at=@At("HEAD"), method="loadStreamed", cancellable=true)
	public void yttr$loadStreamedHead(Identifier id, boolean repeatInstantly, CallbackInfoReturnable<CompletableFuture<AudioStream>> ci) {
		YttrClient.forceIbxmMono = id instanceof MonoIdentifier;
	}
	
	@Inject(at=@At("RETURN"), method="loadStreamed", cancellable=true)
	public void yttr$loadStreamedReturn(Identifier id, boolean repeatInstantly, CallbackInfoReturnable<CompletableFuture<AudioStream>> ci) {
		if (id instanceof MonoIdentifier) {
			ci.setReturnValue(ci.getReturnValue()
					.thenApply(MonoAudioStream::new));
		}
	}

}
