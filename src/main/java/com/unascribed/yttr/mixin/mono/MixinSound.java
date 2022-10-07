package com.unascribed.yttr.mixin.mono;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.client.MonoIdentifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(value=Sound.class, priority=1200)
public class MixinSound {

	@Shadow @Final
	private Identifier id;
	
	@Inject(at=@At("HEAD"), method="getLocation", cancellable=true)
	public void getLocationHead(CallbackInfoReturnable<Identifier> ci) {
		if (id.getPath().endsWith(".xm.bz2--mono-")) {
			ci.setReturnValue(new MonoIdentifier(id.getNamespace(), "sounds/"+id.getPath().replace("--mono-", "")));
		}
	}
	
	@Inject(at=@At("RETURN"), method="getLocation", cancellable=true)
	public void getLocationTail(CallbackInfoReturnable<Identifier> ci) {
		if (ci.getReturnValue().getPath().contains("--mono-")) {
			ci.setReturnValue(new MonoIdentifier(ci.getReturnValue()));
		}
	}
	
}
