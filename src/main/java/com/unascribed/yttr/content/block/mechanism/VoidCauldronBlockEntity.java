package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.content.fluid.VoidFluid;
import com.unascribed.yttr.init.YBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class VoidCauldronBlockEntity extends BlockEntity implements Inventory {

	public VoidCauldronBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.VOID_CAULDRON, pos, state);
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (!stack.isEmpty()) {
			if (world instanceof ServerWorld) {
				((ServerWorld)world).spawnParticles(VoidFluid.BLACK_DUST, pos.getX()+0.5, pos.getY()+0.9, pos.getZ()+0.5, 10, 0.15, 0.1, 0.15, 0.5);
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}
	
	

}
