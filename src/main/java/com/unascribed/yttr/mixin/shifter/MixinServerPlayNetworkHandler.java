package com.unascribed.yttr.mixin.shifter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.item.ShifterItem;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.InventoryItemPickC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {

	@Shadow
	public ServerPlayerEntity player;

	@Inject(at=@At("HEAD"), method="onInventoryItemPick", cancellable=true)
	public void onInventoryItemPick(InventoryItemPickC2SPacket packet, CallbackInfo ci) {
		if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ShifterItem) {
			if (packet.getSlot() < 0 || packet.getSlot() >= player.getInventory().size()) return;
			ItemStack desired = player.getInventory().getStack(packet.getSlot());
			player.getInventory().setStack(packet.getSlot(), player.getStackInHand(Hand.OFF_HAND));
			player.setStackInHand(Hand.OFF_HAND, desired);
			player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, packet.getSlot(), player.getInventory().getStack(packet.getSlot())));
			player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, 40, player.getInventory().getStack(40)));
			ci.cancel();
		}
	}

}
