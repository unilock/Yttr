package com.unascribed.yttr.compat;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.init.YBiomes;
import terrablender.api.SurfaceRuleManager;

public class TerrablenderCompat {
    public static void init() {
        if (YConfig.WorldGen.wasteland) {
            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, "yttr", YBiomes.WASTELAND_RULE);
        }
    }
}
