package com.unascribed.yttr.content.item;

import com.unascribed.yttr.content.fluid.VoidFluid;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;

public class VoidBucketItem extends BucketItem {

	public VoidBucketItem(Settings settings) {
		super(YFluids.VOID, settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		BlockState bs = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (bs.isOf(Blocks.CAULDRON)) {
			ctx.getWorld().setBlockState(ctx.getBlockPos(), YBlocks.VOID_CAULDRON.getDefaultState());
			ctx.getPlayer().setStackInHand(ctx.getHand(), new ItemStack(Items.BUCKET));
			ctx.getWorld().playSound(null, ctx.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1, 1);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		// a left-click probably means they intend to swap items, not destroy them
		if (clickType == ClickType.LEFT) return false;
		
		if (!canDestroy(otherStack)) return false;
		player.playSound(YSounds.DISSOLVE, 0.7f, 1);
		if (otherStack.isOf(Items.BUNDLE)) {
			player.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.7f, 1);
			otherStack.removeSubNbt("Items");
		} else {
			otherStack.setCount(0);
		}
		return true;
	}

	public static boolean canDestroy(ItemStack stack) {
		if (stack.isOf(Items.BUNDLE)) {
			return stack.hasNbt() && stack.getNbt().contains("Items");
		}
		return !stack.isEmpty() && !stack.isIn(YTags.Item.VOID_IMMUNE);
	}

}
