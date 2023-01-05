package com.unascribed.yttr.world;

import java.util.UUID;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class SoulState extends PersistentState {

	public static final UUID MAX_HEALTH_MODIFIER = UUID.fromString("52f41157-796b-43ec-ab89-18f1c9c6c15c");

	public static SoulState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(SoulState::fromNbt, SoulState::new, "yttr_soul");
	}

	public static SoulState fromNbt(NbtCompound tag) {
		SoulState ret = new SoulState();
		ret.readNbt(tag);
		return ret;
	}
	
	private final Multiset<UUID> fragmentation = HashMultiset.create();

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
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		for (var en : fragmentation.entrySet()) {
			var cmp = new NbtCompound();
			cmp.putInt("Fragmentation", en.getCount());
			nbt.put(en.getElement().toString(), cmp);
		}
		return nbt;
	}
	
	public void readNbt(NbtCompound nbt) {
		fragmentation.clear();
		for (var k : nbt.getKeys()) {
			try {
				var id = UUID.fromString(k);
				var cmp = nbt.getCompound(k);
				fragmentation.add(id, cmp.getInt("Fragmentation"));
			} catch (IllegalArgumentException e) {}
		}
	}
	
}
