package com.unascribed.yttr.init;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import com.unascribed.yttr.Yttr;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class YStats {

	public static final Identifier ULTRAPURE_ITEMS_CRAFTED = Yttr.id("ultrapure_items_crafted");
	@FormattedAs("time")
	public static final Identifier RIFLE_CHARGING_TIME = Yttr.id("rifle_charging_time");
	public static final Identifier RIFLE_SHOTS_FIRED = Yttr.id("rifle_shots_fired");
	public static final Identifier RIFLE_SHOTS_BACKFIRED = Yttr.id("rifle_shots_backfired");
	public static final Identifier RIFLE_SHOTS_OVERCHARGED = Yttr.id("rifle_shots_overcharged");
	@FormattedAs("distance")
	public static final Identifier BLOCKS_EFFECTED = Yttr.id("blocks_effected");
	@FormattedAs("distance")
	public static final Identifier BLOCKS_DOVE = Yttr.id("blocks_dove");
	public static final Identifier GEYSERS_OPENED = Yttr.id("geysers_opened");
	public static final Identifier BEDROCK_BROKEN = Yttr.id("bedrock_broken");
	public static final Identifier FILTERS_INSTALLED = Yttr.id("filters_installed");
	@FormattedAs("time")
	public static final Identifier TIME_IN_VOID = Yttr.id("time_in_void");
	public static final Identifier BLOCKS_VOIDED = Yttr.id("blocks_voided");
	public static final Identifier BLOCKS_CLEAVED = Yttr.id("blocks_cleaved");
	public static final Identifier BLOCKS_SHIFTED = Yttr.id("blocks_shifted");
	public static final Identifier BLOQUES_WELDED = Yttr.id("bloques_welded");
	
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Target(ElementType.FIELD)
	private @interface FormattedAs { String value(); }
	
	private static final ImmutableMap<String, StatFormatter> formattersByName = ImmutableMap.of(
			"default", StatFormatter.DEFAULT,
			"divide_by_ten", StatFormatter.DIVIDE_BY_TEN,
			"distance", StatFormatter.DISTANCE,
			"time", StatFormatter.TIME
		);
	private static final Map<Identifier, StatFormatter> formatters = Maps.newHashMap();
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.CUSTOM_STAT, YStats.class, Identifier.class);
		Yttr.autoreg.eachRegisterableField(YStats.class, Identifier.class, FormattedAs.class, (f, id, ann) -> {
			if (ann != null) {
				formatters.put(id, formattersByName.get(ann.value()));
			}
			Stats.CUSTOM.getOrCreateStat(id, formatters.getOrDefault(id, StatFormatter.DEFAULT));
		});
	}

	public static void add(LivingEntity player, Identifier id, int amt) {
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity)player).getStatHandler().increaseStat((ServerPlayerEntity)player, Stats.CUSTOM.getOrCreateStat(id, formatters.getOrDefault(id, StatFormatter.DEFAULT)), amt);
		}
	}
	
}
