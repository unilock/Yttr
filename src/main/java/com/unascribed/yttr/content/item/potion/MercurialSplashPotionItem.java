package com.unascribed.yttr.content.item.potion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class MercurialSplashPotionItem extends SplashPotionItem implements ItemColorProvider {

	public MercurialSplashPotionItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		return Text.translatable("item.yttr.mercurial_potion.prefix", Items.SPLASH_POTION.getName(stack));
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		return Items.SPLASH_POTION.getTranslationKey(stack);
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		return tintIndex == 0 ? PotionUtil.getColor(stack) : -1;
	}

}
