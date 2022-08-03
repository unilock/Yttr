package com.unascribed.yttr.util;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ItemInventory implements Inventory {
	private final ItemStack invStack;
	private final int size;

	public ItemInventory(ItemStack stack, int size) {
		this.invStack = stack;
		this.size = size;
	}

	@Override
	public void clear() {
		if (invStack.hasNbt()) invStack.getNbt().remove("Contents");
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		if (slot < 0 || slot >= size) throw new IndexOutOfBoundsException(""+slot);
		if (!invStack.hasNbt()) invStack.setNbt(new NbtCompound());
		NbtList inv = invStack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		inv.set(slot, stack.isEmpty() ? new NbtCompound() : stack.writeNbt(new NbtCompound()));
		invStack.getNbt().put("Contents", inv);
	}

	@Override
	public ItemStack getStack(int slot) {
		if (slot < 0 || slot >= size) throw new IndexOutOfBoundsException(""+slot);
		if (!invStack.hasNbt()) return ItemStack.EMPTY;
		NbtList inv = invStack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		NbtCompound comp = inv.getCompound(slot);
		if (comp.getSize() == 0) return ItemStack.EMPTY;
		return ItemStack.fromNbt(comp);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack content = getStack(slot);
		ItemStack res = content.split(amount);
		setStack(slot, content);
		return res;
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack content = getStack(slot);
		setStack(slot, ItemStack.EMPTY);
		return content;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < size() ; i++) {
			if (!getStack(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}
	
	protected static void ensureSize(NbtList li, int size) {
		if (li.size() < size) {
			for (int j = 0; j < size-li.size(); j++) {
				li.add(new NbtCompound());
			}
		}
	}
	
}