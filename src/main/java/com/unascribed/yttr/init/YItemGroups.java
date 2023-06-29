package com.unascribed.yttr.init;

import com.unascribed.lib39.fractal.api.ItemSubGroup;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.BloqueBlockItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mixin.accessor.AccessorItem;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YItemGroups {

	public static final ItemGroup PARENT = FabricItemGroupBuilder.create(Yttr.id("parent"))
		.icon(() -> new ItemStack(YItems.LOGO))
		.build()
		.hideName();
	public static final ItemSubGroup RESOURCES = ItemSubGroup.create(PARENT, Yttr.id("resources"));
	public static final ItemSubGroup FILTERING = ItemSubGroup.create(PARENT, Yttr.id("filtering"));
	public static final ItemSubGroup DEVICES = ItemSubGroup.create(PARENT, Yttr.id("devices"));
	public static final ItemSubGroup MECHANISMS = ItemSubGroup.create(PARENT, Yttr.id("mechanisms"));
	public static final ItemSubGroup EQUIPMENT = ItemSubGroup.create(PARENT, Yttr.id("equipment"));
	public static final ItemSubGroup SNARE = ItemSubGroup.create(PARENT, Yttr.id("snare"));
	public static final ItemSubGroup LAMP = ItemSubGroup.create(PARENT, Yttr.id("lamp"));
	public static final ItemSubGroup DECORATION = ItemSubGroup.create(PARENT, Yttr.id("decoration"));
	public static final ItemSubGroup POTION = ItemSubGroup.create(PARENT, Yttr.id("potion"));
	public static final ItemSubGroup RUINED = ItemSubGroup.create(PARENT, Yttr.id("ruined"));
	public static final ItemSubGroup INRED = ItemSubGroup.create(PARENT, Yttr.id("inred"));
	public static final ItemSubGroup MISC = ItemSubGroup.create(PARENT, Yttr.id("misc"));

	public static void init() {
		Registries.ITEM.forEach(i -> {
			Identifier id = Registries.ITEM.getId(i);
			if (id != null && id.getNamespace().equals("yttr")) {
				ItemGroup group = MISC;
				if (i instanceof LampBlockItem) {
					group = LAMP;
				} else if (i instanceof BloqueBlockItem) {
					group = DECORATION;
				} else if (i instanceof PotionItem) {
					group = POTION;
				} else if (i instanceof BlockItem && ((BlockItem)i).getBlock().getLootTableId().equals(Yttr.id("blocks/ruined"))) {
					group = RUINED;
				} else if (id.getPath().startsWith("inred_")) {
					group = INRED;
				} else if (id.getPath().startsWith("wasteland_")) {
					group = RUINED;
				} else if (id.getPath().startsWith("ultrapure_")) {
					group = FILTERING;
				} else if (id.getPath().endsWith("project_table")) {
					group = DEVICES;
				}
				((AccessorItem)i).yttr$setGroup(group);
			}
		});
		assign(RESOURCES,
				YItems.RAW_GADOLINITE,
				YItems.RAW_GADOLINITE_BLOCK,
				YItems.GADOLINITE,
				YItems.DEEPSLATE_GADOLINITE,
				YItems.YTTRIUM_BLOCK,
				YItems.SQUEEZE_LEAVES,
				YItems.SQUEEZE_LOG,
				YItems.SQUEEZE_SAPLING,
				YItems.STRIPPED_SQUEEZE_LOG,
				YItems.BROOKITE,
				YItems.DEEPSLATE_BROOKITE_ORE,
				YItems.BROOKITE_ORE,
				YItems.BROOKITE_BLOCK,
				YItems.YTTRIUM_INGOT,
				YItems.YTTRIUM_NUGGET,
				YItems.IRON_DUST,
				YItems.NEODYMIUM_DUST,
				YItems.YTTRIUM_DUST,
				YItems.XL_IRON_INGOT,
				YItems.DELICACE,
				YItems.GLOWING_GAS,
				YItems.NEODYMIUM_DISC,
				YItems.QUICKSILVER,
				YItems.BEDROCK_SHARD,
				YItems.GLASSY_VOID,
				YItems.VOID_BUCKET,
				YItems.ARMOR_PLATING,
				YItems.TABLE,
				YItems.ROOT_OF_CONTINUITY,
				YItems.DROP_OF_CONTINUITY,
				YItems.NETHERTUFF,
				YItems.GLASSY_VOID_PANE,
				YItems.PROMETHIUM_SPECK,
				YItems.PROMETHIUM_LUMP,
				YItems.PROMETHIUM_GLOB,
				YItems.CUPROSTEEL_INGOT,
				YItems.CUPROSTEEL_BLOCK,
				YItems.SOUL_PLANKS,
				YItems.NEODYMIUM_BLOCK,
				YItems.NEODYMIUM_SLAB,
				YItems.DELRENE,
				YItems.DELRENE_SCRAP,
				YItems.ASH,
				YItems.HAEMOPAL,
				YItems.DUST
			);
		assign(FILTERING,
				YItems.COMPRESSED_ULTRAPURE_CARBON,
				YItems.COMPRESSED_ULTRAPURE_CARBON_BLOCK,
				YItems.ENCASED_VOID_FILTER,
				YItems.VOID_FILTER,
				YItems.MAGTANK,
				YItems.MAGTUBE,
				YItems.DSU,
				YItems.MAGCAPSULE
			);
		assign(DEVICES,
				YItems.CENTRIFUGE,
				YItems.DIVING_PLATE,
				YItems.SUIT_STATION,
				YItems.RAFTER,
				YItems.CAN_FILLER
			);
		assign(MECHANISMS,
				YItems.POWER_METER,
				YItems.CHUTE,
				YItems.HEAVY_YTTRIUM_PLATE,
				YItems.LIGHT_YTTRIUM_PLATE,
				YItems.DOPPER,
				YItems.FLOPPER,
				YItems.YTTRIUM_BUTTON,
				YItems.AWARE_HOPPER,
				YItems.LEVITATION_CHAMBER,
				YItems.SKELETAL_SORTER_LEFT_HANDED,
				YItems.SKELETAL_SORTER_RIGHT_HANDED,
				YItems.REPLICATOR,
				YItems.HIGH_NOTE_BLOCK,
				YItems.LOW_NOTE_BLOCK,
				YItems.BOGGED_NOTE_BLOCK,
				YItems.BOGGED_HIGH_NOTE_BLOCK,
				YItems.BOGGED_LOW_NOTE_BLOCK,
				YItems.CUPROSTEEL_PLATE,
				YItems.CLAMBER_BLOCK,
				YItems.SOUL_CLAMBER_BLOCK,
				YItems.SCREEPER_NEST,
				YItems.VELRESIN,
				YItems.SSD,
				YItems.TRANSFUNGUS
			);
		assign(EQUIPMENT,
				YItems.BROOKITE_SWORD,
				YItems.BROOKITE_SHOVEL,
				YItems.BROOKITE_PICKAXE,
				YItems.BROOKITE_AXE,
				YItems.BROOKITE_HOE,
				YItems.EFFECTOR,
				YItems.SHIFTER,
				YItems.BEDROCK_SMASHER,
				YItems.RIFLE,
				YItems.RIFLE_OVERCLOCKED,
				YItems.RIFLE_REINFORCED,
				YItems.SPECTRAL_AXE,
				YItems.SNARE,
				YItems.SHEARS,
				YItems.CLEAVER,
				YItems.REINFORCED_CLEAVER,
				YItems.SUIT_HELMET,
				YItems.SUIT_CHESTPLATE,
				YItems.SUIT_LEGGINGS,
				YItems.SUIT_BOOTS,
				YItems.GOGGLES,
				YItems.PROJECTOR,
				YItems.AMMO_CAN,
				YItems.SPATULA,
				YItems.EMPTY_AMMO_CAN,
				YItems.AMMO_PACK.orElse(null),
				YItems.CUPROSTEEL_COIL.orElse(null)
			);
		assign(RUINED,
				YItems.RUINED_CONTAINER,
				YItems.RUBBLE
			);
		assign(DECORATION,
				YItems.YTTRIUM_PLATING,
				YItems.SCORCHED_OBSIDIAN,
				YItems.POLISHED_SCORCHED_OBSIDIAN,
				YItems.POLISHED_SCORCHED_OBSIDIAN_CAPSTONE,
				YItems.POLISHED_OBSIDIAN,
				YItems.POLISHED_OBSIDIAN_CAPSTONE,
				YItems.CONTINUOUS_PLATFORM,
				YItems.SCORCHED_CRYING_OBSIDIAN,
				YItems.GIANT_COBBLESTONE,
				YItems.BLACK_VOID_GLASS,
				YItems.GRAY_VOID_GLASS,
				YItems.SILVER_VOID_GLASS,
				YItems.WHITE_VOID_GLASS,
				YItems.CLEAR_VOID_GLASS,
				YItems.BLACK_VOID_GLASS_PANE,
				YItems.GRAY_VOID_GLASS_PANE,
				YItems.SILVER_VOID_GLASS_PANE,
				YItems.WHITE_VOID_GLASS_PANE,
				YItems.CLEAR_VOID_GLASS_PANE
			);
	}

	private static void assign(ItemGroup group, Item... items) {
		for (Item i : items) {
			if (i == null) continue;
			((AccessorItem)i).yttr$setGroup(group);
		}
	}

}
