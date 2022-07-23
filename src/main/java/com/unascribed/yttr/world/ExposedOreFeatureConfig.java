package com.unascribed.yttr.world;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class ExposedOreFeatureConfig extends OreFeatureConfig {

	public final float discardOnBuriedChance;
	
	public ExposedOreFeatureConfig(List<Target> targets, int size, float discardOnBuriedChance) {
		super(targets, size, 0);
		this.discardOnBuriedChance = discardOnBuriedChance;
	}

	public ExposedOreFeatureConfig(List<Target> targets, int size) {
		this(targets, size, 0);
	}

	public ExposedOreFeatureConfig(RuleTest test, BlockState state, int size, float discardOnBuriedChance) {
		super(test, state, size, 0);
		this.discardOnBuriedChance = discardOnBuriedChance;
	}

	public ExposedOreFeatureConfig(RuleTest test, BlockState state, int size) {
		this(test, state, size, 0);
	}

	
	
}
