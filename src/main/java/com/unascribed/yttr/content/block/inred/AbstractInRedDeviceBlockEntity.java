package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedHandler;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class AbstractInRedDeviceBlockEntity extends BlockEntity implements YTickable {

	public AbstractInRedDeviceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/*@Nullable*/
	public abstract InRedDevice getDevice(Direction inspectingFrom);

	public abstract Text getProbeMessage();

	public int getEncoderValue(Direction inspectingFrom) {
		InRedDevice device = getDevice(inspectingFrom);
		if (device instanceof InRedHandler) {
			return ((InRedHandler) device).getEncoderValue();
		}
		return 0;
	}

	public String getValue(InRedHandler handler) {
		int signal = handler.getSignalValue();
		int bit1 = ((signal & 0b00_0001) != 0) ? 1:0;
		int bit2 = ((signal & 0b00_0010) != 0) ? 1:0;
		int bit3 = ((signal & 0b00_0100) != 0) ? 1:0;
		int bit4 = ((signal & 0b00_1000) != 0) ? 1:0;
		int bit5 = ((signal & 0b01_0000) != 0) ? 1:0;
		int bit6 = ((signal & 0b10_0000) != 0) ? 1:0;
		return "0b"+bit6+bit5+"_"+bit4+bit3+bit2+bit1+" ("+signal+")";
	}
	
	@Override
	public NbtCompound toSyncedNbt() {
		return toNbt();
	}
	
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}
	
	public void sync() {
		markDirty();
		Yttr.sync(this);
		world.updateNeighborsAlways(pos, getCachedState().getBlock());
	}
}
