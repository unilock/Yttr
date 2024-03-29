package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

import com.unascribed.lib39.core.api.util.LatchReference;
import com.unascribed.lib39.weld.api.BigBlockItem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.AmmoCanItem;
import com.unascribed.yttr.content.item.BlueCubeItem;
import com.unascribed.yttr.content.item.CleaverItem;
import com.unascribed.yttr.content.item.CreaseItem;
import com.unascribed.yttr.content.item.DropOfContinuityItem;
import com.unascribed.yttr.content.item.EffectorItem;
import com.unascribed.yttr.content.item.GlowingGasItem;
import com.unascribed.yttr.content.item.HaemopalItem;
import com.unascribed.yttr.content.item.HornItem;
import com.unascribed.yttr.content.item.InRedMultimeterItem;
import com.unascribed.yttr.content.item.ProjectorItem;
import com.unascribed.yttr.content.item.ReinforcedCleaverItem;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.content.item.ShearsItem;
import com.unascribed.yttr.content.item.ShifterItem;
import com.unascribed.yttr.content.item.SnareItem;
import com.unascribed.yttr.content.item.SpatulaItem;
import com.unascribed.yttr.content.item.SpectralAxeItem;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.content.item.SwallowableItem;
import com.unascribed.yttr.content.item.VoidBucketItem;
import com.unascribed.yttr.content.item.block.BloqueBlockItem;
import com.unascribed.yttr.content.item.block.DyedBlockItem;
import com.unascribed.yttr.content.item.block.InRedCableBlockItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.content.item.block.LevitationChamberBlockItem;
import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.content.item.block.SkeletalSorterBlockItem;
import com.unascribed.yttr.content.item.block.SpecialBlockItem;
import com.unascribed.yttr.content.item.potion.MercurialPotionItem;
import com.unascribed.yttr.content.item.potion.MercurialSplashPotionItem;
import com.unascribed.yttr.util.annotate.ConstantColor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.item.ArmorItem.ArmorSlot;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class YItems {

	public static final BlockItem GADOLINITE = createBlockItem(YBlocks.GADOLINITE);
	public static final BlockItem YTTRIUM_BLOCK = createBlockItem(YBlocks.YTTRIUM_BLOCK);
	public static final BlockItem POWER_METER = createBlockItem(YBlocks.POWER_METER);
	public static final BlockItem CHUTE = createBlockItem(YBlocks.CHUTE);
	public static final BlockItem BEDROCK_SMASHER = createBlockItem(YBlocks.BEDROCK_SMASHER);
	public static final BlockItem GLASSY_VOID = createBlockItem(YBlocks.GLASSY_VOID);
	public static final BlockItem GLASSY_VOID_PANE = createBlockItem(YBlocks.GLASSY_VOID_PANE);
	public static final BlockItem SQUEEZE_LOG = createBlockItem(YBlocks.SQUEEZE_LOG);
	public static final BlockItem STRIPPED_SQUEEZE_LOG = createBlockItem(YBlocks.STRIPPED_SQUEEZE_LOG);
	@ConstantColor(0xFFEE58)
	public static final BlockItem SQUEEZE_LEAVES = createBlockItem(YBlocks.SQUEEZE_LEAVES);
	public static final BlockItem SQUEEZE_SAPLING = createBlockItem(YBlocks.SQUEEZE_SAPLING);
	public static final BlockItem YTTRIUM_PLATING = createBlockItem(YBlocks.YTTRIUM_PLATING);
	public static final BlockItem LIGHT_YTTRIUM_PLATE = createBlockItem(YBlocks.LIGHT_YTTRIUM_PLATE);
	public static final BlockItem HEAVY_YTTRIUM_PLATE = createBlockItem(YBlocks.HEAVY_YTTRIUM_PLATE);
	public static final BlockItem CENTRIFUGE = createBlockItem(YBlocks.CENTRIFUGE);
	public static final BlockItem DOPPER = createBlockItem(YBlocks.DOPPER);
	public static final BlockItem FLOPPER = new HornItem(YBlocks.FLOPPER, new Item.Settings());
	public static final BlockItem DIVING_PLATE = createBlockItem(YBlocks.DIVING_PLATE);
	public static final BlockItem SUIT_STATION = createBlockItem(YBlocks.SUIT_STATION);
	public static final BlockItem TABLE = createBlockItem(YBlocks.TABLE);
	public static final BlockItem ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.ULTRAPURE_CARBON_BLOCK, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final BlockItem COMPRESSED_ULTRAPURE_CARBON_BLOCK = new BlockItem(YBlocks.COMPRESSED_ULTRAPURE_CARBON_BLOCK, new Item.Settings().rarity(Rarity.UNCOMMON));
	public static final BlockItem ENCASED_VOID_FILTER = createBlockItem(YBlocks.ENCASED_VOID_FILTER);
	public static final BlockItem VOID_FILTER = createBlockItem(YBlocks.VOID_FILTER);
	public static final BlockItem BROOKITE_ORE = createBlockItem(YBlocks.BROOKITE_ORE);
	public static final BlockItem ROOT_OF_CONTINUITY = createBlockItem(YBlocks.ROOT_OF_CONTINUITY);
	public static final BlockItem YTTRIUM_BUTTON = createBlockItem(YBlocks.YTTRIUM_BUTTON);
	public static final BlockItem BROOKITE_BLOCK = createBlockItem(YBlocks.BROOKITE_BLOCK);
	public static final BlockItem NETHERTUFF = createBlockItem(YBlocks.NETHERTUFF);
	public static final BlockItem MAGTUBE = createBlockItem(YBlocks.MAGTUBE);
	public static final BlockItem HIGH_NOTE_BLOCK = createBlockItem(YBlocks.HIGH_NOTE_BLOCK);
	public static final BlockItem LOW_NOTE_BLOCK = createBlockItem(YBlocks.LOW_NOTE_BLOCK);
	public static final BlockItem BOGGED_NOTE_BLOCK = createBlockItem(YBlocks.BOGGED_NOTE_BLOCK);
	public static final BlockItem BOGGED_HIGH_NOTE_BLOCK = createBlockItem(YBlocks.BOGGED_HIGH_NOTE_BLOCK);
	public static final BlockItem BOGGED_LOW_NOTE_BLOCK = createBlockItem(YBlocks.BOGGED_LOW_NOTE_BLOCK);
	@ConstantColor(0xCB8FC3)
	public static final BlockItem CONTINUOUS_PLATFORM = createBlockItem(YBlocks.CONTINUOUS_PLATFORM);
	public static final BlockItem CLAMBER_BLOCK = createBlockItem(YBlocks.CLAMBER_BLOCK);
	public static final BlockItem SOUL_CLAMBER_BLOCK = createBlockItem(YBlocks.SOUL_CLAMBER_BLOCK);
	public static final BlockItem SOUL_PLANKS = createBlockItem(YBlocks.SOUL_PLANKS);
	public static final BlockItem CUPROSTEEL_BLOCK = createBlockItem(YBlocks.CUPROSTEEL_BLOCK);
	public static final BlockItem CUPROSTEEL_PLATE = createBlockItem(YBlocks.CUPROSTEEL_PLATE);
	public static final BlockItem CAN_FILLER = createBlockItem(YBlocks.CAN_FILLER);
	public static final BlockItem DUST = createBlockItem(YBlocks.DUST);
	public static final BlockItem RAFTER = createBlockItem(YBlocks.RAFTER);
	public static final BlockItem PROJECT_TABLE = createBlockItem(YBlocks.PROJECT_TABLE);
	public static final BlockItem RAW_GADOLINITE_BLOCK = createBlockItem(YBlocks.RAW_GADOLINITE_BLOCK);
	public static final BlockItem DEEPSLATE_GADOLINITE = createBlockItem(YBlocks.DEEPSLATE_GADOLINITE);
	public static final BlockItem DEEPSLATE_BROOKITE_ORE = createBlockItem(YBlocks.DEEPSLATE_BROOKITE_ORE);
	public static final BlockItem ASH = createBlockItem(YBlocks.ASH);
	public static final BlockItem DELRENE = createBlockItem(YBlocks.DELRENE);
	public static final BlockItem SCORCHED_OBSIDIAN = createBlockItem(YBlocks.SCORCHED_OBSIDIAN);
	public static final BlockItem POLISHED_SCORCHED_OBSIDIAN = createBlockItem(YBlocks.POLISHED_SCORCHED_OBSIDIAN);
	public static final BlockItem POLISHED_SCORCHED_OBSIDIAN_CAPSTONE = createBlockItem(YBlocks.POLISHED_SCORCHED_OBSIDIAN_CAPSTONE);
	public static final BlockItem POLISHED_OBSIDIAN = createBlockItem(YBlocks.POLISHED_OBSIDIAN);
	public static final BlockItem POLISHED_OBSIDIAN_CAPSTONE = createBlockItem(YBlocks.POLISHED_OBSIDIAN_CAPSTONE);
	public static final BlockItem SCORCHED_CRYING_OBSIDIAN = createBlockItem(YBlocks.SCORCHED_CRYING_OBSIDIAN);
	public static final BlockItem SCREEPER_NEST = createBlockItem(YBlocks.SCREEPER_NEST);
	public static final BlockItem SSD = createBlockItem(YBlocks.SSD);
	public static final BlockItem VELRESIN = createBlockItem(YBlocks.VELRESIN);
	public static final BlockItem TRANSFUNGUS = createBlockItem(YBlocks.TRANSFUNGUS);
	public static final BlockItem TINT = createBlockItem(YBlocks.TINT);
	
	public static final BlockItem BLACK_VOID_GLASS = createBlockItem(YBlocks.BLACK_VOID_GLASS);
	public static final BlockItem GRAY_VOID_GLASS = createBlockItem(YBlocks.GRAY_VOID_GLASS);
	public static final BlockItem SILVER_VOID_GLASS = createBlockItem(YBlocks.SILVER_VOID_GLASS);
	public static final BlockItem WHITE_VOID_GLASS = createBlockItem(YBlocks.WHITE_VOID_GLASS);
	public static final BlockItem CLEAR_VOID_GLASS = createBlockItem(YBlocks.CLEAR_VOID_GLASS);
	
	public static final BlockItem BLACK_VOID_GLASS_PANE = createBlockItem(YBlocks.BLACK_VOID_GLASS_PANE);
	public static final BlockItem GRAY_VOID_GLASS_PANE = createBlockItem(YBlocks.GRAY_VOID_GLASS_PANE);
	public static final BlockItem SILVER_VOID_GLASS_PANE = createBlockItem(YBlocks.SILVER_VOID_GLASS_PANE);
	public static final BlockItem WHITE_VOID_GLASS_PANE = createBlockItem(YBlocks.WHITE_VOID_GLASS_PANE);
	public static final BlockItem CLEAR_VOID_GLASS_PANE = createBlockItem(YBlocks.CLEAR_VOID_GLASS_PANE);

	public static final BlockItem WHITE_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.WHITE);
	public static final BlockItem ORANGE_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.ORANGE);
	public static final BlockItem MAGENTA_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.MAGENTA);
	public static final BlockItem LIGHT_BLUE_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.LIGHT_BLUE);
	public static final BlockItem YELLOW_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.YELLOW);
	public static final BlockItem LIME_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.LIME);
	public static final BlockItem PINK_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.PINK);
	public static final BlockItem GRAY_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.GRAY);
	public static final BlockItem LIGHT_GRAY_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.LIGHT_GRAY);
	public static final BlockItem CYAN_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.CYAN);
	public static final BlockItem PURPLE_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.PURPLE);
	public static final BlockItem BLUE_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.BLUE);
	public static final BlockItem BROWN_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.BROWN);
	public static final BlockItem GREEN_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.GREEN);
	public static final BlockItem RED_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.RED);
	public static final BlockItem BLACK_BLOQUE = createBloqueBlockItem(YBlocks.BLOQUE, DyeColor.BLACK);
	
	public static final BlockItem WHITE_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.WHITE);
	public static final BlockItem ORANGE_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.ORANGE);
	public static final BlockItem MAGENTA_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.MAGENTA);
	public static final BlockItem LIGHT_BLUE_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.LIGHT_BLUE);
	public static final BlockItem YELLOW_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.YELLOW);
	public static final BlockItem LIME_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.LIME);
	public static final BlockItem PINK_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.PINK);
	public static final BlockItem GRAY_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.GRAY);
	public static final BlockItem LIGHT_GRAY_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.LIGHT_GRAY);
	public static final BlockItem CYAN_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.CYAN);
	public static final BlockItem PURPLE_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.PURPLE);
	public static final BlockItem BLUE_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.BLUE);
	public static final BlockItem BROWN_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.BROWN);
	public static final BlockItem GREEN_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.GREEN);
	public static final BlockItem RED_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.RED);
	public static final BlockItem BLACK_PROJECT_TABLE = createDyedBlockItem(YBlocks.DYED_PROJECT_TABLE, DyeColor.BLACK);
	
	public static final BlockItem NEODYMIUM_SLAB = new SpecialBlockItem(YBlocks.NEODYMIUM_SLAB, new Item.Settings()) {
		@Override
		public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
			stacks.add(new ItemStack(this));
		}
	};
	public static final BlockItem NEODYMIUM_BLOCK = new SpecialBlockItem(YBlocks.NEODYMIUM_SLAB, new Item.Settings()) {
		@Override
		public String getTranslationKey() {
			return "block.yttr.neodymium_block";
		}
		@Override
		public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
			stacks.add(new ItemStack(this));
		}
		@Override
		public void appendBlocks(Map<Block,Item> map, Item item) {};
	};

	public static final BlockItem INRED_BLOCK = createBlockItem(YBlocks.INRED_BLOCK);
	public static final BlockItem INRED_CABLE = new InRedCableBlockItem(YBlocks.INRED_CABLE, new Item.Settings());
	public static final BlockItem INRED_AND_GATE = createBlockItem(YBlocks.INRED_AND_GATE);
	public static final BlockItem INRED_NOT_GATE = createBlockItem(YBlocks.INRED_NOT_GATE);
	public static final BlockItem INRED_XOR_GATE = createBlockItem(YBlocks.INRED_XOR_GATE);
	public static final BlockItem INRED_DIODE = createBlockItem(YBlocks.INRED_DIODE);
	public static final BlockItem INRED_SHIFTER = createBlockItem(YBlocks.INRED_SHIFTER);
	public static final BlockItem INRED_TRANSISTOR = createBlockItem(YBlocks.INRED_TRANSISTOR);
	public static final BlockItem INRED_ENCODER = createBlockItem(YBlocks.INRED_ENCODER);
	public static final BlockItem INRED_OSCILLATOR = createBlockItem(YBlocks.INRED_OSCILLATOR);
	public static final BlockItem INRED_DEMO_CYCLER = createBlockItem(YBlocks.INRED_DEMO_CYCLER);
	
	public static final BlockItem WASTELAND_DIRT = createBlockItem(YBlocks.WASTELAND_DIRT);
	public static final BlockItem WASTELAND_GRASS = createBlockItem(YBlocks.WASTELAND_GRASS);
	public static final BlockItem WASTELAND_LOG = createBlockItem(YBlocks.WASTELAND_LOG);
	public static final BlockItem WASTELAND_STONE = createBlockItem(YBlocks.WASTELAND_STONE);
	
	public static final BlockItem RUINED_COBBLESTONE = createBlockItem(YBlocks.RUINED_COBBLESTONE);
	public static final BlockItem RUINED_BRICKS = createBlockItem(YBlocks.RUINED_BRICKS);
	public static final BlockItem RUINED_CONTAINER = createBlockItem(YBlocks.RUINED_CONTAINER);
	public static final BlockItem RUINED_DEVICE_BC_1 = createBlockItem(YBlocks.RUINED_DEVICE_BC_1);
	public static final BlockItem RUINED_DEVICE_BC_2 = createBlockItem(YBlocks.RUINED_DEVICE_BC_2);
	public static final BlockItem RUINED_DEVICE_GT_1 = createBlockItem(YBlocks.RUINED_DEVICE_GT_1);
	public static final BlockItem RUINED_DEVICE_RP_1 = createBlockItem(YBlocks.RUINED_DEVICE_RP_1);
	public static final BlockItem RUINED_DEVICE_FO_1 = createBlockItem(YBlocks.RUINED_DEVICE_FO_1);
	public static final BlockItem RUINED_PIPE = createBlockItem(YBlocks.RUINED_PIPE);
	public static final BlockItem RUINED_FRAME = createBlockItem(YBlocks.RUINED_FRAME);
	public static final BlockItem RUINED_TUBE = createBlockItem(YBlocks.RUINED_TUBE);
	public static final BlockItem RUINED_LEVER = createBlockItem(YBlocks.RUINED_LEVER);
	public static final BlockItem RUINED_TANK = createBlockItem(YBlocks.RUINED_TANK);
	public static final BlockItem RUINED_CONSTRUCT_RC_1 = createBlockItem(YBlocks.RUINED_CONSTRUCT_RC_1);
	public static final BlockItem RUINED_CONSTRUCT_RC_2 = createBlockItem(YBlocks.RUINED_CONSTRUCT_RC_2);
	
	public static final BlockItem RUINED_TORCH = new WallStandingBlockItem(YBlocks.RUINED_TORCH, YBlocks.RUINED_WALL_TORCH, new Item.Settings(), Direction.DOWN);
	
	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem LAMP = new LampBlockItem(YBlocks.LAMP, new Item.Settings());

	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem FIXTURE = new LampBlockItem(YBlocks.FIXTURE, new Item.Settings());

	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem CAGE_LAMP = new LampBlockItem(YBlocks.CAGE_LAMP, new Item.Settings());

	@BuiltinRenderer("LampItemRenderer")
	public static final BlockItem PANEL = new LampBlockItem(YBlocks.PANEL, new Item.Settings());
	
	public static final BlockItem LAZOR_EMITTER = new LampBlockItem(YBlocks.LAZOR_EMITTER, new Item.Settings());
	public static final BlockItem IR_LAZOR_EMITTER = new LampBlockItem(YBlocks.IR_LAZOR_EMITTER, new Item.Settings());
	
	public static final BlockItem AWARE_HOPPER = new BlockItem(YBlocks.AWARE_HOPPER, new Item.Settings()
			.maxCount(1));

	public static final BlockItem LEVITATION_CHAMBER = new LevitationChamberBlockItem(YBlocks.LEVITATION_CHAMBER, new Item.Settings());
	
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_RIGHT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.RIGHT, new Item.Settings());
	public static final SkeletalSorterBlockItem SKELETAL_SORTER_LEFT_HANDED = new SkeletalSorterBlockItem(YBlocks.SKELETAL_SORTER, Arm.LEFT, new Item.Settings());
	
	@BuiltinRenderer("ReplicatorItemRenderer")
	public static final ReplicatorBlockItem REPLICATOR = new ReplicatorBlockItem(YBlocks.REPLICATOR, new Item.Settings());
	
	public static final BigBlockItem MAGTANK = new BigBlockItem(YBlocks.MAGTANK, new Item.Settings()
			.maxCount(4));
	
	public static final BigBlockItem GIANT_COBBLESTONE = new BigBlockItem(YBlocks.GIANT_COBBLESTONE, new Item.Settings()
			.maxCount(1));
	
	public static final BigBlockItem DSU = new BigBlockItem(YBlocks.DSU, new Item.Settings()
			.maxCount(8));

	private static BlockItem createBlockItem(Block block) {
		return new BlockItem(block, new Item.Settings());
	}

	private static BlockItem createBloqueBlockItem(Block block, DyeColor color) {
		return new BloqueBlockItem(block, color, new Item.Settings());
	}

	private static BlockItem createDyedBlockItem(Block block, DyeColor color) {
		return new DyedBlockItem(block, color, new Item.Settings());
	}
	
	public static final Item YTTRIUM_INGOT = new Item(new Item.Settings());
	
	public static final Item YTTRIUM_NUGGET = new Item(new Item.Settings());
	
	public static final Item XL_IRON_INGOT = new Item(new Item.Settings()
			.maxCount(16));
	
	public static final VoidBucketItem VOID_BUCKET = new VoidBucketItem(new Item.Settings()
			.recipeRemainder(Items.BUCKET)
			.maxCount(1));
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE = new RifleItem(new Item.Settings()
			.maxCount(1), 1, 1, false, 0x3E5656);
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE_REINFORCED = new RifleItem(new Item.Settings()
			.maxCount(1), 0.85f, 1, true, 0x223333);
	
	@BuiltinRenderer("RifleItemRenderer")
	public static final RifleItem RIFLE_OVERCLOCKED = new RifleItem(new Item.Settings()
			.maxCount(1), 1.65f, 2, false, 0x111111);
	
	public static final SnareItem SNARE = new SnareItem(new Item.Settings()
			.maxCount(1));
	
	public static final ShearsItem SHEARS = new ShearsItem(new Item.Settings()
			.maxDamage(512));
	
	public static final Item BEDROCK_SHARD = new Item(new Item.Settings());
	
	public static final Item DELICACE = new SwallowableItem(new Item.Settings()
			.food(new FoodComponent.Builder()
					.alwaysEdible()
					.hunger(1)
					.statusEffect(new StatusEffectInstance(YStatusEffects.DELICACENESS, 30*20, 3), 1)
					.snack()
					.build())
			) {
		@Override
		public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
			super.appendTooltip(stack, world, tooltip, context);
			tooltip.add(Text.translatable("potion.withDuration",
					Text.translatable("potion.withAmplifier",
							Text.translatable("effect.yttr.delicaceness"),
							Text.translatable("potion.potency.3")),
					"0:30").formatted(Formatting.BLUE));
			tooltip.add(Text.literal(""));
			tooltip.add(Text.translatable("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
			tooltip.add(Text.translatable("tip.yttr.delicace_bonus_1").formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("tip.yttr.delicace_bonus_2").formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("tip.yttr.delicace_bonus_3").formatted(Formatting.BLUE));
			tooltip.add(Text.translatable("tip.yttr.delicace_bonus_4").formatted(Formatting.BLUE));
		}
	};
	
	public static final Item GLOWING_GAS = new GlowingGasItem(new Item.Settings()
			.recipeRemainder(Items.GLASS_BOTTLE));
	
	public static final Item LOGO = new Item(new Item.Settings());
	
	public static final Item CLEAVER = new CleaverItem(new Item.Settings()
			.maxDamage(1562));
	
	public static final Item REINFORCED_CLEAVER = new ReinforcedCleaverItem(new Item.Settings()
			.maxDamage(3072)
			.fireproof());
	
	public static final EffectorItem EFFECTOR = new EffectorItem(new Item.Settings()
			.maxCount(1));
	
	public static final Item NEODYMIUM_DUST = new Item(new Item.Settings());
	public static final MusicDiscItem NEODYMIUM_DISC = new MusicDiscItem(15, YSounds.BUZZ, new Item.Settings(), 11) {
		@Override
		@Environment(EnvType.CLIENT)
		public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {}
	};
	
	private static final ArmorMaterial SUIT_MATERIAL = new ArmorMaterial() {

		@Override
		public int getDurability(ArmorItem.ArmorSlot slot) {
			return ArmorMaterials.DIAMOND.getDurability(slot);
		}

		@Override
		public int getProtection(ArmorItem.ArmorSlot slot) {
			return ArmorMaterials.DIAMOND.getProtection(slot)+2;
		}

		@Override
		public int getEnchantability() {
			return 5;
		}

		@Override
		public SoundEvent getEquipSound() {
			return YSounds.EQUIP_SUIT;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.YTTRIUM_BLOCK);
		}

		@Override
		public String getName() {
			return "yttr_suit";
		}

		@Override
		public float getToughness() {
			return 6;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.5f;
		}
		
	};
	
	public static final SuitArmorItem SUIT_HELMET = new SuitArmorItem(SUIT_MATERIAL, ArmorSlot.HELMET, new Item.Settings()
			.fireproof());
	
	public static final SuitArmorItem SUIT_CHESTPLATE = new SuitArmorItem(SUIT_MATERIAL, ArmorSlot.CHESTPLATE, new Item.Settings()
			.fireproof());
	
	public static final SuitArmorItem SUIT_LEGGINGS = new SuitArmorItem(SUIT_MATERIAL, ArmorSlot.LEGGINGS, new Item.Settings()
			.fireproof());
	
	public static final SuitArmorItem SUIT_BOOTS = new SuitArmorItem(SUIT_MATERIAL, ArmorSlot.BOOTS, new Item.Settings()
			.fireproof());
	
	public static final Item ARMOR_PLATING = new Item(new Item.Settings());
	
	public static final ArmorItem GOGGLES = new ArmorItem(new ArmorMaterial() {
		
		@Override
		public float getToughness() {
			return 0;
		}
		
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.YTTRIUM_NUGGET);
		}
		
		@Override
		public int getProtection(ArmorItem.ArmorSlot slot) {
			return 0;
		}
		
		@Override
		public String getName() {
			return "yttr_goggles";
		}
		
		@Override
		public float getKnockbackResistance() {
			return 0;
		}
		
		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_IRON;
		}
		
		@Override
		public int getEnchantability() {
			return 0;
		}
		
		@Override
		public int getDurability(ArmorItem.ArmorSlot slot) {
			return 32;
		}
	}, ArmorItem.ArmorSlot.HELMET, new Item.Settings()) {


		@Override
		public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
			return ImmutableMultimap.of();
		}
		
	};
	
	public static final SpectralAxeItem SPECTRAL_AXE = new SpectralAxeItem();
	
	private static final Item.Settings UP_SETTINGS = new Item.Settings()
			.rarity(Rarity.UNCOMMON);
	
	public static final Item ULTRAPURE_CARBON = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_HYDROGEN = new Item(UP_SETTINGS);
//	public static final Item ULTRAPURE_ICED_COFFEES = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_CINNABAR = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_GOLD = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_IRON = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_LAZURITE = new BlueCubeItem(UP_SETTINGS);
	public static final Item ULTRAPURE_SILICA = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_YTTRIUM = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_NEODYMIUM = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_COPPER = new Item(UP_SETTINGS);
	
	public static final Item ULTRAPURE_DIAMOND = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_WOLFRAM = new Item(UP_SETTINGS);
	public static final Item ULTRAPURE_NETHERITE = new Item(UP_SETTINGS);
	
	public static final Item QUICKSILVER = new Item(UP_SETTINGS);
	
	public static final Item COMPRESSED_ULTRAPURE_CARBON = new Item(UP_SETTINGS);
	
	public static final MercurialPotionItem MERCURIAL_POTION = new MercurialPotionItem(new Item.Settings()
			.maxCount(Items.POTION.getMaxCount()));
	public static final MercurialSplashPotionItem MERCURIAL_SPLASH_POTION = new MercurialSplashPotionItem(new Item.Settings()
			.maxCount(Items.SPLASH_POTION.getMaxCount()));

	public static final Item YTTRIUM_DUST = new Item(new Item.Settings());
	public static final Item IRON_DUST = new Item(new Item.Settings());
	
	public static final Item BROOKITE = new Item(new Item.Settings());
	
	public static final ToolMaterial BROOKITE_MATERIAL = new ToolMaterial() {
		
		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(YItems.BROOKITE);
		}
		
		@Override
		public float getMiningSpeedMultiplier() {
			return ToolMaterials.IRON.getMiningSpeedMultiplier()*1.15f;
		}
		
		@Override
		public int getMiningLevel() {
			return ToolMaterials.IRON.getMiningLevel();
		}
		
		@Override
		public int getEnchantability() {
			return ToolMaterials.IRON.getEnchantability();
		}
		
		@Override
		public int getDurability() {
			return ToolMaterials.IRON.getDurability()*7/4;
		}
		
		@Override
		public float getAttackDamage() {
			return ToolMaterials.IRON.getAttackDamage();
		}
	};
	
	public static final SwordItem BROOKITE_SWORD = new SwordItem(BROOKITE_MATERIAL, 3, -2.4f, new Item.Settings()) {};
	public static final ShovelItem BROOKITE_SHOVEL = new ShovelItem(BROOKITE_MATERIAL, 1.5f, -3.0f, new Item.Settings()) {};
	public static final PickaxeItem BROOKITE_PICKAXE = new PickaxeItem(BROOKITE_MATERIAL, 1, -2.8f, new Item.Settings()) {};
	public static final AxeItem BROOKITE_AXE = new AxeItem(BROOKITE_MATERIAL, 6, -3.1f, new Item.Settings()) {};
	public static final HoeItem BROOKITE_HOE = new HoeItem(BROOKITE_MATERIAL, -2, -1, new Item.Settings()) {};
	
	public static final DropOfContinuityItem DROP_OF_CONTINUITY = new DropOfContinuityItem(new Item.Settings().maxCount(1));
	public static final DropOfContinuityItem LOOTBOX_OF_CONTINUITY = new DropOfContinuityItem(new Item.Settings().maxCount(1));
	
	@ColorProvider("ContinuityItemColorProvider")
	public static final ShifterItem SHIFTER = new ShifterItem(new Item.Settings()
			.maxCount(1));
	@ColorProvider("ContinuityItemColorProvider")
	public static final ProjectorItem PROJECTOR = new ProjectorItem(new Item.Settings()
			.maxCount(1));
	
	public static final MusicDiscItem MUSIC_DISC_PAPILLONS = new MusicDiscItem(14, YSounds.PAPILLONS, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 219) {};
	public static final MusicDiscItem MUSIC_DISC_VOID = new MusicDiscItem(14, YSounds.VOID_MUSIC, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 615) {};
	public static final MusicDiscItem MUSIC_DISC_DESERT_HEAT = new MusicDiscItem(14, YSounds.DESERT_HEAT_MONO, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 421) {};
	public static final MusicDiscItem MUSIC_DISC_TORUS = new MusicDiscItem(14, YSounds.TORUS_MONO, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 396) {};
	public static final MusicDiscItem MUSIC_DISC_MEMORANDUM = new MusicDiscItem(14, YSounds.MEMORANDUM_MONO, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 299) {};
	
	public static final Item RUBBLE = new Item(new Item.Settings()) {};
	
	public static final Item PROMETHIUM_SPECK = new Item(new Item.Settings().rarity(Rarity.EPIC));
	public static final Item PROMETHIUM_LUMP = new Item(new Item.Settings().rarity(Rarity.EPIC));
	public static final Item PROMETHIUM_GLOB = new Item(new Item.Settings().rarity(Rarity.EPIC));
	
	public static final Item MAGCAPSULE = new Item(new Item.Settings().maxCount(1));
	public static final Item CUPROSTEEL_INGOT = new Item(new Item.Settings());
	
	public static final LatchReference<Item> CUPROSTEEL_COIL = YLatches.create();
	public static final LatchReference<Item> AMMO_PACK = YLatches.create();
	
	public static final Item EMPTY_AMMO_CAN = new Item(new Item.Settings().maxCount(16));
	public static final AmmoCanItem AMMO_CAN = new AmmoCanItem(new Item.Settings().maxCount(1));

	public static final InRedMultimeterItem INRED_MULTIMETER = new InRedMultimeterItem(new Item.Settings()
			.maxCount(1));
	public static final Item INRED_PCB = new Item(new Item.Settings());
	
	public static final SpatulaItem SPATULA = new SpatulaItem(ToolMaterials.IRON, 6, -3.2f, new Item.Settings());
	
	public static final Item RAW_GADOLINITE = new Item(new Item.Settings());
	public static final Item DELRENE_SCRAP = new Item(new Item.Settings());
	
	@BuiltinRenderer("CreaseRenderer")
	public static final CreaseItem CREASE = new CreaseItem(new Item.Settings()
			.maxCount(1));
	
	public static final Item HAEMOPAL = new HaemopalItem(new Item.Settings().maxCount(1));
	public static final Item BEETOPAL = new HaemopalItem(new Item.Settings().maxCount(1));
	public static final Item EMPTY_HAEMOPAL = new Item(new Item.Settings().maxCount(1));
	
	public static final Item SPARK = new Item(new Item.Settings().maxCount(4));
	
	@ColorProvider("ContinuityItemColorProvider")
	public static final LatchReference<Item> PLATFORMS = YLatches.create();
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ITEM, YItems.class, Item.class);
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface BuiltinRenderer {
		String value();
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface ColorProvider {
		String value();
	}
}
