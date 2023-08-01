package com.unascribed.yttr.content.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Arm;

public class SkeletalSorterBlockItem extends BlockItem {

	public final Arm mainHand;
	
	public SkeletalSorterBlockItem(Block block, Arm mainHand, Settings settings) {
		super(block, settings);
		this.mainHand = mainHand;
	}
	
}
