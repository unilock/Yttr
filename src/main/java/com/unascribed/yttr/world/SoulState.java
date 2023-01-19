package com.unascribed.yttr.world;

import java.util.UUID;

import com.unascribed.yttr.util.math.Bits;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class SoulState extends PersistentState {

	public static final UUID FRAGMENTATION_MODIFIER = UUID.fromString("52f41157-796b-43ec-ab89-18f1c9c6c15c");
	public static final UUID IMPURITY_MODIFIER = UUID.fromString("3dd603e4-3526-4658-aeeb-c40cafbf5a60");

	public static SoulState get(ServerWorld world) {
		return world.getServer().getOverworld().getPersistentStateManager().getOrCreate(SoulState::fromNbt, SoulState::new, "yttr_soul");
	}

	public static SoulState fromNbt(NbtCompound tag) {
		SoulState ret = new SoulState();
		ret.readNbt(tag);
		return ret;
	}
	
	private final Multiset<UUID> fragmentation = HashMultiset.create();
	private final Object2IntMap<UUID> impurity = new Object2IntOpenHashMap<>();

	public int getFragmentation(UUID id) {
		return fragmentation.count(id);
	}
	
	public void addFragmentation(UUID id) {
		fragmentation.add(id);
		markDirty();
	}
	
	public void addFragmentation(UUID id, int d) {
		if (d < 0) {
			fragmentation.remove(id, -d);
		} else {
			fragmentation.add(id, d);
		}
		markDirty();
	}
	
	public void setFragmentation(UUID id, int i) {
		fragmentation.setCount(id, i);
		markDirty();
	}
	
	public int getImpurity(UUID id) {
		return Integer.bitCount(impurity.getInt(id));
	}
	
	public int getImpurityMask(UUID id) {
		return impurity.getInt(id);
	}
	
	public boolean isImpure(UUID id, int heart) {
		return Bits.get(impurity.getInt(id), heart);
	}
	
	public void setImpure(UUID id, int heart, boolean impure) {
		impurity.put(id, Bits.set(impurity.getInt(id), heart, impure));
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		for (var en : fragmentation.entrySet()) {
			var cmp = new NbtCompound();
			putSmallestNumber(cmp, "Fragmentation", en.getCount());
			nbt.put(en.getElement().toString(), cmp);
		}
		for (var en : impurity.object2IntEntrySet()) {
			if (en.getIntValue() == 0) continue;
			var cmp = nbt.getCompound(en.getKey().toString());
			putSmallestNumber(cmp, "Impurity", en.getIntValue());
			nbt.put(en.getKey().toString(), cmp);
		}
		return nbt;
	}
	
	private void putSmallestNumber(NbtCompound cmp, String k, int v) {
		if (v < Byte.MAX_VALUE) {
			cmp.putByte(k, (byte)v);
		} else if (v < Short.MAX_VALUE) {
			cmp.putShort(k, (short)v);
		} else {
			cmp.putInt(k, v);
		}
	}

	public void readNbt(NbtCompound nbt) {
		fragmentation.clear();
		impurity.clear();
		for (var k : nbt.getKeys()) {
			try {
				var id = UUID.fromString(k);
				var cmp = nbt.getCompound(k);
				if (cmp.contains("Fragmentation"))
					fragmentation.add(id, cmp.getInt("Fragmentation"));
				if (cmp.contains("Impurity"))
					impurity.put(id, cmp.getInt("Impurity"));
			} catch (IllegalArgumentException e) {}
		}
	}
	
}
