package com.unascribed.yttr.init.conditional;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.enchant.SpringingEnchantment;
import com.unascribed.yttr.content.enchant.StabilizationEnchantment;
import com.unascribed.yttr.content.item.AmmoPackItem;
import com.unascribed.yttr.content.item.CuprosteelCoilItem;
import com.unascribed.yttr.content.item.PlatformsItem;
import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class YTrinkets {

	public static final String SOLE = "sole";
	
	private static final CuprosteelCoilItem CUPROSTEEL_COIL = new CuprosteelCoilItem(new Item.Settings().maxDamage(896));
	private static final AmmoPackItem AMMO_PACK = new AmmoPackItem(new Item.Settings().maxCount(1));
	private static final PlatformsItem PLATFORMS = new PlatformsItem(new Item.Settings().maxCount(1));
	
	private static final SpringingEnchantment SPRINGING = new SpringingEnchantment();
	private static final StabilizationEnchantment STABILIZATION = new StabilizationEnchantment();
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ITEM, YTrinkets.class, Item.class);
		Yttr.autoreg.autoRegister(Registries.ENCHANTMENT, YTrinkets.class, Enchantment.class);
		
		YItems.CUPROSTEEL_COIL.set(CUPROSTEEL_COIL);
		YItems.AMMO_PACK.set(AMMO_PACK);
		YItems.PLATFORMS.set(PLATFORMS);
		
		YEnchantments.SPRINGING.set(SPRINGING);
		YEnchantments.STABILIZATION.set(STABILIZATION);
	}
	
}
