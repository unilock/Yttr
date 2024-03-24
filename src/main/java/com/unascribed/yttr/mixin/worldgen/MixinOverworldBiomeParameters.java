package com.unascribed.yttr.mixin.worldgen;

import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBiomes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.OverworldBiomeParameters;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

// https://github.com/LambdAurora/AurorasDecorations/blob/9073bd2012fc5547c525fde269cdb39d939bbc3d/src/main/java/dev/lambdaurora/aurorasdeco/mixin/world/OverworldBiomeParametersMixin.java
@Mixin(OverworldBiomeParameters.class)
public abstract class MixinOverworldBiomeParameters {
    @Shadow
    protected abstract void addSurfaceBiomeTo(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome);

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange coastContinentalness;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange farInlandContinentalness;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange nearInlandContinentalness;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange[] erosions;

    @Shadow
    @Final
    private MultiNoiseUtil.ParameterRange midInlandContinentalness;

    @Inject(
            method = "addMidBiomesTo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/source/util/OverworldBiomeParameters;pickRegularBiome(IILnet/minecraft/world/biome/source/util/MultiNoiseUtil$ParameterRange;)Lnet/minecraft/registry/RegistryKey;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onAddMidBiomesTo(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness,
                                  CallbackInfo ci,
                                  int temperatureIndex, MultiNoiseUtil.ParameterRange temperature,
                                  int humidityIndex, MultiNoiseUtil.ParameterRange humidity) {
        if (!YConfig.WorldGen.wasteland) return;
        if (temperatureIndex == 4 && humidityIndex == 0) {
            addSurfaceBiomeTo(
                    parameters, temperature, humidity,
                    this.nearInlandContinentalness, this.erosions[2], weirdness, 0.f,
                    YBiomes.WASTELAND
            );
            addSurfaceBiomeTo(
                    parameters, temperature, humidity,
                    MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.nearInlandContinentalness),
                    this.erosions[3],
                    weirdness,
                    0.f,
                    YBiomes.WASTELAND
            );

            if (weirdness.max() < 0L) {
                addSurfaceBiomeTo(
                        parameters, temperature, humidity,
                        MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness),
                        this.erosions[4],
                        weirdness,
                        0.f,
                        YBiomes.WASTELAND
                );
            } else {
                addSurfaceBiomeTo(
                        parameters, temperature, humidity,
                        MultiNoiseUtil.ParameterRange.combine(this.coastContinentalness, this.farInlandContinentalness),
                        this.erosions[4],
                        weirdness,
                        0.f,
                        YBiomes.WASTELAND
                );
                addSurfaceBiomeTo(
                        parameters, temperature, humidity,
                        this.coastContinentalness, this.erosions[6], weirdness, 0.f,
                        YBiomes.WASTELAND
                );
            }
        }
    }

    @Inject(
            method = "addLowBiomesTo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/source/util/OverworldBiomeParameters;pickRegularBiome(IILnet/minecraft/world/biome/source/util/MultiNoiseUtil$ParameterRange;)Lnet/minecraft/registry/RegistryKey;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onAddLowBiomesTo(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange weirdness,
                                  CallbackInfo ci,
                                  int temperatureIndex, MultiNoiseUtil.ParameterRange temperature,
                                  int humidityIndex, MultiNoiseUtil.ParameterRange humidity) {
        if (!YConfig.WorldGen.wasteland) return;
        if (temperatureIndex == 4 && humidityIndex == 0) {
            addSurfaceBiomeTo(
                    parameters, temperature, humidity,
                    this.nearInlandContinentalness,
                    MultiNoiseUtil.ParameterRange.combine(this.erosions[2], this.erosions[3]),
                    weirdness,
                    0.f,
                    YBiomes.WASTELAND
            );
            addSurfaceBiomeTo(
                    parameters, temperature, humidity,
                    MultiNoiseUtil.ParameterRange.combine(this.nearInlandContinentalness, this.farInlandContinentalness),
                    this.erosions[4],
                    weirdness,
                    0.f,
                    YBiomes.WASTELAND
            );
            addSurfaceBiomeTo(
                    parameters, temperature, humidity,
                    MultiNoiseUtil.ParameterRange.combine(this.midInlandContinentalness, this.farInlandContinentalness),
                    this.erosions[5],
                    weirdness,
                    0.f,
                    YBiomes.WASTELAND
            );
        }
    }
}