package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.AutoCloseableResourceManager;
import net.minecraft.resource.ReloadableResourceManager;

@Mixin(ReloadableResourceManager.class)
public interface AccessorReloadableResourceManagerImpl {

	@Accessor("resources")
	AutoCloseableResourceManager yttr$getResources();
	
}
