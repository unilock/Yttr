package com.unascribed.yttr.content.entity;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YEntities;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixin.accessor.AccessorFallingBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlippingTransfungusEntity extends FallingBlockEntity {

	public SlippingTransfungusEntity(World world, double x, double y, double z, BlockState block) {
		super(YEntities.SLIPPING_TRANSFUNGUS, world);
		((AccessorFallingBlockEntity)this).yttr$setBlock(block);
		this.inanimate = true;
		this.setPosition(x, y, z);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		setFallingBlockPos(getBlockPos());
	}

	public SlippingTransfungusEntity(EntityType<? extends SlippingTransfungusEntity> entityType, World world) {
		super(entityType, world);
	}
	
	
	@Override
	public void tick() {
		if (!getWorld().isClient && getWorld().getBlockState(getBlockPos()).isAir()) {
			getWorld().setBlockState(getBlockPos(), YBlocks.TRANSFUNGUS_SPORES.getDefaultState());
		}
		super.tick();
	}
	
	@Override
	public void move(MovementType movementType, Vec3d movement) {
		super.move(movementType, movement);
		if (isOnGround() && (getWorld().getBlockState(getBlockPos().down()).isIn(YTags.Block.TRANSFUNGUS_SLIPPERY) || age < 20)
				&& getVelocity().lengthSquared() > 0.005 && !horizontalCollision) {
			for (var d : Direction.values()) {
				if (getWorld().getBlockState(getBlockPos().offset(d)).isIn(YTags.Block.TRANSFUNGUS_STICKY)) return;
			}
			setOnGround(false);
		}
	}

}
