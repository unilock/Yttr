package com.unascribed.yttr.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface InventoryProviderItem {

	Inventory asInventory(ItemStack stack);
	
}
