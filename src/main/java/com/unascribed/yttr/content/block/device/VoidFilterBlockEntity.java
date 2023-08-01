package com.unascribed.yttr.content.block.device;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.lib39.mesh.api.BlockNetworkManager;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.crafting.VoidFilteringRecipe;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.util.DelegatingInventory;
import com.unascribed.yttr.util.SideyInventory;
import com.unascribed.yttr.util.YTickable;
import com.unascribed.yttr.world.network.FilterNetwork;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VoidFilterBlockEntity extends BlockEntity implements YTickable, DelegatingInventory, SideyInventory {
	
	public static final int TICKS_PER_TOCK = 100;
	public static final int TOCKS_PER_OP = (20*60)/TICKS_PER_TOCK;
	
	public static final int MAX_PRESSURE = 72000;
	
	private static final Multiset<Item> statQueue = HashMultiset.create();
	
	private final SimpleInventory inv = new SimpleInventory(9);
	private int tockProgress = 0;
	private int opTocks = 0;
	private int maxOpTocks = TOCKS_PER_OP;
	private UUID owner;
	private int pressure;
	
	private final PropertyDelegate properties = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0: return (opTocks*TICKS_PER_TOCK)+tockProgress;
				case 1: return maxOpTocks*TICKS_PER_TOCK;
				case 2: return getCachedState().get(VoidFilterBlock.INDEPENDENT) ? 1 : 0;
				case 3: return pressure;
				case 4: return MAX_PRESSURE;
				default: return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch(index) {
				case 0: opTocks = value/TICKS_PER_TOCK; break;
				case 1: maxOpTocks = value/TICKS_PER_TOCK; break;
			}

		}

		@Override
		public int size() {
			return 4;
		}
	};
	
	public VoidFilterBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.VOID_FILTER, pos, state);
		inv.addListener(i -> markDirty());
	}
	
	@Override
	public void tick() {
		if (world.isClient) return;
		if (world instanceof ServerWorld sw) {
			pressure = BlockNetworkManager.get(sw).getNetworkAt(FilterNetwork.TYPE, pos).map(n -> ((FilterNetwork)n).getPressure()).orElse(0);
		}
		if (!getCachedState().get(VoidFilterBlock.INDEPENDENT)) {
			opTocks = 0;
			tockProgress = 0;
			return;
		}
		if (tockProgress++ >= TICKS_PER_TOCK) {
			tockProgress = 0;
		} else {
			return;
		}
		if (!world.getBlockState(pos.down()).isOf(YBlocks.VOID_GEYSER) && !world.getBlockState(pos.down()).isOf(YBlocks.DORMANT_VOID_GEYSER)) return;
		boolean invFull = true;
		for (int i = 0; i < size(); i++) {
			if (getStack(i).isEmpty()) {
				invFull = false;
				break;
			}
		}
		if (!invFull && opTocks++ >= maxOpTocks) {
			opTocks = 0;
			for (VoidFilteringRecipe r : world.getRecipeManager().listAllOfType(YRecipeTypes.VOID_FILTERING)) {
				if (ThreadLocalRandom.current().nextFloat()*100 < r.getChance()) {
					ItemStack res = r.craft(this, world.getRegistryManager());
					if (inv.addStack(res).isEmpty()) {
						statQueue.add(res.getItem(), res.getCount());
					}
				}
			}
			if (!statQueue.isEmpty() && owner != null) {
				ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(owner);
				if (player != null) {
					for (Multiset.Entry<Item> en : statQueue.entrySet()) {
						player.getStatHandler().increaseStat(player, Stats.CRAFTED.getOrCreateStat(en.getElement()), en.getCount());
					}
					statQueue.clear();
				}
			}
		}
		markDirty();
	}
	
	public PropertyDelegate getProperties() {
		return properties;
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		tag.put("Inventory", Yttr.serializeInv(inv));
		tag.putInt("OpTocks", opTocks);
		if (owner != null) tag.putUuid("Owner", owner);
		NbtCompound statQTag = new NbtCompound();
		for (Multiset.Entry<Item> en : statQueue.entrySet()) {
			statQTag.putInt(Registries.ITEM.getId(en.getElement()).toString(), en.getCount());
		}
		tag.put("StatQueue", statQTag);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		Yttr.deserializeInv(tag.getList("Inventory", NbtType.COMPOUND), inv);
		opTocks = tag.getInt("OpTocks");
		owner = tag.containsUuid("Owner") ? tag.getUuid("Owner") : null;
		statQueue.clear();
		NbtCompound statQTag = tag.getCompound("StatQueue");
		for (String key : statQTag.getKeys()) {
			Item i = Registries.ITEM.getOrEmpty(Identifier.tryParse(key)).orElse(null);
			if (i != null) {
				statQueue.add(i, statQTag.getInt(key));
			}
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.getSquaredDistanceToCenter(player.getPos()) < 5*5;
	}

	@Override
	public boolean canAccess(int slot, Direction side) {
		return true;
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
	public Inventory getDelegateInv() {
		return inv;
	}
	
	@Override
	public void onOpen(PlayerEntity player) {
		DelegatingInventory.super.onOpen(player);
		if (owner == null) {
			owner = player.getUuid();
			markDirty();
		}
	}

}
