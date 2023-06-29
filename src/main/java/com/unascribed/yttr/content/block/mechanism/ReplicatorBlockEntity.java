package com.unascribed.yttr.content.block.mechanism;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.client.render.ReplicatorRenderer;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.util.SideyInventory;
import com.unascribed.yttr.util.YTickable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class ReplicatorBlockEntity extends BlockEntity implements SideyInventory, YTickable {

	public int seed = ThreadLocalRandom.current().nextInt();
	public ItemStack item = ItemStack.EMPTY;
	public UUID owner;
	public boolean locked;
	
	public double distTmp;
	
	public int clientAge = 0;
	public int removedTicks = 0;
	
	private boolean addedClient = false;
	
	public ReplicatorBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.REPLICATOR, pos, state);
	}
	
	@Override
	public void tick() {
		if (world != null && !world.isClient && world.getTime() % 20 == 0) {
			var below = world.getBlockState(pos.down());
			if (below.isOf(YBlocks.CHUTE) && below.get(ChuteBlock.MODE).isDroppy()) {
				Box box = new Box(pos, pos).expand(0.5).stretch(0, -12, 0);
				int count = 0;
				for (var ie : world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), box, ie -> ItemStack.canCombine(item, ie.getStack()))) {
					count += ie.getStack().getCount();
				}
				if (count < item.getCount()) {
					var copy = item.copy();
					copy.setCount(item.getCount()-count);
					ChuteBlockEntity.transfer(world, pos.down(), Direction.DOWN, copy, false);
				}
			}
		}
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void clientTick() {
		clientAge++;
		if (isRemoved()) {
			addedClient = false;
			removedTicks++;
		} else if (!addedClient) {
			addedClient = true;
			ReplicatorRenderer.notifyCreated(this);
		}
	}
	
	@Override
	public void markRemoved() {
		super.markRemoved();
		if (world.isClient) {
			removeClient();
		}
	}
	
	@Environment(EnvType.CLIENT)
	private void removeClient() {
		ReplicatorRenderer.replicators.remove(this);
		ReplicatorRenderer.removing.add(this);
	}

	@Override
	public void readNbt(NbtCompound tag) {
		seed = tag.getInt("Seed");
		item = ItemStack.fromNbt(tag.getCompound("Item"));
		owner = tag.containsUuid("Owner") ? tag.getUuid("Owner") : null;
		locked = tag.getBoolean("Locked");
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.putInt("Seed", seed);
		tag.put("Item", item.writeNbt(new NbtCompound()));
		if (owner != null) tag.putUuid("Owner", owner);
		if (locked) tag.putBoolean("Locked", true);
	}
	
	@Override
	public NbtCompound toSyncedNbt() {
		NbtCompound tag = super.toSyncedNbt();
		writeNbt(tag);
		tag.remove("Owner");
		return tag;
	}
	
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return item.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		ItemStack copy = item.copy();
		copy.setCount(copy.getMaxCount());
		return copy;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack copy = item.copy();
		copy.setCount(amount);
		return copy;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return item.copy();
	}

	@Override
	public void setStack(int slot, ItemStack stack) {}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
	}

}
