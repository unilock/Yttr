package com.unascribed.yttr.mixin.accessor.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.item.ItemRenderer;

@Mixin(ItemRenderer.class)
public interface AccessorItemRenderer {

	@Accessor("colors")
	ItemColors yttr$getColors();
	
}
