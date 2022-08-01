package com.unascribed.yttr.content.item;

import java.util.List;

import com.unascribed.yttr.inventory.AmmoPackScreenHandler;
import com.unascribed.yttr.util.InventoryProviderItem;
import com.unascribed.yttr.util.ItemInventory;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
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

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		Inventory inv = asInventory(stack);
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
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
	
	@Override
	public Inventory asInventory(ItemStack pack) {
		return new ItemInventory(pack, 6);
	}

}
