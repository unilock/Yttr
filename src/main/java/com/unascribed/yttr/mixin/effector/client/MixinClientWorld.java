package com.unascribed.yttr.mixin.effector.client;

import java.util.function.Supplier;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Holder;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.unascribed.yttr.mixinsupport.YttrWorld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class MixinClientWorld extends World implements YttrWorld {

	@Shadow
	private WorldRenderer worldRenderer;

	protected MixinClientWorld(MutableWorldProperties worldProperties, RegistryKey<World> registryKey, DynamicRegistryManager registryManager, Holder<DimensionType> dimension, Supplier<Profiler> profiler, boolean client, boolean debug, long seed, int maxChainedNeighborUpdates) {
		super(worldProperties, registryKey, registryManager, dimension, profiler, client, debug, seed, maxChainedNeighborUpdates);
	}

	@Override
	public void yttr$scheduleRenderUpdate(BlockPos pos) {
		// state arguments aren't used, so don't waste time retrieving information
		worldRenderer.updateBlock(this, pos, null, null, 8);
	}
	
}
