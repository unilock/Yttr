package com.unascribed.yttr.mixinsupport;

public interface DiffractorPlayer {

	/** Visual and functional. Player is not invisible until cloakTime exceeds this. */
	int WARMUP_TIME = 40;
	/** Purely visual. It takes this long for the device to finish spinning down after uncloaking. */
	int UNCLOAK_TIME = 6*20;
	/** Functional. This is how long the item is put on cooldown after uncloaking. */
	int COOLDOWN_TIME = 45*20;
	/** Functional. This is how long a player can remain cloaked before they are forcefully uncloaked. */
	int MAX_TIME = 90*20;
	
	float WARMUP_TIMEf = WARMUP_TIME;
	float UNCLOAK_TIMEf = UNCLOAK_TIME;
	float COOLDOWN_TIMEf = COOLDOWN_TIME;
	float MAX_TIMEf = MAX_TIME;
	
	boolean yttr$isCloaked();
	void yttr$setCloaked(boolean cloaked);
	default boolean yttr$isFullyCloaked() {
		return yttr$isCloaked() && yttr$getCloakTime() > WARMUP_TIME;
	}

	int yttr$getCloakTime();
	void yttr$setCloakTime(int cloakTime);
	int yttr$getUncloakTime();
	
}
