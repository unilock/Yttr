package com.unascribed.yttr.mixin.rifle.client;

import java.util.function.Supplier;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.init.YSounds;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {

	protected MixinClientWorld(MutableWorldProperties worldProperties, RegistryKey<World> registryKey, DynamicRegistryManager registryManager, Holder<DimensionType> dimension, Supplier<Profiler> profiler, boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
		super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed, maxChainedNeighborUpdates);
	}

	@Inject(at=@At("HEAD"), method="playSoundFromEntity", cancellable=true)
	public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, Holder<SoundEvent> soundh, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
		if (player != MinecraftClient.getInstance().player) return;
		var sound = soundh.value();
		if (sound == YSounds.RIFLE_CHARGE_CANCEL) {
			SoundInstance si = YttrClient.rifleChargeSounds.remove(entity);
			if (si != null) {
				MinecraftClient.getInstance().send(() -> {
					MinecraftClient.getInstance().getSoundManager().stop(si);
				});
			}
			ci.cancel();
		} else if (sound == YSounds.DROP_CAST_CANCEL) {
			SoundInstance si = YttrClient.dropCastSounds.remove(entity);
			if (si != null) {
				MinecraftClient.getInstance().send(() -> {
					MinecraftClient.getInstance().getSoundManager().stop(si);
				});
			}
			ci.cancel();
		}
	}
	
}
