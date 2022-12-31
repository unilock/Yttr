package com.unascribed.yttr.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class YRandom {

	public static Random get() {
		return ThreadLocalRandom.current();
	}

	private YRandom() {}
	
}
