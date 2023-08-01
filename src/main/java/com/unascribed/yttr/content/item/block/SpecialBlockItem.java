package com.unascribed.yttr.content.item.block;

import com.unascribed.yttr.SpecialSubItems;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public abstract class SpecialBlockItem extends BlockItem implements SpecialSubItems {

	public SpecialBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

}
