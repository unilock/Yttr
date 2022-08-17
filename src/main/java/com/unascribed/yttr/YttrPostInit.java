package com.unascribed.yttr;

import com.unascribed.lib39.core.api.ModPostInitializer;
import com.unascribed.yttr.init.YLatches;

public class YttrPostInit implements ModPostInitializer {

	@Override
	public void onPostInitialize() {
		YLatches.latchAll();
	}
	
}
