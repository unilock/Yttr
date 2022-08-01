package com.unascribed.yttr.mixin.crease.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

	@Inject(at=@At("HEAD"), method="clickSlot", cancellable=true)
	public void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (slotId >= 0 && player.currentScreenHandler.isValidSlotIndex(slotId) && player.currentScreenHandler.getSlot(slotId).getStack().isOf(YItems.CREASE)) {
			ci.cancel();
		}
	}
	
}
