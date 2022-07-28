package com.unascribed.yttr.mixin.client;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_ANY_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
@Mixin(Window.class)
public class MixinWindow {

	@Inject(at=@At(value="INVOKE", target="org/lwjgl/glfw/GLFW.glfwCreateWindow(IILjava/lang/CharSequence;JJ)J"),
			method="<init>")
	private void modifyGlfwHints(CallbackInfo ci) {
		// Asking for any profile, version 1.0, causes GLFW to attempt to negotiate the newest possible
		// compatibility context it can. On Windows and Linux, this works as you would expect, and
		// results in a 4.6 (or whatever the newest GL the driver supports is) context with
		// ARB_compatibility available. This will not work on macOS, but frankly fuck macOS. I just
		// want to port my mod without turning into an SCP.
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_ANY_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 1);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
	}
	
}
