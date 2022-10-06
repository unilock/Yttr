package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class SSDBlockEntity extends BlockEntity implements Nameable, DelegatingInventory, SideyInventory {

	public static final int[] SLOT_MAXES = {
		4, 4, 8, 16, 32, // adds up to 64
		64, 64, 64
	};
	
	private final SimpleInventory inv = new SimpleInventory(8);
	private int slots = 8;
	
	public SSDBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.SSD, pos, state);
		inv.addListener(i -> markDirty());
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putByte("Slots", (byte)slots);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		slots = MathHelper.clamp(tag.getByte("Slots"), 1, 8);
	}
	
	public void setSlots(int slots) {
		this.slots = slots;
		markDirty();
	}
	
	@Override
	public Inventory getDelegateInv() {
		return inv;
	}
	
	@Override
	public int size() {
		return slots;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return player.squaredDistanceTo(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) < 8*8;
	}
	
	@Override
	public Text getName() {
		return Text.translatable("block.yttr.ssd");
	}

	@Override
	public boolean canAccess(int slot, Direction side) {
		return slot < slots;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return slot < slots && getStack(slot).getCount() < SLOT_MAXES[slot];
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot < slots;
	}

}
