package com.unascribed.yttr.content.block.lazor;

import java.util.List;

import com.unascribed.yttr.SpecialSubItems;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class LazorEmitterBlock extends AbstractColoredLazorBlock implements SpecialSubItems {

	public LazorEmitterBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected boolean isEmitter() {
		return true;
	}
	
	private ItemStack getDrop(BlockState state) {
		ItemStack is = new ItemStack(this);
		LampBlockItem.setColor(is, state.get(COLOR));
		return is;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
		return Lists.newArrayList(getDrop(state));
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getDrop(state);
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		if (group == YItemGroups.LAMPS) {
			for (LampColor color : LampColor.VALUES) {
				ItemStack stack = new ItemStack(this);
				LampBlockItem.setColor(stack, color);
				list.add(stack);
			}
		}
	}
}
