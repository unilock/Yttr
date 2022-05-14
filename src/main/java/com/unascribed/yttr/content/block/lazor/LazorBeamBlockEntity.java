package com.unascribed.yttr.content.block.lazor;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mechanics.HaloBlockEntity;

import net.minecraft.block.entity.BlockEntity;

public class LazorBeamBlockEntity extends BlockEntity implements HaloBlockEntity {

	public LazorBeamBlockEntity() {
		super(YBlockEntities.LAZOR_BEAM);
	}

	@Override
	public boolean shouldRenderHalo() {
		return true;
	}

	@Override
	public int getGlowColor() {
		return getCachedState().get(LazorBeamBlock.COLOR).glowColor;
	}

	@Override
	public Object getStateObject() {
		return getCachedState();
	}

}
