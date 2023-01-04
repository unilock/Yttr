package com.unascribed.yttr.mixin.rifle.client;

import java.util.function.Supplier;

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
import net.minecraft.util.Holder;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World {

	protected MixinClientWorld(MutableWorldProperties properties, RegistryKey<World> registryKey, Holder<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
		super(properties, registryKey, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
	}

	@Inject(at=@At("HEAD"), method="m_dmodcllo", cancellable=true)
	public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, long seed, CallbackInfo ci) {
		if (player != MinecraftClient.getInstance().player) return;
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
