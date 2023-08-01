package com.unascribed.yttr.init;

import java.util.ArrayList;
import com.unascribed.yttr.SpecialSubItems;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroup.DisplayParameters;
import net.minecraft.item.ItemGroup.ItemStackCollector;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class YItemGroups {

	public static final ItemGroup GENERAL = FabricItemGroup.builder()
			.entries((params, out) -> buildEntries(YItemGroups.GENERAL, params, out))
			.icon(() -> YItems.LOGO.getDefaultStack())
			.name(Text.translatable("itemGroup.yttr"))
			.build();

	public static final ItemGroup LAMPS = FabricItemGroup.builder()
			.entries((params, out) -> buildEntries(YItemGroups.LAMPS, params, out))
			.icon(() -> {
				var is = new ItemStack(YItems.LAMP);
				LampBlockItem.setColor(is, LampColor.RED);
				LampBlockItem.setInverted(is, true);
				return is;
			})
			.name(Text.translatable("itemGroup.yttr.lamp"))
			.build();

	public static final ItemGroup SNARES = FabricItemGroup.builder()
			.entries((params, out) -> buildEntries(YItemGroups.SNARES, params, out))
			.icon(() -> YItems.SNARE.getDefaultStack())
			.name(Text.translatable("itemGroup.yttr.snare"))
			.build();

	public static void buildEntries(ItemGroup group, DisplayParameters params, ItemStackCollector out) {
		// use our class so we keep order
		var lili = new ArrayList<DefaultedList<ItemStack>>();
		Yttr.autoreg.eachRegisterableField(YItems.class, Item.class, null, (f, t, a) -> {
			try {
				var i = (Item)f.get(null);
				var li = DefaultedList.<ItemStack>of();
				if (i instanceof BlockItem bi && bi.getBlock() instanceof SpecialSubItems ssi) {
					ssi.appendStacks(group, li);
				} else if (i instanceof SpecialSubItems ssi) {
					ssi.appendStacks(group, li);
				} else if (group == GENERAL) {
					li.add(new ItemStack(i));
				}
				lili.add(li);
			} catch (Throwable e) {}
		});
		for (var li : lili) {
			for (var is : li) {
				try {
					out.addStack(is);
				} catch (IllegalStateException e) {}
			}
		}
	}
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ITEM_GROUP, YItemGroups.class, ItemGroup.class);
	}
	
}
