package com.unascribed.yttr.mixin.worldgen;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.datafixers.util.Pair;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBiomes;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.OverworldBiomeParameters;

// https://github.com/LambdAurora/AurorasDecorations/blob/46acabb2d822e82fa0e0816ab0dec78609aa74ec/src/main/java/dev/lambdaurora/aurorasdeco/mixin/world/OverworldBiomeParametersMixin.java
@Mixin(OverworldBiomeParameters.class)
public abstract class MixinOverworldBiomeParameters {
	@Shadow
	protected abstract void addSurfaceBiomeTo(Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> parameters, MultiNoiseUtil.ParameterRange temperature, MultiNoiseUtil.ParameterRange humidity, MultiNoiseUtil.ParameterRange continentalness, MultiNoiseUtil.ParameterRange erosion, MultiNoiseUtil.ParameterRange weirdness, float offset, RegistryKey<Biome> biome);

	@Shadow
	@Final
	private MultiNoiseUtil.ParameterRange COAST_CONTINENTALNESS;

	@Shadow
	@Final
	private MultiNoiseUtil.ParameterRange FAR_INLAND_CONTINENTALNESS;

	@Shadow
	@Final
	private MultiNoiseUtil.ParameterRange NEAR_INLAND_CONTINENTALNESS;

	@Shadow
	@Final
	private MultiNoiseUtil.ParameterRange[] EROSIONS;

	@Shadow
	@Final
	private MultiNoiseUtil.ParameterRange MID_INLAND_CONTINENTALNESS;

	@Inject(
			method = "addMidBiomesTo",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/source/util/OverworldBiomeParameters;pickRegularBiome(IILnet/minecraft/world/biome/source/util/MultiNoiseUtil$ParameterRange;)Lnet/minecraft/util/registry/RegistryKey;"
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
					NEAR_INLAND_CONTINENTALNESS, EROSIONS[2], weirdness, 0.f,
					YBiomes.WASTELAND_HOLDER.getKey().get()
				);
			addSurfaceBiomeTo(
					parameters, temperature, humidity,
					MultiNoiseUtil.ParameterRange.combine(COAST_CONTINENTALNESS, NEAR_INLAND_CONTINENTALNESS),
					EROSIONS[3],
					weirdness,
					0.f,
					YBiomes.WASTELAND_HOLDER.getKey().get()
				);

			if (weirdness.max() < 0L) {
				addSurfaceBiomeTo(
						parameters, temperature, humidity,
						MultiNoiseUtil.ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
						EROSIONS[4],
						weirdness,
						0.f,
						YBiomes.WASTELAND_HOLDER.getKey().get()
					);
			} else {
				addSurfaceBiomeTo(
						parameters, temperature, humidity,
						MultiNoiseUtil.ParameterRange.combine(COAST_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
						EROSIONS[4],
						weirdness,
						0.f,
						YBiomes.WASTELAND_HOLDER.getKey().get()
					);
				addSurfaceBiomeTo(
						parameters, temperature, humidity,
						COAST_CONTINENTALNESS, EROSIONS[6], weirdness, 0.f,
						YBiomes.WASTELAND_HOLDER.getKey().get()
					);
			}
		}
	}

	@Inject(
			method = "addLowBiomesTo",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/source/util/OverworldBiomeParameters;pickRegularBiome(IILnet/minecraft/world/biome/source/util/MultiNoiseUtil$ParameterRange;)Lnet/minecraft/util/registry/RegistryKey;"
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
					NEAR_INLAND_CONTINENTALNESS,
					MultiNoiseUtil.ParameterRange.combine(EROSIONS[2], EROSIONS[3]),
					weirdness,
					0.f,
					YBiomes.WASTELAND_HOLDER.getKey().get()
				);
			addSurfaceBiomeTo(
					parameters, temperature, humidity,
					MultiNoiseUtil.ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
					EROSIONS[4],
					weirdness,
					0.f,
					YBiomes.WASTELAND_HOLDER.getKey().get()
				);
			addSurfaceBiomeTo(
					parameters, temperature, humidity,
					MultiNoiseUtil.ParameterRange.combine(MID_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
					EROSIONS[5],
					weirdness,
					0.f,
					YBiomes.WASTELAND_HOLDER.getKey().get()
				);
		}
	}
}
