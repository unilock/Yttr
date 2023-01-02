package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mixin.accessor.AccessorHopperBlockEntity;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DopperBlockEntity extends HopperBlockEntity implements YTickable {

	private boolean tock = false;

	public DopperBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}
	
	@Override
	public void tick() {
		if (world != null && !world.isClient) {
			AccessorHopperBlockEntity acc = (AccessorHopperBlockEntity)this;
			acc.yttr$setTransferCooldown(acc.yttr$getTransferCooldown()-1);
			acc.yttr$setLastTickTime(world.getTime());
			if (!acc.yttr$needsCooldown()) {
				acc.yttr$setTransferCooldown(0);
				BlockState state = getCachedState();
				if (tock) {
					state = state.with(DopperBlock.FACING, state.get(DopperBlock.FACING).getOpposite());
				}
				if (AccessorHopperBlockEntity.yttr$insertAndExtract(getWorld(), pos, state, this, () -> extract(getWorld(), this))) {
					tock = !tock;
				}
			}
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.putBoolean("Tock", tock);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		tock = tag.getBoolean("Tock");
	}
	
	@Override
	public BlockEntityType<?> getType() {
		return YBlockEntities.DOPPER;
	}
	
	@Override
	protected Text getContainerName() {
		return Text.translatable("block.yttr.dopper");
	}
	
}
