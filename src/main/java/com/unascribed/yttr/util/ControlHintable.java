package com.unascribed.yttr.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface ControlHintable {

	String getState(PlayerEntity player, ItemStack stack, boolean fHeld);
	
}
