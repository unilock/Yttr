package com.unascribed.yttr.mixinsupport;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.util.math.Vec2i;

import com.google.common.primitives.Ints;

import net.minecraft.util.math.BlockPos;

public interface DiverPlayer {
	
	long NANOS_PER_TICK = TimeUnit.MILLISECONDS.toNanos(50);

	boolean yttr$isDiving();
	void yttr$setDiving(boolean b);
	
	boolean yttr$isInvisibleFromDiving();
	boolean yttr$isNoGravityFromDiving();
	
	Set<UUID> yttr$getKnownGeysers();
	
	int yttr$getLastDivePosUpdate();
	void yttr$setLastDivePosUpdate(int i);
	
	@Nullable Vec2i yttr$getDivePos();
	void yttr$setDivePos(@Nullable Vec2i v);
	
	long yttr$getFastDiveFinishNanos();
	void yttr$setFastDiveFinishNanos(long nanos);
	
	@Deprecated
	default int yttr$getFastDiveTime() {
		return Ints.saturatedCast((System.nanoTime()-yttr$getFastDiveFinishNanos())/NANOS_PER_TICK);
	}
	default void yttr$setFastDiveTime(int ticks) {
		yttr$setFastDiveFinishNanos(System.nanoTime()+(ticks*NANOS_PER_TICK));
	}
	
	@Nullable BlockPos yttr$getFastDiveTarget();
	void yttr$setFastDiveTarget(@Nullable BlockPos g);
	
}
