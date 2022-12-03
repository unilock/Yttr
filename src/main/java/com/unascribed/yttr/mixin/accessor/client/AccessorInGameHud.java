package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public interface AccessorInGameHud {

	@Accessor("heldItemTooltipFade")
	int yttr$getHeldItemTooltipFade();
	@Accessor("currentStack")
	ItemStack yttr$getCurrentStack();
	
}
