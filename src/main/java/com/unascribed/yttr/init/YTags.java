package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class YTags {

	public static final class Item {

		public static final TagKey<net.minecraft.item.Item> UNSNAREABLE = TagKey.of(RegistryKeys.ITEM, Yttr.id("unsnareable"));
		public static final TagKey<net.minecraft.item.Item> VOID_IMMUNE = TagKey.of(RegistryKeys.ITEM, Yttr.id("void_immune"));
		public static final TagKey<net.minecraft.item.Item> FLUXES = TagKey.of(RegistryKeys.ITEM, Yttr.id("fluxes"));
		public static final TagKey<net.minecraft.item.Item> ULTRAPURE_CUBES = TagKey.of(RegistryKeys.ITEM, Yttr.id("ultrapure_cubes"));
		public static final TagKey<net.minecraft.item.Item> GIFTS = TagKey.of(RegistryKeys.ITEM, Yttr.id("gifts"));
		public static final TagKey<net.minecraft.item.Item> NOT_GIFTS = TagKey.of(RegistryKeys.ITEM, Yttr.id("not_gifts"));
		public static final TagKey<net.minecraft.item.Item> MAGNETIC = TagKey.of(RegistryKeys.ITEM, Yttr.id("magnetic"));
		public static final TagKey<net.minecraft.item.Item> CONDUCTIVE_BOOTS = TagKey.of(RegistryKeys.ITEM, Yttr.id("conductive_boots"));
		public static final TagKey<net.minecraft.item.Item> DSU_512 = TagKey.of(RegistryKeys.ITEM, Yttr.id("dsu_512"));
		public static final TagKey<net.minecraft.item.Item> DSU_1024 = TagKey.of(RegistryKeys.ITEM, Yttr.id("dsu_1024"));
		public static final TagKey<net.minecraft.item.Item> DSU_2048 = TagKey.of(RegistryKeys.ITEM, Yttr.id("dsu_2048"));
		public static final TagKey<net.minecraft.item.Item> DSU_4096 = TagKey.of(RegistryKeys.ITEM, Yttr.id("dsu_4096"));
		public static final TagKey<net.minecraft.item.Item> DSU_HIGHSTACK = TagKey.of(RegistryKeys.ITEM, Yttr.id("dsu_highstack"));
		public static final TagKey<net.minecraft.item.Item> BLOQUES = TagKey.of(RegistryKeys.ITEM, Yttr.id("bloques"));
		
		private static void init() {}
		
	}
	
	public static final class Block {

		public static final TagKey<net.minecraft.block.Block> FIRE_MODE_INSTABREAK = TagKey.of(RegistryKeys.BLOCK, Yttr.id("fire_mode_instabreak"));
		public static final TagKey<net.minecraft.block.Block> SNAREABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("snareable"));
		public static final TagKey<net.minecraft.block.Block> UNSNAREABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("unsnareable"));
		public static final TagKey<net.minecraft.block.Block> UNCLEAVABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("uncleavable"));
		public static final TagKey<net.minecraft.block.Block> GIFTS = TagKey.of(RegistryKeys.BLOCK, Yttr.id("gifts"));
		public static final TagKey<net.minecraft.block.Block> MAGTUBE_TARGETS = TagKey.of(RegistryKeys.BLOCK, Yttr.id("magtube_targets"));
		public static final TagKey<net.minecraft.block.Block> RUINED_DEVICES = TagKey.of(RegistryKeys.BLOCK, Yttr.id("ruined_devices"));
		public static final TagKey<net.minecraft.block.Block> ORES = TagKey.of(RegistryKeys.BLOCK, Yttr.id("ores"));
		public static final TagKey<net.minecraft.block.Block> LESSER_ORES = TagKey.of(RegistryKeys.BLOCK, Yttr.id("lesser_ores"));
		public static final TagKey<net.minecraft.block.Block> CLAMBER_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Yttr.id("clamber_blocks"));
		public static final TagKey<net.minecraft.block.Block> MAGNETIC = TagKey.of(RegistryKeys.BLOCK, Yttr.id("magnetic"));
		public static final TagKey<net.minecraft.block.Block> SCORCHED_RETROGEN_IGNORABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("scorched_retrogen_ignorable"));
		public static final TagKey<net.minecraft.block.Block> SCREEPER_NEST_LENIENT = TagKey.of(RegistryKeys.BLOCK, Yttr.id("screeper_nest_lenient"));
		public static final TagKey<net.minecraft.block.Block> SCREEPER_NEST_ACCESSORY = TagKey.of(RegistryKeys.BLOCK, Yttr.id("screeper_nest_accessory"));
		public static final TagKey<net.minecraft.block.Block> VOID_GLASS = TagKey.of(RegistryKeys.BLOCK, Yttr.id("void_glass"));
		public static final TagKey<net.minecraft.block.Block> VOID_GLASS_PANES = TagKey.of(RegistryKeys.BLOCK, Yttr.id("void_glass_panes"));
		public static final TagKey<net.minecraft.block.Block> TRANSFUNGUS_SLIPPERY = TagKey.of(RegistryKeys.BLOCK, Yttr.id("transfungus_slippery"));
		public static final TagKey<net.minecraft.block.Block> TRANSFUNGUS_STICKY = TagKey.of(RegistryKeys.BLOCK, Yttr.id("transfungus_sticky"));
		public static final TagKey<net.minecraft.block.Block> VELRESIN_STABLE = TagKey.of(RegistryKeys.BLOCK, Yttr.id("velresin_stable"));
		public static final TagKey<net.minecraft.block.Block> CLEAVE_PASSTHRU = TagKey.of(RegistryKeys.BLOCK, Yttr.id("cleave_passthru"));
		
		private static void init() {}
		
	}
	
	public static final class Fluid {

		public static final TagKey<net.minecraft.fluid.Fluid> VOID = TagKey.of(RegistryKeys.FLUID, Yttr.id("void"));
		public static final TagKey<net.minecraft.fluid.Fluid> PURE_VOID = TagKey.of(RegistryKeys.FLUID, Yttr.id("pure_void"));
		
		private static void init() {}
		
	}
	
	public static final class Entity {

		public static final TagKey<EntityType<?>> UNSNAREABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("unsnareable"));
		public static final TagKey<EntityType<?>> SNAREABLE_NONLIVING = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("snareable_nonliving"));
		public static final TagKey<EntityType<?>> BOSSES = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("bosses"));
		public static final TagKey<EntityType<?>> MAGNETIC = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("magnetic"));
		public static final TagKey<EntityType<?>> SCREEPER_IMMUNE = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("screeper_immune"));
		public static final TagKey<EntityType<?>> BLOODLESS = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("bloodless"));
		public static final TagKey<EntityType<?>> DISJUNCTIBLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("disjunctible"));
		public static final TagKey<EntityType<?>> UNADJUSTABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, Yttr.id("unadjustable"));
		
		private static void init() {}
		
	}

	public static void init() {
		Item.init();
		Block.init();
		Fluid.init();
		Entity.init();
	}
	
}
