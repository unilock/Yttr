package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class PowerMeterBlockEntity extends BlockEntity {

	public int readout;
	public long readoutTime = 0;
	
	public PowerMeterBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.POWER_METER, pos, state);
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		readout = (type << 8) | data;
		readoutTime = System.currentTimeMillis();
		return true;
	}

	public void sendReadout(int readout) {
		this.readout = readout;
		this.readoutTime = System.currentTimeMillis();
		getWorld().addSyncedBlockEvent(getPos(), YBlocks.POWER_METER, ((readout >> 8)&0xFF), readout&0xFF);
		getWorld().updateNeighborsAlways(pos, YBlocks.POWER_METER);
		getWorld().scheduleBlockTick(pos, getCachedState().getBlock(), 4*20);
	}
	

}
