package com.unascribed.yttr.client;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.EXTEfx.*;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.network.MessageC2SSetCloaked;
import com.unascribed.yttr.util.YRandom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class DiffractorClientLogic extends IHasAClient {

	private static int alFilter, alEffect, alSlot;
	private static CloakAmbienceSound ambienceSound;
	
	private static int emptyHandTime = 0;
	
	private static final int[] colors = {0xF4943E,0xF49943,0xF4A24D,0xF4AF5B,0xF5BE6A,0xF7D07B,0xFAE38C,0xFDF79C,0xFFFFAC,0xFEFFBB,0xF6FFC1,0xE8FFC2,0xD6FFC3,0xC3FFC3,0xAFFFC3,0x9DFFC3,0x8BFFC4,0x7CFFC4,0x71FFC5,0x6AFFC5,0x6AFFC7,0x6FFFCD,0x79FFD5,0x87FFE1,0x97FFED,0xA7FFF9,0xB9FFFE,0xCCFFFF,0xDFFFFF,0xEAFFFF,0xE9FFFF,0xDEF2F9,0xCDDBF0,0xBEC3E9,0xB3ABE3,0xAE92DF,0xAE78DC,0xB05CD7,0xB241D3,0xB42AD0,0xB61ECF,0xB921CF,0xBE27D0,0xC631D0,0xD13BD1,0xDD46D3,0xEA51D5,0xF65DD6,0xFE68D7,0xFF75D6,0xFF80CF,0xFF85C1,0xFF89B1,0xFF8CA4,0xFC8E96,0xF98F87,0xF69076,0xF59065,0xF49153,0xF49146};

	public static void render(GuiGraphics ctx, float tickDelta) {
		if (mc.player instanceof DiffractorPlayer dp) {
//			if (emptyHandTime > 20) {
//				ControlHints.renderHints(matrices, "yttr.diffractor.controlhint."+(dp.yttr$isCloaked()?"cloaked":"normal"), 1-((emptyHandTime-20)/10f));
//			}
			float fade = MathHelper.clamp(dp.yttr$getCloakTime()/40f, 0, 1);
			float fullness = 1;
			var cd = mc.player.getItemCooldownManager();
			if (cd.isCoolingDown(YItems.DIFFRACTOR.get())) {
				fullness = 1-(cd.getCooldownProgress(YItems.DIFFRACTOR.get(), tickDelta));
				fade = 0.6f;
				if (fullness > 0.9f) {
					fade = (1-((fullness-0.9f)/0.1f))*fade;
				}
			} else {
				fullness = 1-(dp.yttr$getCloakTime()/DiffractorPlayer.MAX_TIMEf);
			}
			if (fade > 0) {
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.setShader(GameRenderer::getPositionColorShader);
				var tess = Tessellator.getInstance();
				var bb = tess.getBufferBuilder();
				bb.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
				int j = 48;
				float max = (j+2)*fullness;
				for (int i = 0; i < j+2; i++) {
					float a = (i/(float)j);
					float t = (a-0.5f)*((float)Math.PI*2);
					float r = 10+((i%2)*1);
					float x = (MathHelper.sin(t)*r)+mc.getWindow().getScaledWidth()/2f;
					float y = (MathHelper.cos(t)*r)+mc.getWindow().getScaledHeight()/2f;
					int c = colors[Math.floorMod(((int)((a)*colors.length)+mc.player.age), colors.length)];
					float part = 0.9f;
					if (i == (int)max) {
						part = ((max%1)*0.8f)+0.1f;
					} else if (i > max) {
						part = 0.1f;
					}
					bb.vertex(x, y, 0).color((c>>16)&0xFF, (c>>8)&0xFF, c&0xFF, (int)((fade*part)*255)).next();
				}
				tess.draw();
				RenderSystem.disableBlend();
			}
		}
	}
	
	public static void tweakAlSource(int name) {
		if (alFilter != 0 && alGetSourcei(name, AL_SOURCE_RELATIVE) == AL_FALSE) {
			alSourcei(name, AL_DIRECT_FILTER, alFilter);
			alSource3i(name, AL_AUXILIARY_SEND_FILTER, alSlot, 0, alFilter);
		}
	}
	
	public static void tick() {
		if (mc.player instanceof DiffractorPlayer dp) {
			float cloakness = MathHelper.clamp(dp.yttr$getCloakTime()/DiffractorPlayer.WARMUP_TIMEf, 0, 1);
			if (cloakness > 0) {
				if (alFilter == 0) {
					alEffect = alGenEffects();
					alEffecti(alEffect, AL_EFFECT_TYPE, AL_EFFECT_FLANGER);
					alEffecti(alEffect, AL_FLANGER_WAVEFORM, AL_FLANGER_WAVEFORM_TRIANGLE);
					alEffecti(alEffect, AL_FLANGER_PHASE, 0);
					alEffectf(alEffect, AL_FLANGER_RATE, 0.27f);
					alEffectf(alEffect, AL_FLANGER_DEPTH, 1.0f);
					alEffectf(alEffect, AL_FLANGER_FEEDBACK, -0.5f);
					alEffectf(alEffect, AL_FLANGER_DELAY, 0.002f);
					
					alFilter = alGenFilters();
					alFilteri(alFilter, AL_FILTER_TYPE, AL_FILTER_LOWPASS);
					alFilterf(alFilter, AL_LOWPASS_GAIN, 1.1f);
					alFilterf(alFilter, AL_LOWPASS_GAINHF, 0.025f);

					alSlot = alGenAuxiliaryEffectSlots();
					alAuxiliaryEffectSloti(alSlot, AL_EFFECTSLOT_EFFECT, alEffect);
					alAuxiliaryEffectSloti(alSlot, AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, AL_FALSE);
				}
				alAuxiliaryEffectSlotf(alSlot, AL_EFFECTSLOT_GAIN, cloakness);
			} else if (alFilter != 0) {
				alDeleteAuxiliaryEffectSlots(alSlot);
				alDeleteEffects(alEffect);
				alDeleteFilters(alFilter);
				alSlot = alEffect = alFilter = 0;
			}
			
			var slot = Yttr.trinketsAccess.getWorn(mc.player, YItems.DIFFRACTOR::is);
			if (slot.isPresent()) {
				boolean onCooldown = mc.player.getItemCooldownManager().isCoolingDown(YItems.DIFFRACTOR.get());
				if (mc.player.getMainHandStack().isEmpty() && !onCooldown) {
					emptyHandTime++;
				} else {
					emptyHandTime = 0;
				}
				if (mc.player.isSneaking() && mc.options.swapHandsKey.wasPressed()) {
					while (mc.options.swapHandsKey.wasPressed()) {}
					if (!onCooldown) {
						new MessageC2SSetCloaked(!dp.yttr$isCloaked()).sendToServer();
						if (!dp.yttr$isCloaked()) {
							mc.getSoundManager().play(new PositionedSoundInstance(YSounds.CLOAK.getId(), mc.player.getSoundCategory(), 1, 1, YRandom.get(), false, 0, AttenuationType.NONE, 0, 0, 0, true));
						}
					} else {
						mc.player.sendMessage(Text.translatable("tip.yttr.diffractor.cooldown"), true);
						mc.player.playSound(YSounds.REPLICATOR_REFUSE, 1, 0.5f);
					}
				}
			} else {
				emptyHandTime = 0;
			}
			
			if (ambienceSound == null || ambienceSound.isDone()) {
				ambienceSound = new CloakAmbienceSound(YSounds.CLOAK_LOOP);
			}
			
			if (dp.yttr$isCloaked() && !mc.getSoundManager().isPlaying(ambienceSound)) {
				mc.getSoundManager().play(ambienceSound);
			}
			if (dp.yttr$getCloakTime() == DiffractorPlayer.MAX_TIME-100) {
				mc.getSoundManager().play(new PositionedSoundInstance(YSounds.CLOAK_EXPIRE.getId(), mc.player.getSoundCategory(), 1, 1, YRandom.get(), false, 0, AttenuationType.NONE, 0, 0, 0, true));
			}
		}
	}
	
}
