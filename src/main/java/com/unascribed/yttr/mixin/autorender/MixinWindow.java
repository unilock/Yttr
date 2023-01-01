package com.unascribed.yttr.mixin.autorender;

import static org.lwjgl.glfw.GLFW.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.lib39.core.mixinsupport.AutoMixinEligible;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
@Mixin(Window.class)
@AutoMixinEligible(ifSystemProperty="yttr.render")
public class MixinWindow {

	@Inject(at=@At(value="INVOKE", target="org/lwjgl/glfw/GLFW.glfwCreateWindow(IILjava/lang/CharSequence;JJ)J"),
			method="<init>")
	private void modifyGlfwHints(CallbackInfo ci) {
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
	}
	
}
