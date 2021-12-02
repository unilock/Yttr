package com.unascribed.yttr.content.item;

import java.util.List;
import com.unascribed.yttr.mechanics.rifle.RifleMode;

import com.google.common.base.Ascii;
import com.google.common.base.Enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class AmmoCanItem extends Item implements ItemColorProvider {

	public static final int CAPACITY = 1024;
	
	public AmmoCanItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public Text getName(ItemStack stack) {
		if (!stack.hasTag()) return new TranslatableText("item.yttr.ammo_can.prefixed", new TranslatableText("multiplayer.status.unknown"));
		RifleMode mode = Enums.getIfPresent(RifleMode.class, stack.getTag().getString("Mode")).orNull();
		if (mode == null) return new TranslatableText("item.yttr.ammo_can.prefixed", new TranslatableText("multiplayer.status.unknown"));
		return new TranslatableText("item.yttr.ammo_can.prefixed", new TranslatableText("yttr.rifle_mode."+Ascii.toLowerCase(mode.name())));
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		int shots = stack.hasTag() ? stack.getTag().getInt("Shots") : 0;
		tooltip.add(new TranslatableText("item.yttr.ammo_can.shots", shots, CAPACITY).formatted(Formatting.GRAY));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return -1;
		if (!stack.hasTag()) {
			if (tintIndex == 2) return 0;
			return 0xFFFF00FF;
		}
		RifleMode mode = Enums.getIfPresent(RifleMode.class, stack.getTag().getString("Mode")).orNull();
		if (mode == null) return -1;
		float v = stack.getTag().getInt("Shots")/(float)CAPACITY;
		if (v < 0.15f) {
			// ensure nearly-empty canisters are still differentiable
			v = 0.15f;
		}
		return RifleItem.getPortionColor(tintIndex-1, 3, v, mode.color, 0xFF284946);
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (isIn(group)) {
			for (RifleMode mode : RifleMode.VALUES) {
				ItemStack stack = new ItemStack(this);
				stack.setTag(new NbtCompound());
				stack.getTag().putString("Mode", mode.name());
				stack.getTag().putInt("Shots", CAPACITY);
				stacks.add(stack);
			}
		}
	}

}