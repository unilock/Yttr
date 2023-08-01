package com.unascribed.yttr.inventory;

import java.util.Optional;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YHandledScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ProjectTableScreenHandler extends AbstractRecipeScreenHandler<CraftingInventory> {
	
	private final CraftingInventory input;
	private final CraftingResultInventory result;
	private final Inventory inv;
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	
	public ProjectTableScreenHandler(int syncId, PlayerInventory playerInventory) {
		this(syncId, new SimpleInventory(27), playerInventory, ScreenHandlerContext.EMPTY);
	}

	public ProjectTableScreenHandler(int syncId, Inventory inv, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(YHandledScreens.PROJECT_TABLE, syncId);
		this.context = context;
		this.player = playerInventory.player;
		this.inv = inv;
		this.input = new CraftingInventory(this, 3, 3) {
			@Override
			public int size() {
				return 9;
			}

			@Override
			public boolean isEmpty() {
				for (int i = 0; i < size(); i++) {
					if (!getStack(i).isEmpty()) return false;
				}
				return true;
			}

			@Override
			public ItemStack getStack(int slot) {
				return slot >= size() ? ItemStack.EMPTY : inv.getStack(slot);
			}

			@Override
			public ItemStack removeStack(int slot) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				return inv.removeStack(slot);
			}

			@Override
			public ItemStack removeStack(int slot, int amount) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				ItemStack is = inv.removeStack(slot, amount);
				if (!is.isEmpty()) {
					onContentChanged(this);
				}
				return is;
			}

			@Override
			public void setStack(int slot, ItemStack stack) {
				if (slot >= 9) throw new IndexOutOfBoundsException();
				inv.setStack(slot, stack);
				onContentChanged(this);
			}

			@Override
			public void markDirty() {
				inv.markDirty();
			}

			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return inv.canPlayerUse(player);
			}

			@Override
			public void clear() {
				for (int i = 0; i < size(); i++) {
					removeStack(i);
				}
			}
		};
		result = new CraftingResultInventory();
		
		addSlot(new CraftingResultSlot(player, input, result, 0, 124, 35));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new Slot(input, x + y * 3, 30 + x * 18, 17 + y * 18));
			}
		}

		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		
		YHandledScreens.addPlayerSlots(this::addSlot, playerInventory, 8, 133);
		
		updateResult(syncId, player.getWorld(), player, input, result, this);

	}

	protected static void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory, ScreenHandler sh) {
		if (!world.isClient) {
			ServerPlayerEntity spe = (ServerPlayerEntity)player;
			ItemStack res = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()) {
				CraftingRecipe craftingRecipe = optional.get();
				if (resultInventory.shouldCraftRecipe(world, spe, craftingRecipe)) {
					res = craftingRecipe.craft(craftingInventory, world.getRegistryManager());
				}
			}

			resultInventory.setStack(0, res);
			spe.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, sh.nextRevision(), 0, res));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		context.run((world, blockPos) -> {
			updateResult(syncId, world, player, input, result, this);
		});
	}

	@Override
	public void populateRecipeFinder(RecipeMatcher finder) {
		for (int i = 0; i < inv.size(); i++) {
			finder.addUnenchantedInput(inv.getStack(i));
		}
	}
	
	@Override
	public void fillInputSlots(boolean craftAll, Recipe<?> recipe, ServerPlayerEntity player) {
		new InputSlotFiller<CraftingInventory>(this) {
			@Override
			protected void fillInputSlot(Slot slot, ItemStack stack) {
				// scan storage, but not grid
				for (int i = 9; i < inv.size(); i++) {
					ItemStack is = inv.getStack(i);
					if (!is.isEmpty() && ItemStack.canCombine(stack, is) && !is.isDamaged() && !is.hasEnchantments() && !is.hasCustomName()) {
						is = is.copy();
						if (is.getCount() > 1) {
							inv.removeStack(i, 1);
						} else {
							inv.removeStack(i);
						}

						is.setCount(1);
						if (slot.getStack().isEmpty()) {
							slot.setStack(is);
						} else {
							slot.getStack().increment(1);
						}
						return;
					}
				}
				// handle player inventory
				super.fillInputSlot(slot, stack);
			}
			
			@Override
			protected void returnInputs() {
				for(int i = 0; i < handler.getCraftingSlotCount(); i++) {
					if (handler.canInsertIntoSlot(i)) {
						ItemStack is = handler.getSlot(i).getStack().copy();
						for (int j = 9; j < inv.size(); j++) {
							if (is.isEmpty()) break;
							ItemStack cur = inv.getStack(j);
							if (cur.isEmpty()) {
								inv.setStack(j, is);
								is = ItemStack.EMPTY;
							} else if (ItemStack.canCombine(is, cur)) {
								int amt = Math.min(cur.getMaxCount()-cur.getCount(), is.getCount());
								is.decrement(amt);
								cur.increment(amt);
							}
						}
						if (!is.isEmpty()) {
							inventory.offer(is, false);
						}
						handler.getSlot(i).setStack(is);
					}
				}

				handler.clearCraftingSlots();
			}
		}.fillInputSlots(player, (Recipe<CraftingInventory>)recipe, craftAll);

	}

	@Override
	public void clearCraftingSlots() {
		input.clear();
		result.clear();
	}

	@Override
	public boolean matches(Recipe<? super CraftingInventory> recipe) {
		return recipe.matches(input, player.getWorld());
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(context, player, YBlocks.PROJECT_TABLE) || canUse(context, player, YBlocks.DYED_PROJECT_TABLE);
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (index == 0) {
				this.context.run((world, blockPos) -> {
					itemStack2.getItem().onCraft(itemStack2, world, player);
				});
				if (!this.insertItem(itemStack2, 28, 63, false)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickTransfer(itemStack2, itemStack);
			} else if (index >= 28 && index <= 63) {
				if (!this.insertItem(itemStack2, 10, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 28, 63, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.result && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.input.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.input.getHeight();
	}

	@Override
	public int getCraftingSlotCount() {
		return 10;
	}

	@Override
	public RecipeBookCategory getCategory() {
		return RecipeBookCategory.CRAFTING;
	}

	@Override
	public boolean canInsertIntoSlot(int index) {
		return index != this.getCraftingResultSlotIndex();
	}
}
