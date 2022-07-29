package com.unascribed.yttr.content.item;

import java.util.List;

import com.unascribed.yttr.inventory.AmmoPackScreenHandler;
import com.unascribed.yttr.util.InventoryProviderItem;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class AmmoPackItem extends TrinketItem implements InventoryProviderItem {

	public AmmoPackItem(Settings settings) {
		super(settings);
	}
	
	public int getSize(ItemStack pack) {
		return 6;
	}
	
	public void setStack(ItemStack pack, int i, ItemStack stack) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasNbt()) pack.setNbt(new NbtCompound());
		NbtList inv = pack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		inv.set(i, stack.isEmpty() ? new NbtCompound() : stack.writeNbt(new NbtCompound()));
		pack.getNbt().put("Contents", inv);
	}

	public ItemStack getStack(ItemStack pack, int i) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasNbt()) return ItemStack.EMPTY;
		NbtList inv = pack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		NbtCompound comp = inv.getCompound(i);
		if (comp.getSize() == 0) return ItemStack.EMPTY;
		return ItemStack.fromNbt(comp);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		for (int i = 0; i < getSize(stack); i++) {
			ItemStack is = getStack(stack, i);
			if (!is.isEmpty()) {
				tooltip.add(is.getName());
				is.getItem().appendTooltip(is, world, tooltip, context);
			}
		}
	}
	
	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		if (clickType == ClickType.RIGHT && cursorStackReference.get().isEmpty()) {
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new AmmoPackScreenHandler(asInventory(stack), syncId, inv);
				}
				
				@Override
				public Text getDisplayName() {
					return stack.getName();
				}
			});
			return true;
		}
		return false;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isSneaking()) {
			ItemStack stack = player.getStackInHand(hand);
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new AmmoPackScreenHandler(asInventory(stack), syncId, inv);
				}
				
				@Override
				public Text getDisplayName() {
					return stack.getName();
				}
			});
			return TypedActionResult.consume(stack);
		}
		return super.use(world, player, hand);
	}
	
	public void clear(ItemStack pack) {
		if (pack.hasNbt()) pack.getNbt().remove("Contents");
	}
	
	@Override
	public Inventory asInventory(ItemStack pack) {
		return new Inventory() {
			
			@Override
			public void clear() {
				AmmoPackItem.this.clear(pack);
			}
			
			@Override
			public int size() {
				return AmmoPackItem.this.getSize(pack);
			}
			
			@Override
			public void setStack(int slot, ItemStack stack) {
				AmmoPackItem.this.setStack(pack, slot, stack);
			}
			
			@Override
			public ItemStack getStack(int slot) {
				return AmmoPackItem.this.getStack(pack, slot);
			}
			
			@Override
			public ItemStack removeStack(int slot, int amount) {
				ItemStack content = getStack(slot);
				ItemStack res = content.split(amount);
				setStack(slot, content);
				return res;
			}
			
			@Override
			public ItemStack removeStack(int slot) {
				ItemStack content = getStack(slot);
				setStack(slot, ItemStack.EMPTY);
				return content;
			}
			
			@Override
			public void markDirty() {
			}
			
			@Override
			public boolean isEmpty() {
				for (int i = 0; i < size() ; i++) {
					if (!getStack(i).isEmpty()) return false;
				}
				return true;
			}
			
			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return true;
			}
		};
	}
	
	protected static void ensureSize(NbtList li, int size) {
		if (li.size() < size) {
			for (int j = 0; j < size-li.size(); j++) {
				li.add(new NbtCompound());
			}
		}
	}

}
