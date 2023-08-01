package com.unascribed.yttr.inventory;

import java.util.function.Predicate;

import com.unascribed.yttr.content.block.device.CanFillerBlockEntity;
import com.unascribed.yttr.init.YHandledScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class CanFillerScreenHandler extends ScreenHandler {

	private final World world;
	
	private final Inventory canFiller;
	private final PropertyDelegate properties;
	
	private static class CFSlot extends Slot {

		private final Predicate<ItemStack> canInsert;
		
		public CFSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> canInsert) {
			super(inventory, index, x, y);
			this.canInsert = canInsert;
		}
		
		@Override
		public boolean canInsert(ItemStack stack) {
			return canInsert.test(stack);
		}
		
	}
	
	public CanFillerScreenHandler(int syncId, PlayerInventory playerInv) {
		this(new SimpleInventory(5), syncId, playerInv, new ArrayPropertyDelegate(4));
	}
	
	public CanFillerScreenHandler(Inventory canFiller, int syncId, PlayerInventory playerInv, PropertyDelegate properties) {
		super(YHandledScreens.CAN_FILLER, syncId);
		world = playerInv.player.getWorld();
		
		this.canFiller = canFiller;
		this.properties = properties;
		
		addSlot(new CFSlot(canFiller, 0, 33, 20, CanFillerBlockEntity::isRifleAmmo));
		addSlot(new CFSlot(canFiller, 1, 127, 20, CanFillerBlockEntity::isPropellant));
		addSlot(new CFSlot(canFiller, 2, 80, 40, CanFillerBlockEntity::isCan));
		addSlot(new CFSlot(canFiller, 3, 71, 86, is -> false));
		addSlot(new CFSlot(canFiller, 4, 89, 86, is -> false));
		
		YHandledScreens.addPlayerSlots(this::addSlot, playerInv, 8, 119);
		
		addProperties(properties);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canFiller.canPlayerUse(player);
	}
	
	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		ItemStack out = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			out = slotStack.copy();
			if (index >= 0 && index < 5) {
				// try to put outputs into the player's inventory
				if (!insertItem(slotStack, 5, 41, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickTransfer(slotStack, out);
			} else if (index >= 5) {
				if (CanFillerBlockEntity.isPropellant(slotStack)) {
					if (!insertItem(slotStack, 1, 2, true)) {
						return ItemStack.EMPTY;
					}
				} else if (CanFillerBlockEntity.isRifleAmmo(slotStack)) {
					if (!insertItem(slotStack, 0, 1, true)) {
						return ItemStack.EMPTY;
					}
				} else if (CanFillerBlockEntity.isCan(slotStack)) {
					if (!insertItem(slotStack, 2, 3, true)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!insertItem(slotStack, 5, 41, false)) {
				// move anything else to player inventory
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (slotStack.getCount() == out.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, slotStack);
		}

		return out;
	}

	public int getWorkTime() {
		return properties.get(0);
	}
	
	public int getMaxWorkTime() {
		return properties.get(1);
	}
}
