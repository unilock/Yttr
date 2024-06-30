package com.unascribed.yttr.client;

import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.util.YRandom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class CloakAmbienceSound extends PositionedSoundInstance implements TickableSoundInstance {

	public CloakAmbienceSound(SoundEvent event) {
		super(event.getId(), SoundCategory.AMBIENT, 0.05f, 1, YRandom.get(), true, 0, AttenuationType.NONE, 0, 0, 0, true);
	}

	@Override
	public boolean isDone() {
		return MinecraftClient.getInstance().player instanceof DiffractorPlayer dp ? !dp.yttr$isCloaked() && dp.yttr$getCloakTime() <= 0 : true;
	}

	@Override
	public void tick() {
		if (MinecraftClient.getInstance().player instanceof DiffractorPlayer dp) {
			int t = dp.yttr$getCloakTime();
			if (t < DiffractorPlayer.WARMUP_TIME) {
				volume = (t/DiffractorPlayer.WARMUP_TIMEf)*0.15f;
			} else if (t > DiffractorPlayer.MAX_TIME-100) {
				int b = DiffractorPlayer.MAX_TIME-100;
				float a = (t-b)/100f;
				volume = (a*0.6f)+0.15f;
				pitch = 1+(a*1.5f);
			} else {
				volume = 0.15f;
			}
		}
	}

}
