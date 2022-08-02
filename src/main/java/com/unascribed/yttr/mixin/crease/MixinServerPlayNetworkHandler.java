package com.unascribed.yttr.mixin.crease;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.util.YLog;

import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow
	public ServerPlayerEntity player;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/network/packet/c2s/play/ClickSlotC2SPacket.getRevision()I"), method="onClickSlot", cancellable=true)
	public void onClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
		int slotId = packet.getSlot();
		if (slotId >= 0 && player.currentScreenHandler.getSlot(slotId).getStack().isOf(YItems.CREASE)) {
			YLog.warn("Player {} clicked crease at index {}", player.getEntityName(), slotId);
			player.currentScreenHandler.syncState();
			ci.cancel();
		}
	}
	
}
