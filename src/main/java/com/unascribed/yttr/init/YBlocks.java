package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.ClamberBlock;
import com.unascribed.yttr.content.block.CustomFallingBlock;
import com.unascribed.yttr.content.block.CustomShapeBlock;
import com.unascribed.yttr.content.block.NeodymiumBlock;
import com.unascribed.yttr.content.block.TemporaryAirBlock;
import com.unascribed.yttr.content.block.TemporaryFluidBlock;
import com.unascribed.yttr.content.block.TintBlock;
import com.unascribed.yttr.content.block.abomination.AwareHopperBlock;
import com.unascribed.yttr.content.block.abomination.ScreeperNestBlock;
import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlock;
import com.unascribed.yttr.content.block.abomination.TransfungusBlock;
import com.unascribed.yttr.content.block.abomination.TransfungusSporesBlock;
import com.unascribed.yttr.content.block.basic.BasicFacingBlock;
import com.unascribed.yttr.content.block.basic.BasicHorizontalFacingBlock;
import com.unascribed.yttr.content.block.big.DSUBlock;
import com.unascribed.yttr.content.block.big.GiantsBlock;
import com.unascribed.yttr.content.block.big.MagtankBlock;
import com.unascribed.yttr.content.block.decor.BloqueBlock;
import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.content.block.decor.ContinuousPlatformBlock;
import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.block.decor.ScorchedCryingObsidianBlock;
import com.unascribed.yttr.content.block.decor.TableBlock;
import com.unascribed.yttr.content.block.decor.WallLampBlock;
import com.unascribed.yttr.content.block.device.CanFillerBlock;
import com.unascribed.yttr.content.block.device.CentrifugeBlock;
import com.unascribed.yttr.content.block.device.DyedProjectTableBlock;
import com.unascribed.yttr.content.block.device.EncasedVoidFilterBlock;
import com.unascribed.yttr.content.block.device.PowerMeterBlock;
import com.unascribed.yttr.content.block.device.ProjectTableBlock;
import com.unascribed.yttr.content.block.device.RafterBlock;
import com.unascribed.yttr.content.block.device.SSDBlock;
import com.unascribed.yttr.content.block.device.SuitStationBlock;
import com.unascribed.yttr.content.block.device.VoidFilterBlock;
import com.unascribed.yttr.content.block.inred.InRedAndGateBlock;
import com.unascribed.yttr.content.block.inred.InRedBlock;
import com.unascribed.yttr.content.block.inred.InRedCableBlock;
import com.unascribed.yttr.content.block.inred.InRedDemoCyclerBlock;
import com.unascribed.yttr.content.block.inred.InRedDiodeBlock;
import com.unascribed.yttr.content.block.inred.InRedEncoderBlock;
import com.unascribed.yttr.content.block.inred.InRedNotGateBlock;
import com.unascribed.yttr.content.block.inred.InRedOscillatorBlock;
import com.unascribed.yttr.content.block.inred.InRedScaffoldBlock;
import com.unascribed.yttr.content.block.inred.InRedShifterBlock;
import com.unascribed.yttr.content.block.inred.InRedTransistorBlock;
import com.unascribed.yttr.content.block.inred.InRedXorGateBlock;
import com.unascribed.yttr.content.block.lazor.IRLazorBeamBlock;
import com.unascribed.yttr.content.block.lazor.IRLazorEmitterBlock;
import com.unascribed.yttr.content.block.lazor.LazorBeamBlock;
import com.unascribed.yttr.content.block.lazor.LazorEmitterBlock;
import com.unascribed.yttr.content.block.mechanism.ChuteBlock;
import com.unascribed.yttr.content.block.mechanism.CuprosteelPressurePlateBlock;
import com.unascribed.yttr.content.block.mechanism.DopperBlock;
import com.unascribed.yttr.content.block.mechanism.FlopperBlock;
import com.unascribed.yttr.content.block.mechanism.LevitationChamberBlock;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlock;
import com.unascribed.yttr.content.block.mechanism.VelresinBlock;
import com.unascribed.yttr.content.block.mechanism.VoidCauldronBlock;
import com.unascribed.yttr.content.block.mechanism.YttriumButtonBlock;
import com.unascribed.yttr.content.block.mechanism.YttriumPressurePlateBlock;
import com.unascribed.yttr.content.block.natural.CoreLavaFluidBlock;
import com.unascribed.yttr.content.block.natural.DelicaceBlock;
import com.unascribed.yttr.content.block.natural.HaemopalHolsterBlock;
import com.unascribed.yttr.content.block.natural.RootOfContinuityBlock;
import com.unascribed.yttr.content.block.natural.SqueezeLeavesBlock;
import com.unascribed.yttr.content.block.natural.SqueezeLogBlock;
import com.unascribed.yttr.content.block.natural.SqueezeSaplingBlock;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlock;
import com.unascribed.yttr.content.block.note.BoggedHighNoteBlock;
import com.unascribed.yttr.content.block.note.BoggedLowNoteBlock;
import com.unascribed.yttr.content.block.note.BoggedNoteBlock;
import com.unascribed.yttr.content.block.note.HighNoteBlock;
import com.unascribed.yttr.content.block.note.LowNoteBlock;
import com.unascribed.yttr.content.block.ruined.RCStyleMultiblock;
import com.unascribed.yttr.content.block.ruined.RuinedFrameBlock;
import com.unascribed.yttr.content.block.ruined.RuinedLeverBlock;
import com.unascribed.yttr.content.block.ruined.RuinedPipeBlock;
import com.unascribed.yttr.content.block.ruined.RuinedTorchBlock;
import com.unascribed.yttr.content.block.ruined.RuinedWallTorchBlock;
import com.unascribed.yttr.content.block.ruined.WastelandGrassBlock;
import com.unascribed.yttr.content.block.void_.BedrockSmasherBlock;
import com.unascribed.yttr.content.block.void_.DivingPlateBlock;
import com.unascribed.yttr.content.block.void_.DormantVoidGeyserBlock;
import com.unascribed.yttr.content.block.void_.ErodedBedrockBlock;
import com.unascribed.yttr.content.block.void_.GlassyVoidBlock;
import com.unascribed.yttr.content.block.void_.GlassyVoidPaneBlock;
import com.unascribed.yttr.content.block.void_.InfiniteVoidFluidBlock;
import com.unascribed.yttr.content.block.void_.MagtubeBlock;
import com.unascribed.yttr.content.block.void_.PureVoidFluidBlock;
import com.unascribed.yttr.content.block.void_.VoidFluidBlock;
import com.unascribed.yttr.content.block.void_.VoidGeyserBlock;
import com.unascribed.yttr.mixin.accessor.AccessorBlock;
import com.unascribed.yttr.util.annotate.RenderLayer;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock.TypedContextPredicate;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FernBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.int_provider.UniformIntProvider;
import net.minecraft.world.World;

public class YBlocks {

	private static final FabricBlockSettings METALLIC_SETTINGS = FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL);

	private static final FabricBlockSettings INRED_DEVICE_SETTINGS = FabricBlockSettings.create()
			.strength(0.5F, 8)
			.breakInstantly()
			.mapColor(MapColor.CYAN);
	
	public static final BlockSoundGroup HOLLOWHUGE_SOUNDS = new BlockSoundGroup(0.8f, 1, YSounds.HOLLOWBREAKHUGE, YSounds.HOLLOWSTEP, YSounds.HOLLOWPLACEHUGE, YSounds.HOLLOWHIT, YSounds.HOLLOWSTEP);
	public static final BlockSoundGroup HOLLOW_SOUNDS = new BlockSoundGroup(0.8f, 1, YSounds.HOLLOWBREAK, YSounds.HOLLOWSTEP, YSounds.HOLLOWPLACE, YSounds.HOLLOWHIT, YSounds.HOLLOWSTEP);
	public static final BlockSoundGroup CONTINUOUS_SOUNDS = new BlockSoundGroup(0.3f, 1, YSounds.PROJECT, YSounds.PROJECT, YSounds.PROJECT, YSounds.PROJECT, YSounds.PROJECT);
	
	private static final FabricBlockSettings HOLLOWHUGE_SETTINGS = FabricBlockSettings.copyOf(METALLIC_SETTINGS)
			.sounds(HOLLOWHUGE_SOUNDS)
			.strength(8)
			.nonOpaque();
	private static final FabricBlockSettings HOLLOW_SETTINGS = FabricBlockSettings.copyOf(METALLIC_SETTINGS)
			.sounds(HOLLOW_SOUNDS);
	private static final FabricBlockSettings GLASSY_VOID_SETTINGS = FabricBlockSettings.create()
			.strength(7)
			.nonOpaque();
	
	public static final Block GADOLINITE = new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE));
	public static final Block DEEPSLATE_GADOLINITE = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE));
	
	public static final Block YTTRIUM_BLOCK = new Block(METALLIC_SETTINGS);
	public static final PowerMeterBlock POWER_METER = new PowerMeterBlock(METALLIC_SETTINGS);
	
	
	public static final VoidFluidBlock VOID = new VoidFluidBlock(YFluids.VOID, FabricBlockSettings.create()
		.mapColor(MapColor.BLACK)
		.notSolid()
		.pistonBehavior(PistonBehavior.DESTROY)
		.replaceable()
		.liquid()
		.noCollision()
		.strength(100)
		.dropsNothing()
	);
	@RenderLayer("translucent")
	public static final PureVoidFluidBlock PURE_VOID = new PureVoidFluidBlock(YFluids.PURE_VOID, FabricBlockSettings.create()
		.liquid()
		.notSolid()
		.noCollision()
		.pistonBehavior(PistonBehavior.DESTROY)
		.strength(100)
		.dropsNothing()
	);
	public static final FluidBlock CORE_LAVA = new CoreLavaFluidBlock(YFluids.CORE_LAVA, FabricBlockSettings.create()
		.mapColor(MapColor.RED)
		.notSolid()
		.liquid()
		.noCollision()
		.strength(100)
		.dropsNothing()
		.luminance(bs -> 15)
		.pistonBehavior(PistonBehavior.BLOCK));
	
	
	public static final AwareHopperBlock AWARE_HOPPER = new AwareHopperBlock(METALLIC_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final LevitationChamberBlock LEVITATION_CHAMBER = new LevitationChamberBlock(FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.nonOpaque()
		);
	@RenderLayer("cutout_mipped")
	public static final ChuteBlock CHUTE = new ChuteBlock(FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
		);
	public static final VoidGeyserBlock VOID_GEYSER = new VoidGeyserBlock(FabricBlockSettings.create()
			.strength(-1, 9000000)
			.dropsNothing()
			.solid()
		);
	public static final DormantVoidGeyserBlock DORMANT_VOID_GEYSER = new DormantVoidGeyserBlock(FabricBlockSettings.copyOf(VOID_GEYSER)
			.nonOpaque());
	public static final Block BEDROCK_SMASHER = new BedrockSmasherBlock(FabricBlockSettings.create()
			.strength(35, 4000));
	public static final Block RUINED_BEDROCK = new Block(FabricBlockSettings.create()
			.strength(75, 9000000)
			.nonOpaque()
		);
	@RenderLayer("translucent")
	public static final Block GLASSY_VOID = new GlassyVoidBlock(3, GLASSY_VOID_SETTINGS);
	public static final Block SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.create()
			.mapColor(MapColor.YELLOW)
			.sounds(BlockSoundGroup.GRASS)
			.strength(2)
		);
	public static final Block STRIPPED_SQUEEZE_LOG = new SqueezeLogBlock(FabricBlockSettings.copyOf(SQUEEZE_LOG));
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZE_LEAVES = new SqueezeLeavesBlock(FabricBlockSettings.create()
			.mapColor(MapColor.YELLOW)
			.sounds(BlockSoundGroup.GRASS)
			.strength(0.2f)
			.suffocates((bs, bv, pos) -> false)
			.blockVision((bs, bv, pos) -> false)
			.nonOpaque()
			.ticksRandomly()
		);
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZED_LEAVES = new SqueezedLeavesBlock(FabricBlockSettings.copyOf(SQUEEZE_LEAVES)
			.drops(Yttr.id("blocks/squeeze_leaves"))
			.dynamicBounds()
		);
	@RenderLayer("cutout_mipped")
	public static final Block SQUEEZE_SAPLING = new SqueezeSaplingBlock(new SqueezeSaplingGenerator(), FabricBlockSettings.create()
			.mapColor(MapColor.YELLOW)
			.sounds(BlockSoundGroup.GRASS)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.nonOpaque()
		);
	@RenderLayer("translucent")
	public static final Block DELICACE = new DelicaceBlock(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK));
	@RenderLayer("cutout")
	public static final Block LAMP = new LampBlock(FabricBlockSettings.create()
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
		);
	@RenderLayer("cutout")
	public static final Block FIXTURE = new WallLampBlock(FabricBlockSettings.create()
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			, 12, 2, 10, 6);
	@RenderLayer("cutout")
	public static final Block CAGE_LAMP = new WallLampBlock(FabricBlockSettings.create()
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			, 10, 2, 6, 10);
	@RenderLayer("cutout")
	public static final Block PANEL = new WallLampBlock(FabricBlockSettings.create()
			.strength(2)
			.sounds(BlockSoundGroup.METAL)
			, 14, 1, 12, 1);
	
	
	public static final Block YTTRIUM_PLATING = new Block(METALLIC_SETTINGS);
	@RenderLayer("translucent")
	public static final Block GLASSY_VOID_PANE = new GlassyVoidPaneBlock(3, GLASSY_VOID_SETTINGS);
	
	public static final CleavedBlock CLEAVED_BLOCK = new CleavedBlock(FabricBlockSettings.create()
			.dynamicBounds()
			.nonOpaque());

	public static final YttriumPressurePlateBlock LIGHT_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 15);

	public static final YttriumPressurePlateBlock HEAVY_YTTRIUM_PLATE = new YttriumPressurePlateBlock(METALLIC_SETTINGS, 64);
	
	public static final CentrifugeBlock CENTRIFUGE = new CentrifugeBlock(METALLIC_SETTINGS);
	
	public static final DopperBlock DOPPER = new DopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	public static final FlopperBlock FLOPPER = new FlopperBlock(FabricBlockSettings.copyOf(Blocks.HOPPER));
	
	public static final DivingPlateBlock DIVING_PLATE = new DivingPlateBlock(METALLIC_SETTINGS);
	
	public static final SuitStationBlock SUIT_STATION = new SuitStationBlock(METALLIC_SETTINGS);
	
	public static final TableBlock TABLE = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS));
	
	public static final SkeletalSorterBlock SKELETAL_SORTER = new SkeletalSorterBlock(FabricBlockSettings.copyOf(TABLE));
	
	public static final ReplicatorBlock REPLICATOR = new ReplicatorBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK)
			.nonOpaque()
			.noCollision()
			.dropsNothing()
		);
	
	public static final VoidCauldronBlock VOID_CAULDRON = new VoidCauldronBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON)
			.dropsLike(Blocks.CAULDRON));
	
	public static final Block ULTRAPURE_CARBON_BLOCK = new Block(FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
		);
	
	public static final Block COMPRESSED_ULTRAPURE_CARBON_BLOCK = new Block(FabricBlockSettings.create()
			.strength(6)
			.requiresTool()
			.sounds(BlockSoundGroup.STONE)
		);
	
	public static final EncasedVoidFilterBlock ENCASED_VOID_FILTER = new EncasedVoidFilterBlock(FabricBlockSettings.copyOf(BEDROCK_SMASHER));
	public static final VoidFilterBlock VOID_FILTER = new VoidFilterBlock(FabricBlockSettings.copyOf(METALLIC_SETTINGS)
			.resistance(4000));
	
	public static final ErodedBedrockBlock ERODED_BEDROCK = new ErodedBedrockBlock(FabricBlockSettings.create()
			.strength(45, 0)
		);
	
	public static final LazorBeamBlock LAZOR_BEAM = new LazorBeamBlock(FabricBlockSettings.create()
			.collidable(false)
			.replaceable()
			.dropsNothing()
			.ticksRandomly()
			.strength(0, 10000)
			.luminance(bs -> 13)
			.pistonBehavior(PistonBehavior.DESTROY)
		);
	
	@RenderLayer("cutout")
	public static final LazorEmitterBlock LAZOR_EMITTER = new LazorEmitterBlock(FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.pistonBehavior(PistonBehavior.DESTROY)
			.sounds(BlockSoundGroup.METAL)
		);
	
	public static final MagtankBlock MAGTANK = new MagtankBlock(HOLLOWHUGE_SETTINGS);
	public static final GiantsBlock GIANT_COBBLESTONE = new GiantsBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE));
	public static final DSUBlock DSU = new DSUBlock(HOLLOWHUGE_SETTINGS);
	
	public static final Block BROOKITE_ORE = new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.EMERALD_ORE), UniformIntProvider.create(1, 5));
	public static final Block DEEPSLATE_BROOKITE_ORE = new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_EMERALD_ORE), UniformIntProvider.create(1, 5));
	
	public static final RootOfContinuityBlock ROOT_OF_CONTINUITY = new RootOfContinuityBlock(FabricBlockSettings.create()
			.strength(20)
			.luminance(4)
			.sounds(RootOfContinuityBlock.SOUND_GROUP)
			.requiresTool());
	
	public static final YttriumButtonBlock YTTRIUM_BUTTON = new YttriumButtonBlock(FabricBlockSettings.create()
			.strength(1)
			.requiresTool()
			.sounds(BlockSoundGroup.METAL)
			.noCollision());
	
	public static final Block BROOKITE_BLOCK = new Block(FabricBlockSettings.create()
			.strength(3)
			.requiresTool()
			.sounds(BlockSoundGroup.NETHERITE)
		);
	
	public static final AirBlock TEMPORARY_LIGHT_AIR = new TemporaryAirBlock(FabricBlockSettings.create()
			.noCollision()
			.air()
			.nonOpaque()
			.luminance(9)
			.ticksRandomly());
	
	public static final AirBlock PERMANENT_LIGHT_AIR = new AirBlock(FabricBlockSettings.create()
			.noCollision()
			.air()
			.nonOpaque()
			.luminance(15)
		);
	
	public static final FluidBlock TEMPORARY_LIGHT_WATER = new TemporaryFluidBlock(Fluids.WATER, FabricBlockSettings.copyOf(Blocks.WATER)
			.luminance(9)
		);
	
	public static final FluidBlock PERMANENT_LIGHT_WATER = new FluidBlock(Fluids.WATER, FabricBlockSettings.copyOf(Blocks.WATER)
			.luminance(15)
		);
		
	public static final Block NETHERTUFF = new Block(FabricBlockSettings.copyOf(Blocks.NETHERRACK)
			.strength(1.4f, 0.2f)
			.allowsSpawning((state, world, pos, et) -> false)
		);
	
	@RenderLayer("cutout")
	public static final MagtubeBlock MAGTUBE = new MagtubeBlock(METALLIC_SETTINGS);
	
	public static final Block WASTELAND_DIRT = new Block(FabricBlockSettings.copyOf(Blocks.DIRT)
			.strength(0.8f)
		);
	
	@RenderLayer("cutout")
	public static final FernBlock WASTELAND_GRASS = new WastelandGrassBlock(FabricBlockSettings.copyOf(Blocks.GRASS));
	
	public static final PillarBlock WASTELAND_LOG = new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG)
			.strength(1.0f)
		);
	
	public static final Block WASTELAND_STONE = new Block(FabricBlockSettings.copyOf(Blocks.STONE)
			.strength(1.0f)
		);
	
	private static final Block.Settings RUINED_SETTINGS = FabricBlockSettings.copyOf(Blocks.DIRT)
			.sounds(BlockSoundGroup.BASALT)
			.drops(Yttr.id("blocks/ruined")
		);
	private static final Block.Settings RUINED_UNCL_SETTINGS = FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.noCollision()
			.drops(Yttr.id("blocks/ruined")
		);
	private static final Block.Settings RUINED_PARTIAL_SETTINGS = FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.nonOpaque()
			.drops(Yttr.id("blocks/ruined")
		);
	private static final Block.Settings RUINED_TORCH_SETTINGS = FabricBlockSettings.copyOf(RUINED_UNCL_SETTINGS)
			.luminance(2)
			.drops(Yttr.id("blocks/ruined")
		);
	
	private static <T extends Block> T ruinedDevice(T block) {
		((AccessorBlock)block).yttr$setTranslationKey("block.yttr.ruined_device");
		return block;
	}
	
	private static <T extends Block> T ruinedConstruct(T block) {
		((AccessorBlock)block).yttr$setTranslationKey("block.yttr.ruined_construct");
		return block;
	}
	
	public static final Block RUINED_COBBLESTONE = new Block(RUINED_SETTINGS);
	public static final Block RUINED_BRICKS = new Block(RUINED_SETTINGS);
	
	public static final Block RUINED_CONTAINER = new Block(FabricBlockSettings.copyOf(RUINED_SETTINGS)
			.drops(Yttr.id("blocks/ruined_container")));
	@RenderLayer("cutout")
	public static final Block RUINED_TANK = new CustomShapeBlock(RUINED_SETTINGS, Block.createCuboidShape(2, 0, 2, 14, 16, 14));
	
	public static final Block RUINED_DEVICE_BC_1 = ruinedDevice(new BasicHorizontalFacingBlock(RUINED_SETTINGS));
	public static final Block RUINED_DEVICE_BC_2 = new BasicFacingBlock(RUINED_PARTIAL_SETTINGS);
	
	public static final Block RUINED_DEVICE_GT_1 = ruinedDevice(new Block(RUINED_SETTINGS));
	
	public static final Block RUINED_DEVICE_RP_1 = ruinedDevice(new BasicFacingBlock(RUINED_SETTINGS));
	
	public static final Block RUINED_CONSTRUCT_RC_1 = ruinedConstruct(new RCStyleMultiblock(1, 1, 1, RUINED_SETTINGS));
	public static final Block RUINED_CONSTRUCT_RC_2 = ruinedConstruct(new RCStyleMultiblock(1, 3, 0, RUINED_SETTINGS));
	
	@RenderLayer("cutout")
	public static final Block RUINED_DEVICE_FO_1 = ruinedDevice(new Block(RUINED_PARTIAL_SETTINGS));
	
	@RenderLayer("cutout")
	public static final Block RUINED_PIPE = new RuinedPipeBlock(RUINED_SETTINGS);
	@RenderLayer("cutout")
	public static final Block RUINED_FRAME = new RuinedFrameBlock(RUINED_SETTINGS);
	@RenderLayer("cutout")
	public static final Block RUINED_TUBE = new RuinedPipeBlock(RUINED_SETTINGS);
	
	@RenderLayer("cutout")
	public static final Block RUINED_TORCH = new RuinedTorchBlock(RUINED_TORCH_SETTINGS, null);
	@RenderLayer("cutout")
	public static final Block RUINED_WALL_TORCH = new RuinedWallTorchBlock(RUINED_TORCH_SETTINGS, null);
	public static final Block RUINED_LEVER = new RuinedLeverBlock(RUINED_UNCL_SETTINGS);
	
	public static final Block SPECIALTY_BEDROCK = new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).dropsNothing());
	@RenderLayer("translucent")
	public static final ContinuousPlatformBlock CONTINUOUS_PLATFORM = new ContinuousPlatformBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK)
			.dropsNothing()
			.ticksRandomly()
			.nonOpaque()
			.blockVision((state, world, pos) -> false)
			.suffocates((state, world, pos) -> false)
			.allowsSpawning((state, world, pos, entity) -> false)
			.sounds(CONTINUOUS_SOUNDS)
		);
	
	public static final HighNoteBlock HIGH_NOTE_BLOCK = new HighNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final LowNoteBlock LOW_NOTE_BLOCK = new LowNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	
	public static final BoggedNoteBlock BOGGED_NOTE_BLOCK = new BoggedNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final BoggedHighNoteBlock BOGGED_HIGH_NOTE_BLOCK = new BoggedHighNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	public static final BoggedLowNoteBlock BOGGED_LOW_NOTE_BLOCK = new BoggedLowNoteBlock(FabricBlockSettings.copyOf(Blocks.NOTE_BLOCK));
	
	public static final ClamberBlock CLAMBER_BLOCK = new ClamberBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.nonOpaque()
		);
	public static final ClamberBlock SOUL_CLAMBER_BLOCK = new ClamberBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.mapColor(MapColor.BROWN)
			.nonOpaque()
		);
	
	public static final Block SOUL_PLANKS = new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
			.mapColor(MapColor.BROWN)
		);

	public static final InRedBlock INRED_BLOCK = new InRedBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK)
			.mapColor(MapColor.MAGENTA)
	);
	public static final InRedCableBlock INRED_CABLE = new InRedCableBlock(FabricBlockSettings.create()
			.strength(0F, 8F)
			.breakInstantly()
	);
	@RenderLayer("cutout_mipped")
	public static final InRedScaffoldBlock INRED_SCAFFOLD = new InRedScaffoldBlock(FabricBlockSettings.copyOf(Blocks.SCAFFOLDING));
	@RenderLayer("cutout_mipped")
	public static final InRedAndGateBlock INRED_AND_GATE = new InRedAndGateBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedNotGateBlock INRED_NOT_GATE = new InRedNotGateBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedXorGateBlock INRED_XOR_GATE = new InRedXorGateBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedDiodeBlock INRED_DIODE = new InRedDiodeBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedShifterBlock INRED_SHIFTER = new InRedShifterBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedTransistorBlock INRED_TRANSISTOR = new InRedTransistorBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedEncoderBlock INRED_ENCODER = new InRedEncoderBlock(INRED_DEVICE_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final InRedOscillatorBlock INRED_OSCILLATOR = new InRedOscillatorBlock(INRED_DEVICE_SETTINGS);
	public static final InRedDemoCyclerBlock INRED_DEMO_CYCLER = new InRedDemoCyclerBlock(INRED_DEVICE_SETTINGS);

	public static final Block CUPROSTEEL_BLOCK = new Block(METALLIC_SETTINGS);
	public static final CuprosteelPressurePlateBlock CUPROSTEEL_PLATE = new CuprosteelPressurePlateBlock(METALLIC_SETTINGS);
	
	public static final CanFillerBlock CAN_FILLER = new CanFillerBlock(METALLIC_SETTINGS);
	
	public static final FallingBlock DUST = new CustomFallingBlock(FabricBlockSettings.copyOf(Blocks.SAND), 0xFFD7D3C5);
	
	public static final NeodymiumBlock NEODYMIUM_SLAB = new NeodymiumBlock(METALLIC_SETTINGS);
	
	public static final RafterBlock RAFTER = new RafterBlock(METALLIC_SETTINGS);
	@RenderLayer("cutout_mipped")
	public static final ProjectTableBlock PROJECT_TABLE = new ProjectTableBlock(FabricBlockSettings.create()
			.mapColor(MapColor.PINK)
			.hardness(1.5f)
			.sounds(BlockSoundGroup.WOOD));
	@RenderLayer("cutout_mipped")
	public static final DyedProjectTableBlock DYED_PROJECT_TABLE = new DyedProjectTableBlock(FabricBlockSettings.copyOf(PROJECT_TABLE));
	
	public static final Block RAW_GADOLINITE_BLOCK = new Block(FabricBlockSettings.copyOf(Blocks.RAW_IRON_BLOCK));
	
	private static final TypedContextPredicate<EntityType<?>> NOT_IN_TERMINUS = (state, world, pos, et) -> {
		return !(world instanceof World w && w.getBiome(pos).isRegistryKeyId(Yttr.id("scorched_terminus")));
	};
	
	public static final FallingBlock ASH = new CustomFallingBlock(FabricBlockSettings.copyOf(Blocks.SAND)
			.allowsSpawning(NOT_IN_TERMINUS), 0xFF181018);
	
	public static final BloqueBlock BLOQUE = new BloqueBlock(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)
			.sounds(BlockSoundGroup.CALCITE));
	public static final Block DELRENE = new Block(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)
			.sounds(BlockSoundGroup.CANDLE));
	
	
	public static final Block SCORCHED_OBSIDIAN = new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)
			.mapColor(MapColor.RED_TERRACOTTA)
			.sounds(BlockSoundGroup.ANCIENT_DEBRIS)
			.allowsSpawning(NOT_IN_TERMINUS));
	public static final Block POLISHED_SCORCHED_OBSIDIAN = new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)
			.mapColor(MapColor.RED_TERRACOTTA)
			.sounds(BlockSoundGroup.ANCIENT_DEBRIS)
			.allowsSpawning(NOT_IN_TERMINUS));
	public static final Block POLISHED_SCORCHED_OBSIDIAN_CAPSTONE = new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)
			.mapColor(MapColor.RED_TERRACOTTA)
			.sounds(BlockSoundGroup.ANCIENT_DEBRIS)
			.allowsSpawning(NOT_IN_TERMINUS));
	public static final Block POLISHED_SCORCHED_OBSIDIAN_HOLSTER = new HaemopalHolsterBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)
			.mapColor(MapColor.RED)
			.luminance(4)
			.sounds(BlockSoundGroup.NETHERITE)
			.allowsSpawning(NOT_IN_TERMINUS));
	
	public static final Block POLISHED_OBSIDIAN = new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN));
	public static final Block POLISHED_OBSIDIAN_CAPSTONE = new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN));
	
	public static final Block SCORCHED_CRYING_OBSIDIAN = new ScorchedCryingObsidianBlock(FabricBlockSettings.copyOf(Blocks.CRYING_OBSIDIAN)
			.mapColor(MapColor.RED_TERRACOTTA)
			.sounds(BlockSoundGroup.ANCIENT_DEBRIS)
			.allowsSpawning(NOT_IN_TERMINUS));

	public static final ScreeperNestBlock SCREEPER_NEST = new ScreeperNestBlock(FabricBlockSettings.copyOf(Blocks.BEE_NEST));
	
	@RenderLayer("cutout_mipped")
	public static final SSDBlock SSD = new SSDBlock(FabricBlockSettings.copyOf(HOLLOW_SETTINGS));
	
	public static final VelresinBlock VELRESIN = new VelresinBlock(FabricBlockSettings.create()
			.mapColor(MapColor.YELLOW_TERRACOTTA)
			.sounds(BlockSoundGroup.HONEY)
			.strength(0));

	@RenderLayer("translucent")
	public static final Block BLACK_VOID_GLASS = new GlassyVoidBlock(0, GLASSY_VOID_SETTINGS);
	@RenderLayer("translucent")
	public static final Block BLACK_VOID_GLASS_PANE = new GlassyVoidPaneBlock(1, GLASSY_VOID_SETTINGS);
	
	@RenderLayer("translucent")
	public static final Block GRAY_VOID_GLASS = new GlassyVoidBlock(2, GLASSY_VOID_SETTINGS);
	@RenderLayer("translucent")
	public static final Block GRAY_VOID_GLASS_PANE = new GlassyVoidPaneBlock(2, GLASSY_VOID_SETTINGS);
	
	@RenderLayer("translucent")
	public static final Block SILVER_VOID_GLASS = new GlassyVoidBlock(4, GLASSY_VOID_SETTINGS);
	@RenderLayer("translucent")
	public static final Block SILVER_VOID_GLASS_PANE = new GlassyVoidPaneBlock(4, GLASSY_VOID_SETTINGS);
	
	@RenderLayer("translucent")
	public static final Block WHITE_VOID_GLASS = new GlassyVoidBlock(8, GLASSY_VOID_SETTINGS);
	@RenderLayer("translucent")
	public static final Block WHITE_VOID_GLASS_PANE = new GlassyVoidPaneBlock(8, GLASSY_VOID_SETTINGS);
	
	@RenderLayer("translucent")
	public static final Block CLEAR_VOID_GLASS = new GlassyVoidBlock(12, GLASSY_VOID_SETTINGS);
	@RenderLayer("translucent")
	public static final Block CLEAR_VOID_GLASS_PANE = new GlassyVoidPaneBlock(12, GLASSY_VOID_SETTINGS);
	
	public static final FluidBlock INFINITE_VOID = new InfiniteVoidFluidBlock(YFluids.VOID, FabricBlockSettings.create()
		.notSolid()
		.liquid()
		.pistonBehavior(PistonBehavior.BLOCK)
		.noCollision()
		.strength(-1)
		.dropsNothing());

	@RenderLayer("cutout_mipped")
	public static final Block TRANSFUNGUS = new TransfungusBlock(FabricBlockSettings.create()
			.sounds(BlockSoundGroup.CAVE_VINES)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.nonOpaque()
		);

	
	public static final TransfungusSporesBlock TRANSFUNGUS_SPORES = new TransfungusSporesBlock(FabricBlockSettings.create()
			.noCollision()
			.air()
			.replaceable()
			.nonOpaque()
		);
	
	public static final IRLazorBeamBlock IR_LAZOR_BEAM = new IRLazorBeamBlock(FabricBlockSettings.create()
			.collidable(false)
			.dropsNothing()
			.replaceable()
			.ticksRandomly()
			.strength(0, 10000)
			.pistonBehavior(PistonBehavior.DESTROY)
			.air()
		);
	
	@RenderLayer("cutout")
	public static final IRLazorEmitterBlock IR_LAZOR_EMITTER = new IRLazorEmitterBlock(FabricBlockSettings.create()
			.strength(4)
			.requiresTool()
			.pistonBehavior(PistonBehavior.DESTROY)
			.sounds(BlockSoundGroup.METAL)
		);

	
	public static final TintBlock TINT = new TintBlock(FabricBlockSettings.create()
			.noCollision()
			.replaceable()
			.strength(-1.0F, 3600000.8F)
			.dropsNothing()
			.nonOpaque()
		);
	
	

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.BLOCK, YBlocks.class, Block.class);
	}

}
