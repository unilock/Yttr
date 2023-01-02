package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.inred.InRedLogic;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InRedNotGateBlockEntity extends AbstractInRedDeviceBlockEntity {
	private InRedHandler signal = new InRedHandler();
	public boolean booleanMode;
	public boolean backActive;

	public InRedNotGateBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.INRED_NOT_GATE, pos, state);
	}

	@Override
	public void tick() {
		if (world.isClient || !hasWorld()) return;

		BlockState state = getCachedState();

		if (InRedLogic.isIRTick()) {
			//IR tick means we're searching for a next value
			if (state.getBlock() instanceof InRedNotGateBlock) {
				Direction back = state.get(InRedNotGateBlock.FACING).getOpposite();
				int sig = InRedLogic.findIRValue(world, pos, back);
				backActive = sig != 0;
				if (!booleanMode) {
					signal.setNextSignalValue((~sig) & 0b11_1111);
				} else {
					if (sig == 0) {
						signal.setNextSignalValue(1);
					} else {
						signal.setNextSignalValue(0);
					}
				}
				sync();
			}
		} else {
			//Not an IR tick, so this is a "copy" tick. Adopt the previous tick's "next" value.
			signal.setSignalValue(signal.getNextSignalValue());
			sync();
			//setActive(state, signal.getSignalValue()!=0); //This is also when we light up
		}
	}

	public void toggleBooleanMode() {
		if (booleanMode) {
			booleanMode = false;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.5f);
		} else {
			booleanMode = true;
			world.playSound(null, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, 0.55f);
		}
//		world.setBlockState(pos, getCachedState().with(InRedNotGateBlock.MODE, booleanMode));
		sync();
	}

	@Override
	public InRedDevice getDevice(Direction inspectingFrom) {
		if (world == null) return InRedHandler.ALWAYS_OFF;
		if (inspectingFrom == null) return  signal;

		BlockState state = getCachedState();
		if (state.getBlock() == YBlocks.INRED_NOT_GATE) {
			Direction notGateFront = state.get(InRedNotGateBlock.FACING);
			if (notGateFront == inspectingFrom) {
				return  signal;
			} else if (notGateFront == inspectingFrom.getOpposite()) {
				return InRedHandler.ALWAYS_OFF;
			} else {
				return null;
			}
		}
		return InRedHandler.ALWAYS_OFF; //We can't tell what our front face is, so supply a dummy that's always-off.
	}

	public boolean isActive() {
		return signal.getSignalValue() != 0;
	}

	@Override
	public Text getProbeMessage() {
		return Text.translatable("tip.yttr.inred.multimeter.out", getValue(signal));
	}

	@Override
	public void readNbt(NbtCompound tag) {
		if (tag.contains("Signal")) signal.deserialize(tag.getCompound("Signal"));
		booleanMode = tag.getBoolean("BooleanMode");
		backActive = tag.getBoolean("BackActive");
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		tag.put("Signal", signal.serialize());
		tag.putBoolean("BooleanMode", booleanMode);
		tag.putBoolean("BackActive", backActive);
	}

}
