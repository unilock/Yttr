package com.unascribed.yttr.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.unascribed.yttr.client.screen.handled.RafterScreen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public class MixinHandledScreen {

	@ModifyReturnValue(at=@At("RETURN"), method="getSlotAt")
	private Slot getSlotAt(Slot original, double x, double y) {
		Object self = this;
		if (original == null && self instanceof RafterScreen) {
			return ((RafterScreen)self).getAltSlotAt(x, y);
		}
		return original;
	}
	
}
