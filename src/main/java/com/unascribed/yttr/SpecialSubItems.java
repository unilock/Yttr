package com.unascribed.yttr;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface SpecialSubItems {

	void appendStacks(ItemGroup group, DefaultedList<ItemStack> out);
	
}
