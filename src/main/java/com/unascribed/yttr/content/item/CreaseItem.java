package com.unascribed.yttr.content.item;

import com.unascribed.yttr.SpecialSubItems;
import com.unascribed.yttr.util.InventoryProviderItem;
import com.unascribed.yttr.util.ItemInventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;

public class CreaseItem extends Item implements InventoryProviderItem, SpecialSubItems {

	public CreaseItem(Settings settings) {
		super(settings);
	}

	@Override
	public Inventory asInventory(ItemStack stack) {
		return new ItemInventory(stack, 9);
	}
	
	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		return true;
	}
	
	@Override
	public boolean onClickedOnOther(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
		return true;
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> out) {
	}

}
