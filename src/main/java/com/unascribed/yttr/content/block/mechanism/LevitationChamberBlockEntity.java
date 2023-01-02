package com.unascribed.yttr.content.block.mechanism;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;
import com.unascribed.yttr.util.YTickable;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LevitationChamberBlockEntity extends BlockEntity implements YTickable, SideyInventory, NamedScreenHandlerFactory, DelegatingInventory {

	private final SimpleInventory inv = new SimpleInventory(5);
	
	public int age;
	private int cooldown = 0;
	
	public LevitationChamberBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.LEVITATION_CHAMBER, pos, state);
		inv.addListener((i) -> markDirty());
	}
	
	@Override
	public void tick() {
		age++;
		if (cooldown-- <= 0 && !isEmpty()) {
			for (int i = 0; i < size(); i++) {
				ItemStack is = getStack(i);
				if (!is.isEmpty()) {
					ItemStack transfer = is.copy();
					transfer.setCount(1);
					if (ChuteBlockEntity.transfer(world, pos, Direction.UP, transfer, false)) {
						is.decrement(1);
						setStack(i, is);
						cooldown = 4;
						break;
					}
				}
			}
		}
	}

	@Override
	public void readNbt(NbtCompound tag) {
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("Cooldown", cooldown);
	}
	
	@Override
	public Inventory getDelegateInv() {
		return inv;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return inv.canPlayerUse(player);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return dir == Direction.UP;
	}
	
	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return true;
	}
	
	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
	}
	
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new HopperScreenHandler(syncId, inv, this);
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("block.yttr.levitation_chamber");
	}
	
}
