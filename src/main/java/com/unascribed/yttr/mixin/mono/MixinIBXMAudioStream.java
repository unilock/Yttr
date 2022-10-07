package com.unascribed.yttr.mixin.mono;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.lib39.keygen.IBXMAudioStream;
import com.unascribed.yttr.client.YttrClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(value=IBXMAudioStream.class, remap=false)
public class MixinIBXMAudioStream {

	@ModifyVariable(at=@At("HEAD"), method="create", remap=false)
	private static boolean modifyStereo(boolean orig) {
		if (YttrClient.forceIbxmMono) return false;
		return orig;
	}
	
}
