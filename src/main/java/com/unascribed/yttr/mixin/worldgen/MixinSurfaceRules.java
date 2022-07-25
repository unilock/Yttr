package com.unascribed.yttr.mixin.worldgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.gen.surfacebuilder.SurfaceRules;

@Mixin(SurfaceRules.class)
public class MixinSurfaceRules {

	@Redirect(at=@At(value="INVOKE", target="java/util/Arrays.asList([Ljava/lang/Object;)Ljava/util/List;"),
			method="sequence", require=0)
	private static List modifySequenceList(Object[] arr) {
		return new ArrayList(Arrays.asList(arr));
	}
	
}
