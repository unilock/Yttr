package com.unascribed.yttr.mixin.cleaver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixinsupport.SlopeStander;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Mixin(Entity.class)
@Environment(EnvType.CLIENT)
public class MixinEntity implements SlopeStander {

	private double yttr$yOffset;
	private double yttr$lastYOffset;
	private boolean yttr$lastOnGround;
	private boolean yttr$checkingCleaveCollision;
	
	@Inject(at=@At("HEAD"), method="baseTick")
	public void baseTick(CallbackInfo ci) {
		yttr$lastYOffset = yttr$yOffset;
		yttr$lastOnGround = ((Entity)(Object)this).isOnGround();
	}
	
	@Inject(at=@At("TAIL"), method="checkBlockCollision")
	protected void checkBlockCollision(CallbackInfo ci) {
		if (yttr$checkingCleaveCollision) return;
		yttr$yOffset = 0;
		Entity self = (Entity)(Object)this;
		if (self.getWorld().isClient) {
			Box box = self.getBoundingBox();
			for (var bp : BlockPos.iterateOutwards(self.getBlockPos(), 1, 1, 1)) {
				BlockState bs = self.getWorld().getBlockState(bp);
				if (box.intersects(bp.getX()-0.5, bp.getY()-0.5, bp.getZ()-0.5, bp.getX()+1.5, bp.getY()+1.5, bp.getZ()+1.5) && bs.isOf(YBlocks.CLEAVED_BLOCK)) {
					try {
						yttr$checkingCleaveCollision = true;
						((CleavedBlock)bs.getBlock()).onEntityNearby(bs, self.getWorld(), bp, self);
						yttr$lastOnGround = self.isOnGround();
						break;
					} catch (Throwable t) {
						CrashReport report = CrashReport.create(t, "[Yttr] Performing cleaved block slope adjustment");
						CrashReportSection section = report.addElement("Block being collided with");
						CrashReportSection.addBlockInfo(section, self.getWorld(), bp, bs);
						throw new CrashException(report);
					} finally {
						yttr$checkingCleaveCollision = false;
					}
				}
			}
		}
	}
	
	@Override
	public double yttr$getYOffset() {
		return yttr$yOffset;
	}

	@Override
	public void yttr$setYOffset(double yOffset) {
		yttr$yOffset = yOffset;
	}
	
	@Override
	public double yttr$getLastYOffset() {
		return yttr$lastYOffset;
	}
	
	@Override
	public boolean yttr$wasOnGround() {
		return yttr$lastOnGround;
	}

}
