package com.unascribed.yttr.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;

@Mixin(ReloadableResourceManagerImpl.class)
public interface AccessorReloadableResourceManagerImpl {

	@Accessor("activeManager")
	LifecycledResourceManager yttr$getActiveManager();
	
}
