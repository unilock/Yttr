package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.InputUtil;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBind;

@Environment(EnvType.CLIENT)
@Mixin(KeyBind.class)
public interface AccessorKeyBind {

	@Accessor("boundKey")
	InputUtil.Key yttr$getBoundKey();
	
}
