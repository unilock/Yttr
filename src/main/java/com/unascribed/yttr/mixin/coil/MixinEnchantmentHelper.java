package com.unascribed.yttr.mixin.coil;

import java.util.List;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.unascribed.yttr.content.enchant.CoilEnchantment;
import com.unascribed.yttr.init.YItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

	@ModifyReturnValue(at=@At("RETURN"), method="getPossibleEntries")
	private static List<EnchantmentLevelEntry> getPossibleEntries(List<EnchantmentLevelEntry> original, int power, ItemStack stack) {
		if (!YItems.CUPROSTEEL_COIL.is(stack.getItem())) {
			original.removeIf(ele -> ele != null && ele.enchantment instanceof CoilEnchantment);
		}
		return original;
	}
	
}
