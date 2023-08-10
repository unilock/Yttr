package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules.*;

public class YGameRules {

	public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(Yttr.id("yttr"), Text.literal("Yttr"));
	
	public static final Key<IntRule> PLATFORM_DECAY_TICKS = GameRuleRegistry.register("yttr:platformDecayTicks", CATEGORY, GameRuleFactory.createIntRule(200));
	public static final Key<IntRule> PLATFORM_DECAY_SLEW = GameRuleRegistry.register("yttr:platformDecaySlew", CATEGORY, GameRuleFactory.createIntRule(40));
	
	public static void init() {}
	
}
