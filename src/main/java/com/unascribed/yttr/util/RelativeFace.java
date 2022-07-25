package com.unascribed.yttr.util;

import net.minecraft.util.math.Direction;

public enum RelativeFace {
	FRONT,
	RIGHT,
	LEFT,
	BACK,
	TOP,
	BOTTOM,
	;
	
	public static RelativeFace from(Direction facing, Direction face) {
		if (face == facing) return FRONT;
		if (face == facing.getOpposite()) return BACK;
		if (face == facing.rotateYClockwise()) return RIGHT;
		if (face == facing.rotateYCounterclockwise()) return LEFT;
		if (face == Direction.UP) return TOP;
		if (face == Direction.DOWN) return BOTTOM;
		throw new AssertionError(face+"??");
	}
	
}
