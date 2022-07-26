package com.unascribed.yttr.content.item.block;

import com.unascribed.yttr.content.block.inred.InRedScaffoldBlock;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class InRedCableBlockItem extends BlockItem {

	public InRedCableBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos scaffold = null;
		if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SCAFFOLDING) {
			scaffold = context.getBlockPos();
		} else {
			BlockPos ofs = context.getBlockPos().offset(context.getSide());
			if (context.getWorld().getBlockState(ofs).getBlock() == Blocks.SCAFFOLDING) {
				scaffold = ofs;
			}
		}
		if (scaffold != null) {
			BlockState bs = context.getWorld().getBlockState(scaffold);
			ItemPlacementContext ipc = getPlacementContext(new ItemPlacementContext(context));
			context.getWorld().setBlockState(scaffold, YBlocks.INRED_SCAFFOLD.getPlacementState(ipc)
					.with(InRedScaffoldBlock.BOTTOM, bs.get(ScaffoldingBlock.BOTTOM))
					.with(InRedScaffoldBlock.DISTANCE, bs.get(ScaffoldingBlock.DISTANCE))
					.with(InRedScaffoldBlock.WATERLOGGED, bs.get(ScaffoldingBlock.WATERLOGGED)));
			if (!context.getPlayer().isCreative()) {
				context.getStack().decrement(1);
			}
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(context);
	}
	
}
