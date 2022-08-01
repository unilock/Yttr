package com.unascribed.yttr.content.block.decor;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.client.render.LampRenderer;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mechanics.HaloBlockEntity;
import com.unascribed.yttr.util.YTickable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LampBlockEntity extends BlockEntity implements HaloBlockEntity, YTickable {

	private boolean clientCreated = false;
	
	public LampBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.LAMP, pos, state);
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
	public boolean shouldRenderHalo() {
		return getCachedState().get(LampBlock.LIT);
	}

	@Override
	public int getGlowColor() {
		return getCachedState().get(LampBlock.COLOR).glowColor;
	}
	
	@Override
	public @Nullable Direction getFacing() {
		return getCachedState().contains(WallLampBlock.FACING) ? getCachedState().get(WallLampBlock.FACING) : null;
	}
	
	@Override
	public Object getStateObject() {
		return getCachedState();
	}
	

}
