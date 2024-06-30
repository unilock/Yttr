package com.unascribed.yttr.mixin.diffractor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mixinsupport.DiffractorListener;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements DiffractorPlayer {

	private boolean yttr$cloaked = false;
	private int yttr$cloakTime = 0;
	private int yttr$uncloakTime = 0;
	
	@Inject(at=@At("HEAD"), method="tick")
	public void yttr$cloakTick(CallbackInfo ci) {
		PlayerEntity self = (PlayerEntity)(Object)this;
		self.isInvisible();
		if (!self.isAlive()) return;
		if (yttr$cloaked && Yttr.trinketsAccess.getWorn(self, YItems.DIFFRACTOR::is).isEmpty()) {
			yttr$setCloaked(false);
		}
		if (yttr$cloaked) {
			yttr$cloakTime++;
			yttr$uncloakTime = 0;
		} else {
			yttr$uncloakTime++;
			if (yttr$cloakTime > 0) {
				if (yttr$cloakTime > DiffractorPlayer.WARMUP_TIME) {
					yttr$cloakTime = DiffractorPlayer.WARMUP_TIME;
				} else {
					yttr$cloakTime--;
				}
			}
		}
		if (yttr$cloakTime > DiffractorPlayer.MAX_TIME) {
			yttr$setCloaked(false);
		}
	}
	
	@Inject(at=@At("HEAD"), method="playSound", cancellable=true)
	public void yttr$suppressPlaySoundWhenCloaked(CallbackInfo ci) {
		if (yttr$isFullyCloaked()) {
			ci.cancel();
		}
	}

	@Override
	public boolean yttr$isCloaked() {
		return yttr$cloaked;
	}

	@Override
	public void yttr$setCloaked(boolean cloaked) {
		if (cloaked == yttr$cloaked) return;
		yttr$cloaked = cloaked;
		if (this instanceof DiffractorListener dl) {
			if (cloaked) {
				dl.yttr$onCloak();
			} else {
				dl.yttr$onUncloak();
			}
		}
	}

	@Override
	public int yttr$getCloakTime() {
		return yttr$cloakTime;
	}
	
	@Override
	public void yttr$setCloakTime(int cloakTime) {
		yttr$cloakTime = cloakTime;
	}
	
	@Override
	public int yttr$getUncloakTime() {
		return yttr$uncloakTime;
	}
	

}
