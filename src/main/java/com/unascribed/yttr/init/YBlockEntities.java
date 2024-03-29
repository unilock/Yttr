package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.block.abomination.AwareHopperBlockEntity;
import com.unascribed.yttr.content.block.abomination.ScreeperNestBlockEntity;
import com.unascribed.yttr.content.block.abomination.SkeletalSorterBlockEntity;
import com.unascribed.yttr.content.block.big.DSUBlockEntity;
import com.unascribed.yttr.content.block.decor.BloqueBlockEntity;
import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;
import com.unascribed.yttr.content.block.decor.LampBlockEntity;
import com.unascribed.yttr.content.block.device.CanFillerBlockEntity;
import com.unascribed.yttr.content.block.device.CentrifugeBlockEntity;
import com.unascribed.yttr.content.block.device.PowerMeterBlockEntity;
import com.unascribed.yttr.content.block.device.ProjectTableBlockEntity;
import com.unascribed.yttr.content.block.device.SSDBlockEntity;
import com.unascribed.yttr.content.block.device.SuitStationBlockEntity;
import com.unascribed.yttr.content.block.device.VoidFilterBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedAndGateBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedDemoCyclerBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedDiodeBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedEncoderBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedNotGateBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedOscillatorBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedShifterBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedTransistorBlockEntity;
import com.unascribed.yttr.content.block.inred.InRedXorGateBlockEntity;
import com.unascribed.yttr.content.block.lazor.LazorBeamBlockEntity;
import com.unascribed.yttr.content.block.mechanism.ChuteBlockEntity;
import com.unascribed.yttr.content.block.mechanism.DopperBlockEntity;
import com.unascribed.yttr.content.block.mechanism.FlopperBlockEntity;
import com.unascribed.yttr.content.block.mechanism.LevitationChamberBlockEntity;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlockEntity;
import com.unascribed.yttr.content.block.mechanism.VoidCauldronBlockEntity;
import com.unascribed.yttr.content.block.natural.SqueezedLeavesBlockEntity;
import com.unascribed.yttr.content.block.void_.VoidGeyserBlockEntity;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.registry.Registries;

public class YBlockEntities {

	@Renderer("AwareHopperBlockEntityRenderer")
	public static final BlockEntityType<AwareHopperBlockEntity> AWARE_HOPPER = create(AwareHopperBlockEntity::new, YBlocks.AWARE_HOPPER);
	@Renderer("PowerMeterBlockEntityRenderer")
	public static final BlockEntityType<PowerMeterBlockEntity> POWER_METER = create(PowerMeterBlockEntity::new, YBlocks.POWER_METER);
	@Renderer("LevitationChamberBlockEntityRenderer")
	public static final BlockEntityType<LevitationChamberBlockEntity> LEVITATION_CHAMBER = create(LevitationChamberBlockEntity::new, YBlocks.LEVITATION_CHAMBER);
	public static final BlockEntityType<ChuteBlockEntity> CHUTE = create(ChuteBlockEntity::new, YBlocks.CHUTE);
	public static final BlockEntityType<VoidGeyserBlockEntity> VOID_GEYSER = create(VoidGeyserBlockEntity::new, YBlocks.VOID_GEYSER);
	@Renderer("SqueezedLeavesBlockEntityRenderer")
	public static final BlockEntityType<SqueezedLeavesBlockEntity> SQUEEZED_LEAVES = create(SqueezedLeavesBlockEntity::new, YBlocks.SQUEEZED_LEAVES);
	public static final BlockEntityType<LampBlockEntity> LAMP = create(LampBlockEntity::new, YBlocks.LAMP, YBlocks.FIXTURE, YBlocks.CAGE_LAMP, YBlocks.PANEL);
//	@Renderer("CleavedBlockEntityRenderer")
	public static final BlockEntityType<CleavedBlockEntity> CLEAVED_BLOCK = create(CleavedBlockEntity::new, YBlocks.CLEAVED_BLOCK);
	public static final BlockEntityType<CentrifugeBlockEntity> CENTRIFUGE = create(CentrifugeBlockEntity::new, YBlocks.CENTRIFUGE);
	public static final BlockEntityType<DopperBlockEntity> DOPPER = create(DopperBlockEntity::new, YBlocks.DOPPER);
	public static final BlockEntityType<FlopperBlockEntity> FLOPPER = create(FlopperBlockEntity::new, YBlocks.FLOPPER);
	public static final BlockEntityType<SuitStationBlockEntity> SUIT_STATION = create(SuitStationBlockEntity::new, YBlocks.SUIT_STATION);
	@Renderer("SkeletalSorterBlockEntityRenderer")
	public static final BlockEntityType<SkeletalSorterBlockEntity> SKELETAL_SORTER = create(SkeletalSorterBlockEntity::new, YBlocks.SKELETAL_SORTER);
	public static final BlockEntityType<ReplicatorBlockEntity> REPLICATOR = create(ReplicatorBlockEntity::new, YBlocks.REPLICATOR);
	public static final BlockEntityType<VoidCauldronBlockEntity> VOID_CAULDRON = create(VoidCauldronBlockEntity::new, YBlocks.VOID_CAULDRON);
	public static final BlockEntityType<VoidFilterBlockEntity> VOID_FILTER = create(VoidFilterBlockEntity::new, YBlocks.VOID_FILTER);
	@Renderer("DSUBlockEntityRenderer")
	public static final BlockEntityType<DSUBlockEntity> DSU = create(DSUBlockEntity::new, YBlocks.DSU);
	public static final BlockEntityType<CanFillerBlockEntity> CAN_FILLER = create(CanFillerBlockEntity::new, YBlocks.CAN_FILLER);
	public static final BlockEntityType<ProjectTableBlockEntity> PROJECT_TABLE = create(ProjectTableBlockEntity::new, YBlocks.PROJECT_TABLE, YBlocks.DYED_PROJECT_TABLE);
	public static final BlockEntityType<LazorBeamBlockEntity> LAZOR_BEAM = create(LazorBeamBlockEntity::new, YBlocks.LAZOR_BEAM);
	public static final BlockEntityType<BloqueBlockEntity> BLOQUE = create(BloqueBlockEntity::new, YBlocks.BLOQUE);
	public static final BlockEntityType<ScreeperNestBlockEntity> SCREEPER_NEST = create(ScreeperNestBlockEntity::new, YBlocks.SCREEPER_NEST);
	public static final BlockEntityType<SSDBlockEntity> SSD = create(SSDBlockEntity::new, YBlocks.SSD);
	
	
	public static final BlockEntityType<InRedAndGateBlockEntity> INRED_AND_GATE = create(InRedAndGateBlockEntity::new, YBlocks.INRED_AND_GATE);
	public static final BlockEntityType<InRedNotGateBlockEntity> INRED_NOT_GATE = create(InRedNotGateBlockEntity::new, YBlocks.INRED_NOT_GATE);
	public static final BlockEntityType<InRedXorGateBlockEntity> INRED_XOR_GATE = create(InRedXorGateBlockEntity::new, YBlocks.INRED_XOR_GATE);
	public static final BlockEntityType<InRedDiodeBlockEntity> INRED_DIODE = create(InRedDiodeBlockEntity::new, YBlocks.INRED_DIODE);
	public static final BlockEntityType<InRedShifterBlockEntity> INRED_SHIFTER = create(InRedShifterBlockEntity::new, YBlocks.INRED_SHIFTER);
	public static final BlockEntityType<InRedTransistorBlockEntity> INRED_TRANSISTOR = create(InRedTransistorBlockEntity::new, YBlocks.INRED_TRANSISTOR);
	public static final BlockEntityType<InRedEncoderBlockEntity> INRED_ENCODER = create(InRedEncoderBlockEntity::new, YBlocks.INRED_ENCODER);
	public static final BlockEntityType<InRedOscillatorBlockEntity> INRED_OSCILLATOR = create(InRedOscillatorBlockEntity::new, YBlocks.INRED_OSCILLATOR);
	public static final BlockEntityType<InRedDemoCyclerBlockEntity> INRED_DEMO_CYCLER = create(InRedDemoCyclerBlockEntity::new, YBlocks.INRED_DEMO_CYCLER);

	private static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityFactory<T> cons, Block... acceptableBlocks) {
		return new BlockEntityType<>(cons, ImmutableSet.copyOf(acceptableBlocks), null);
	}

	public static void init() {
		Yttr.autoreg.autoRegister(Registries.BLOCK_ENTITY_TYPE, YBlockEntities.class, BlockEntityType.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Renderer {
		String value();
	}
	
}
