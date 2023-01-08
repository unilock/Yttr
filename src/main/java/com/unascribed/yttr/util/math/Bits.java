package com.unascribed.yttr.util.math;

public class Bits {

	public static boolean get(int bitset, int idx) {
		return (bitset&(1<<idx)) != 0;
	}
	
	public static int set(int bitset, int idx, boolean value) {
		int bit = 1<<idx;
		if (value) {
			bitset |= bit;
		} else {
			bitset &= ~bit;
		}
		return bitset;
	}
	
}
