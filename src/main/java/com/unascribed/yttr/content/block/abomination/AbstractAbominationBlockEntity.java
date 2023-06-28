package com.unascribed.yttr.content.block.abomination;

import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractAbominationBlockEntity extends BlockEntity implements YTickable {

	protected int sayTicks = -60;
	
	public int age;
	
	public float headYaw;
	public float prevHeadYaw;
	public float headPitch;
	public float prevHeadPitch;
	
	public AbstractAbominationBlockEntity(BlockEntityType<? extends AbstractAbominationBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	public boolean isSuffocating() {
		BlockPos headBlock = BlockPos.fromPosition(getHeadPos());
		if (headBlock.equals(pos)) return false;
		return world.getBlockState(headBlock).shouldSuffocate(world, headBlock);
	}
	
	public abstract Vec3d getHeadPos();
	
	protected abstract SoundEvent getHurtSound();
	protected abstract SoundEvent getAmbientSound();
	
	public boolean canSay() {
		return true;
	}
	
	@Override
	public void tick() {
		age++;
		
		if (age % 20 == 0 && isSuffocating()) {
			sayTicks = -60;
			world.playSound(null, pos, getHurtSound(), SoundCategory.BLOCKS, canSay() ? 1 : 0.6f, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
		} else if (world.random.nextInt(1000) < sayTicks++ && canSay()) {
			sayTicks = -60;
			world.playSound(null, pos, getAmbientSound(), SoundCategory.BLOCKS, 0.7f, (world.random.nextFloat()-world.random.nextFloat())*0.2f + 1);
		}
		
		prevHeadYaw = headYaw;
		prevHeadPitch = headPitch;
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.putFloat("HeadYaw", headYaw);
		tag.putFloat("HeadPitch", headPitch);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		headYaw = prevHeadYaw = (tag.contains("Yaw") ? tag.getFloat("Yaw") : tag.getFloat("HeadYaw"));
		headPitch = prevHeadPitch = (tag.contains("Pitch") ? tag.getFloat("Pitch") : tag.getFloat("HeadPitch"));
	}
	
	@Override
	public NbtCompound toSyncedNbt() {
		NbtCompound tag = super.toSyncedNbt();
		tag.putFloat("HeadYaw", headYaw);
		tag.putFloat("HeadPitch", headPitch);
		return tag;
	}


	
}
