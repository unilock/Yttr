package com.unascribed.yttr.mixin.client;

import static org.lwjgl.glfw.GLFW.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public class MixinWindow {

	@ModifyConstant(method="<init>", constant=@Constant(intValue=GLFW_OPENGL_CORE_PROFILE),
			require=0) // if someone else changed this, they probably made the same change
	private static int modifyOpenGlProfile(int orig) {
		return GLFW_OPENGL_COMPAT_PROFILE;
	}
	
}
