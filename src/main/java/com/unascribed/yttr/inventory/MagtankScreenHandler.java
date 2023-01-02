package com.unascribed.yttr.inventory;

import com.unascribed.yttr.init.YHandledScreens;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MagtankScreenHandler extends ScreenHandler {

	public MagtankScreenHandler(int syncId, PlayerInventory playerInv) {
		super(YHandledScreens.MAGTANK, syncId);
		YHandledScreens.addPlayerSlots(this::addSlot, playerInv, 8, 112);
		addProperties(new ArrayPropertyDelegate(2));
	}
	
	public MagtankScreenHandler(ServerWorld world, BlockPos pos, int syncId, PlayerInventory playerInv) {
		super(YHandledScreens.MAGTANK, syncId);
		
		YHandledScreens.addPlayerSlots(this::addSlot, playerInv, 8, 112);
		
		addProperties(new PropertyDelegate() {
			
			@Override
			public int size() {
				return 2;
			}
			
			@Override
			public void set(int index, int value) {
				
			}
			
			@Override
			public int get(int index) {
				return 0;
			}
		});
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack quickTransfer(PlayerEntity player, int index) {
		return ItemStack.EMPTY;
	}

}
