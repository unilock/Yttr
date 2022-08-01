package com.unascribed.yttr.content.block.decor;

import com.unascribed.yttr.client.render.LampRenderer;
import com.unascribed.yttr.mechanics.HaloBlockEntity;
import com.unascribed.yttr.util.YTickable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractHaloBlockEntity extends BlockEntity implements HaloBlockEntity, YTickable {

	private boolean clientCreated = false;

	public AbstractHaloBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void tick() {
		// not used
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void clientTick() {
		if (!clientCreated) {
			LampRenderer.notifyCreated(this);
			clientCreated = true;
		}
	}
	
	@Override
	public Object getStateObject() {
		return getCachedState();
	}

}
