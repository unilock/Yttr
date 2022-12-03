package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.network.ServerPlayNetworkHandler;

@Mixin(ServerPlayNetworkHandler.class)
public interface AccessorServerPlayNetHandler {

	@Accessor("floatingTicks")
	void yttr$setFloatingTicks(int i);
	
}
