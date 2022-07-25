package com.unascribed.yttr.mixin.worldgen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.unascribed.yttr.init.YBiomes;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules.SequenceMaterialRule;

@Mixin(VanillaSurfaceRules.class)
public class MixinVanillaSurfaceRules {

	@ModifyVariable(at=@At(value="CONSTANT", args="doubleValue=-0.909", ordinal=0),
			method="getOverworldLikeRules", ordinal=6)
	private static SurfaceRules.MaterialRule modifyDirtRule(SurfaceRules.MaterialRule rule) {
		((SequenceMaterialRule)rule).sequence().add(0,
				SurfaceRules.condition(
					SurfaceRules.biome(YBiomes.WASTELAND_HOLDER.getKey().get()),
					SurfaceRules.block(YBlocks.WASTELAND_DIRT.getDefaultState())
				));
		return rule;
	}

	@ModifyVariable(at=@At(value="CONSTANT", args="doubleValue=-0.909", ordinal=0),
			method="getOverworldLikeRules", ordinal=7)
	private static SurfaceRules.MaterialRule modifyGrassRule(SurfaceRules.MaterialRule rule) {
		((SequenceMaterialRule)rule).sequence().add(0,
				SurfaceRules.condition(
					SurfaceRules.biome(YBiomes.WASTELAND_HOLDER.getKey().get()),
					SurfaceRules.block(YBlocks.WASTELAND_DIRT.getDefaultState())
				));
		return rule;
	}
	
}
