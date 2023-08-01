package com.unascribed.yttr.mixinsupport;

import com.unascribed.yttr.Substitutes;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class UltrapureBonus {

	public static void handleCraft(RecipeInputInventory inv, ItemStack out) {
		if (!out.isDamageable()) return;
		boolean anyPure = false;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (Substitutes.getPrime(stack.getItem()) != null) {
				// this is an ultrapure resource
				anyPure = true;
			} else if (Substitutes.getSubstitute(stack.getItem()) != null) {
				// this is an impure resource
				return;
			}
		}
		if (anyPure) {
			if (!out.hasCustomName()) {
				out.setCustomName(Text.translatable("item.yttr.ultrapure_tool.prefix", out.getName()).setStyle(Style.EMPTY.withItalic(false)));
			}
			if (!out.hasNbt()) {
				out.setNbt(new NbtCompound());
			}
			out.getNbt().putInt("yttr:DurabilityBonus", out.getNbt().getInt("yttr:DurabilityBonus")+1);
			out.getNbt().putBoolean("yttr:Ultrapure", true);
		}
	}
	
}
