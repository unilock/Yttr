package com.unascribed.yttr.util;

import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.random.Xoroshiro128PlusPlusRandom;

public final class YRandom {

	private static final ThreadLocal<RandomGenerator> HOLDER = ThreadLocal.withInitial(() -> new Xoroshiro128PlusPlusRandom(System.nanoTime()));
	
	public static RandomGenerator get() {
		return HOLDER.get();
	}

	private YRandom() {}
	
}
