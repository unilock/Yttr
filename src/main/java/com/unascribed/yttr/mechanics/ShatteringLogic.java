package com.unascribed.yttr.mechanics;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class ShatteringLogic {

	public static boolean isShattering;
	public static int shatteringDepth;
	
	public static CraftingInventory inv = new CraftingInventory(new ScreenHandler(null, -1) {
		@Override
		public boolean canUse(PlayerEntity player) {
			return false;
		}

		@Override
		public ItemStack quickTransfer(PlayerEntity player, int index) {
			return null;
		}
	}, 1, 1);
	
}
