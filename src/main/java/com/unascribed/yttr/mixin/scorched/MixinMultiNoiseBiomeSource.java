package com.unascribed.yttr.mixin.scorched;

import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBiomes;
import net.minecraft.registry.Holder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// lower priority for TerraBlender compat
@Mixin(value = MultiNoiseBiomeSource.class, priority = 100)
public abstract class MixinMultiNoiseBiomeSource {
    @Shadow
    protected abstract MultiNoiseUtil.ParameterRangeList<Holder<Biome>> getBiomeEntries();

    @Unique
    private boolean yttr$doSetScorchedBiomes = true;
    @Unique
    private Holder<Biome> yttr$scorchedSummit = null;
    @Unique
    private Holder<Biome> yttr$scorchedTerminus = null;

    @Inject(method = "getNoiseBiome", at = @At("HEAD"), cancellable = true)
    private void getNoiseBiome(int bX, int bY, int bZ, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!YConfig.WorldGen.scorched) return;
        if (yttr$doSetScorchedBiomes) {
            yttr$setScorchedBiomes();
            yttr$doSetScorchedBiomes = false;
        }
        if (yttr$scorchedSummit != null) {
            if (bY >= BiomeCoords.fromBlock(192) && yttr$scorchedTerminus != null) {
                cir.setReturnValue(yttr$scorchedTerminus);
            } else if (bY >= BiomeCoords.fromBlock(120)) {
                cir.setReturnValue(yttr$scorchedSummit);
            }
        }
    }

    @Unique
    private void yttr$setScorchedBiomes() {
        this.getBiomeEntries().getEntries().stream().map(Pair::getSecond).forEach(holder -> {
            if (holder.isBound()) {
                if (holder.isRegistryKey(YBiomes.SCORCHED_SUMMIT)) {
                    yttr$scorchedSummit = holder;
                }
                if (holder.isRegistryKey(YBiomes.SCORCHED_TERMINUS)) {
                    yttr$scorchedTerminus = holder;
                }
            }
        });
    }
}
