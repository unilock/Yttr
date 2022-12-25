package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public final class YTags {

	public static final class Item {

		public static final TagKey<net.minecraft.item.Item> UNSNAREABLE = TagKey.of(Registry.ITEM_KEY, Yttr.id("unsnareable"));
		public static final TagKey<net.minecraft.item.Item> VOID_IMMUNE = TagKey.of(Registry.ITEM_KEY, Yttr.id("void_immune"));
		public static final TagKey<net.minecraft.item.Item> FLUXES = TagKey.of(Registry.ITEM_KEY, Yttr.id("fluxes"));
		public static final TagKey<net.minecraft.item.Item> ULTRAPURE_CUBES = TagKey.of(Registry.ITEM_KEY, Yttr.id("ultrapure_cubes"));
		public static final TagKey<net.minecraft.item.Item> GIFTS = TagKey.of(Registry.ITEM_KEY, Yttr.id("gifts"));
		public static final TagKey<net.minecraft.item.Item> NOT_GIFTS = TagKey.of(Registry.ITEM_KEY, Yttr.id("not_gifts"));
		public static final TagKey<net.minecraft.item.Item> MAGNETIC = TagKey.of(Registry.ITEM_KEY, Yttr.id("magnetic"));
		public static final TagKey<net.minecraft.item.Item> CONDUCTIVE_BOOTS = TagKey.of(Registry.ITEM_KEY, Yttr.id("conductive_boots"));
		public static final TagKey<net.minecraft.item.Item> DSU_512 = TagKey.of(Registry.ITEM_KEY, Yttr.id("dsu_512"));
		public static final TagKey<net.minecraft.item.Item> DSU_1024 = TagKey.of(Registry.ITEM_KEY, Yttr.id("dsu_1024"));
		public static final TagKey<net.minecraft.item.Item> DSU_2048 = TagKey.of(Registry.ITEM_KEY, Yttr.id("dsu_2048"));
		public static final TagKey<net.minecraft.item.Item> DSU_4096 = TagKey.of(Registry.ITEM_KEY, Yttr.id("dsu_4096"));
		public static final TagKey<net.minecraft.item.Item> DSU_HIGHSTACK = TagKey.of(Registry.ITEM_KEY, Yttr.id("dsu_highstack"));
		public static final TagKey<net.minecraft.item.Item> BLOQUES = TagKey.of(Registry.ITEM_KEY, Yttr.id("bloques"));
		
		private static void init() {}
		
	}
	
	public static final class Block {

		public static final TagKey<net.minecraft.block.Block> FIRE_MODE_INSTABREAK = TagKey.of(Registry.BLOCK_KEY, Yttr.id("fire_mode_instabreak"));
		public static final TagKey<net.minecraft.block.Block> SNAREABLE = TagKey.of(Registry.BLOCK_KEY, Yttr.id("snareable"));
		public static final TagKey<net.minecraft.block.Block> UNSNAREABLE = TagKey.of(Registry.BLOCK_KEY, Yttr.id("unsnareable"));
		public static final TagKey<net.minecraft.block.Block> UNCLEAVABLE = TagKey.of(Registry.BLOCK_KEY, Yttr.id("uncleavable"));
		public static final TagKey<net.minecraft.block.Block> GIFTS = TagKey.of(Registry.BLOCK_KEY, Yttr.id("gifts"));
		public static final TagKey<net.minecraft.block.Block> MAGTUBE_TARGETS = TagKey.of(Registry.BLOCK_KEY, Yttr.id("magtube_targets"));
		public static final TagKey<net.minecraft.block.Block> RUINED_DEVICES = TagKey.of(Registry.BLOCK_KEY, Yttr.id("ruined_devices"));
		public static final TagKey<net.minecraft.block.Block> ORES = TagKey.of(Registry.BLOCK_KEY, Yttr.id("ores"));
		public static final TagKey<net.minecraft.block.Block> LESSER_ORES = TagKey.of(Registry.BLOCK_KEY, Yttr.id("lesser_ores"));
		public static final TagKey<net.minecraft.block.Block> CLAMBER_BLOCKS = TagKey.of(Registry.BLOCK_KEY, Yttr.id("clamber_blocks"));
		public static final TagKey<net.minecraft.block.Block> MAGNETIC = TagKey.of(Registry.BLOCK_KEY, Yttr.id("magnetic"));
		public static final TagKey<net.minecraft.block.Block> SCORCHED_RETROGEN_IGNORABLE = TagKey.of(Registry.BLOCK_KEY, Yttr.id("scorched_retrogen_ignorable"));
		public static final TagKey<net.minecraft.block.Block> SCREEPER_NEST_LENIENT = TagKey.of(Registry.BLOCK_KEY, Yttr.id("screeper_nest_lenient"));
		public static final TagKey<net.minecraft.block.Block> SCREEPER_NEST_ACCESSORY = TagKey.of(Registry.BLOCK_KEY, Yttr.id("screeper_nest_accessory"));
		public static final TagKey<net.minecraft.block.Block> VOID_GLASS = TagKey.of(Registry.BLOCK_KEY, Yttr.id("void_glass"));
		public static final TagKey<net.minecraft.block.Block> VOID_GLASS_PANES = TagKey.of(Registry.BLOCK_KEY, Yttr.id("void_glass_panes"));
		public static final TagKey<net.minecraft.block.Block> TRANSFUNGUS_SLIPPERY = TagKey.of(Registry.BLOCK_KEY, Yttr.id("transfungus_slippery"));
		public static final TagKey<net.minecraft.block.Block> TRANSFUNGUS_STICKY = TagKey.of(Registry.BLOCK_KEY, Yttr.id("transfungus_sticky"));
		public static final TagKey<net.minecraft.block.Block> VELRESIN_STABLE = TagKey.of(Registry.BLOCK_KEY, Yttr.id("velresin_stable"));
		
		private static void init() {}
		
	}
	
	public static final class Fluid {

		public static final TagKey<net.minecraft.fluid.Fluid> VOID = TagKey.of(Registry.FLUID_KEY, Yttr.id("void"));
		public static final TagKey<net.minecraft.fluid.Fluid> PURE_VOID = TagKey.of(Registry.FLUID_KEY, Yttr.id("pure_void"));
		
		private static void init() {}
		
	}
	
	public static final class Entity {

		public static final TagKey<EntityType<?>> UNSNAREABLE = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("unsnareable"));
		public static final TagKey<EntityType<?>> SNAREABLE_NONLIVING = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("snareable_nonliving"));
		public static final TagKey<EntityType<?>> BOSSES = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("bosses"));
		public static final TagKey<EntityType<?>> MAGNETIC = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("magnetic"));
		public static final TagKey<EntityType<?>> SCREEPER_IMMUNE = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("screeper_immune"));
		public static final TagKey<EntityType<?>> BLOODLESS = TagKey.of(Registry.ENTITY_TYPE_KEY, Yttr.id("bloodless"));
		
		private static void init() {}
		
	}

	public static void init() {
		Item.init();
		Block.init();
		Fluid.init();
		Entity.init();
	}
	
}
