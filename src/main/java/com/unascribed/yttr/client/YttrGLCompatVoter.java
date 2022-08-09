package com.unascribed.yttr.client;

import com.unascribed.lib39.deferral.api.GLCompatVoter;
import com.unascribed.yttr.YConfig;

public class YttrGLCompatVoter implements GLCompatVoter {

	@Override
	public boolean wantsCompatibilityProfile() {
		return !YConfig.Client.forceOpenGLCore.resolve(false);
	}

}
