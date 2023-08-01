package com.unascribed.yttr.mixin.neodymium;

import com.unascribed.yttr.init.YDamageTypes;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.content.block.NeodymiumBlock.MagneticVoxelShape;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixinsupport.Magnetized;

import com.google.common.collect.Iterables;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction.Axis;

@Mixin(Entity.class)
public class MixinEntity implements Magnetized {
	private boolean yttr$magnetizedBelow;
	private boolean yttr$magnetizedAbove;
	private boolean yttr$magnetizedAboveStuck;
	
	@Inject(at=@At("TAIL"), method="baseTick")
	protected void baseTick(CallbackInfo ci) {
		yttr$magnetTick();
	}

	@Override
	public void yttr$magnetTick() {
		yttr$magnetizedBelow = false;
		yttr$magnetizedAbove = false;
		yttr$magnetizedAboveStuck = false;
		boolean receptiveAbove = false;
		boolean receptiveBelow = false;
		Entity self = (Entity)(Object)this;
		if (self.getType() == null || self.getWorld() == null || self.getBoundingBox() == null) return;
		if (self instanceof AbstractMinecartEntity || self.getType().isIn(YTags.Entity.MAGNETIC)) {
			receptiveBelow = true;
			receptiveAbove = true;
		} else if (self instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)self;
			if (yttr$isInSafe(le.getEquippedStack(EquipmentSlot.FEET), YTags.Item.MAGNETIC)) {
				receptiveBelow = true;
			}
			if (yttr$isInSafe(le.getEquippedStack(EquipmentSlot.HEAD), YTags.Item.MAGNETIC)) {
				receptiveAbove = true;
			}
		} else if (self instanceof ItemEntity) {
			if (yttr$isInSafe(((ItemEntity)self).getStack(), YTags.Item.MAGNETIC)) {
				receptiveBelow = true;
				receptiveAbove = true;
			}
		}
		if (receptiveBelow) {
			Box box = self.getBoundingBox();
			Box bottom = new Box(box.minX, box.minY-0.5, box.minZ, box.maxX, box.minY, box.maxZ);
			if (Iterables.any(self.getWorld().getBlockCollisions(self, bottom), vs -> vs instanceof MagneticVoxelShape)) {
				yttr$magnetizedBelow = true;
			}
		}
		if (receptiveAbove) {
			Box box = self.getBoundingBox();
			Box top = new Box(box.minX, box.maxY, box.minZ, box.maxX, box.maxY+0.5, box.maxZ);
			if (Iterables.any(self.getWorld().getCollisions(self, top), vs -> {
				if (!(vs instanceof MagneticVoxelShape)) return false;
				double min = vs.getMin(Axis.Y);
				if (min < box.maxY) return false;
				if (min == box.maxY) yttr$magnetizedAboveStuck = true;
				return true;
			})) {
				yttr$magnetizedAbove = true;
			}
		}
		if (yttr$magnetizedAbove) {
			self.setVelocity(self.getVelocity().x, Math.max(self.getVelocity().y, 0.1), self.getVelocity().z);
			if (yttr$magnetizedBelow && !(self instanceof IronGolemEntity) && !(self instanceof ItemEntity)) {
				self.damage(self.getDamageSources().create(YDamageTypes.MAGNET), 2);
			}
			if (Math.abs(self.getPitch()) > 0.01 && self.getWorld().random.nextInt(20) == 0) {
				self.playSound(YSounds.MAGNET_STEP, 1, 1);
			}
			self.setPitch(self.getPitch() / 2);
		} else if (yttr$magnetizedBelow) {
			self.setVelocity(self.getVelocity().x, Math.min(self.getVelocity().y, -0.9), self.getVelocity().z);
		}
	}
	
	private boolean yttr$isInSafe(ItemStack stack, TagKey<Item> tag) {
		if (stack.getItem() == null) return false; // aaAAaaAA
		if (stack.getItem().getBuiltInRegistryHolder() == null) return false; // AAAAAAAA
		return stack.isIn(tag);
	}

	@Inject(at=@At("RETURN"), method="getJumpVelocityMultiplier", cancellable=true)
	protected void getJumpVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		if (yttr$magnetizedBelow) ci.setReturnValue(ci.getReturnValueF()*0.1f);
		if (yttr$magnetizedAbove) ci.setReturnValue(0f);
	}

	@Inject(at=@At("RETURN"), method="getVelocityMultiplier", cancellable=true)
	protected void getVelocityMultiplier(CallbackInfoReturnable<Float> ci) {
		if (yttr$magnetizedBelow) ci.setReturnValue(ci.getReturnValueF()*0.2f);
		if (yttr$magnetizedAbove) ci.setReturnValue(ci.getReturnValueF()*0.1f);
	}
	
	@Inject(at=@At("HEAD"), method="playStepSound")
	protected void playStepSound(BlockState state, CallbackInfo ci) {
		if (yttr$magnetizedBelow) {
			Entity self = (Entity)(Object)this;
			self.playSound(YSounds.MAGNET_STEP, 1, 1);
		}
	}

	@Override
	public boolean yttr$isMagnetizedBelow() {
		return yttr$magnetizedBelow;
	}

	@Override
	public boolean yttr$isMagnetizedAbove() {
		return yttr$magnetizedAbove;
	}

	@Override
	public boolean yttr$isMagnetizedAboveStuck() {
		return yttr$magnetizedAboveStuck;
	}
	
}
