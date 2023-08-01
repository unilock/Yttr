package com.unascribed.yttr.content.entity;

import com.unascribed.yttr.init.YEntities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

public class RifleDummyEntity extends Entity {

	public RifleDummyEntity(World world) {
		super(YEntities.RIFLE_DUMMY, world);
	}

	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		throw new UnsupportedOperationException();
	}

}
