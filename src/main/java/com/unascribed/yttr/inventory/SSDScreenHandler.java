package com.unascribed.yttr.inventory;

import com.unascribed.yttr.content.block.device.SSDBlockEntity;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YSounds;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class SSDScreenHandler extends ScreenHandler {

	public final Inventory ssd;
	public final PropertyDelegate props;
	
	public SSDScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(8), syncId, playerInv, new ArrayPropertyDelegate(1));
	}
	
	public SSDScreenHandler(Inventory ssd, int syncId, PlayerInventory playerInv, PropertyDelegate properties) {
		super(YHandledScreens.SSD, syncId);
		this.props = properties;
		
		this.ssd = ssd;
		
		for (int i = 0; i < 8; i++) {
			final int fi = i;
			addSlot(new Slot(ssd, i, 8 + (i*18), 20) {
				@Override
				public boolean isEnabled() {
					return fi < properties.get(0);
				}
				
				@Override
				public int getMaxItemCount() {
					return SSDBlockEntity.SLOT_MAXES[fi];
				}
			});
		}
		
		YHandledScreens.addPlayerSlots(this::addSlot, playerInv, 8, 51);
		
		addProperties(properties);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return ssd.canPlayerUse(player);
	}
	
	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (ssd instanceof SSDBlockEntity be) {
			int nw = MathHelper.clamp(id, 1, 8);
			if (nw > be.size()) {
				be.getWorld().playSound(null, be.getPos(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.6f, 0.85f+(id/12f));
			} else if (nw < be.size()) {
				be.getWorld().playSound(null, be.getPos(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.6f, 0.8f+(id/12f));
			}
			if (nw != be.size()) {
				be.getWorld().playSound(null, be.getPos(), YSounds.HOLLOWSTEP, SoundCategory.BLOCKS, 0.8f, 0.6f+(id/8f));
			}
			be.setSlots(nw);
		}
		return true;
	}
	
	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		// mojang PLEASE rewrite shift clicking i am BEGGING you
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack cur = slot.getStack();
			result = cur.copy();
			if (index < 8) {
				if (!insertItem(cur, 8, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!insertItem(cur, 0, props.get(0), false)) {
				return ItemStack.EMPTY;
			}

			if (cur.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return result;
	}
	
	// modified to be aware of slot maxes
	@Override
	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean bl = false;
		int i = startIndex;
		if (fromLast) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (!stack.isEmpty()) {
				if (fromLast) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				Slot slot = this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				int max = slot.getMaxItemCount(itemStack);
				if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
					int j = itemStack.getCount() + stack.getCount();
					if (j <= max) {
						stack.setCount(0);
						itemStack.setCount(j);
						slot.markDirty();
						bl = true;
					} else if (itemStack.getCount() < max) {
						stack.decrement(max - itemStack.getCount());
						itemStack.setCount(max);
						slot.markDirty();
						bl = true;
					}
				}

				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while(true) {
				if (fromLast) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				Slot slot = this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				int max = slot.getMaxItemCount(itemStack);
				if (itemStack.isEmpty() && slot.canInsert(stack)) {
					if (stack.getCount() > max) {
						slot.setStack(stack.split(max));
					} else {
						slot.setStack(stack.split(stack.getCount()));
					}

					slot.markDirty();
					bl = true;
					break;
				}

				if (fromLast) {
					--i;
				} else {
					++i;
				}
			}
		}

		return bl;
	}
	
}
