package com.unascribed.yttr.compat.modmenu;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.client.suit.SuitMusic;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.base.Ascii;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class YttrConfigScreen extends Screen {

	public static final MusicSound MUSIC = new MusicSound(YSounds.SILENCE, 10, 10, true);
	
	private enum Section {
		GENERAL,
		CLIENT,
		RIFLE,
		WORLDGEN
	}
	
	private final Screen parent;
	private int time;
	private int closeTime;
	private boolean isClosing;
	private int initialMouseX = -1;
	private int initialMouseY = -1;
	
	private int mouseX, mouseY;
	private boolean clicked;
	private Section currentSection = null;
	
	private int uniq;
	
	private final SuitRenderer sr = new SuitRenderer();
	private final SuitMusic music = new SuitMusic(YSounds.TORUS, 1, SoundCategory.MUSIC) {
		@Override
		public boolean isDone() {
			return !(MinecraftClient.getInstance().currentScreen instanceof YttrConfigScreen);
		}
	};
	
	public YttrConfigScreen(Screen parent) {
		super(new LiteralText("Yttr configuration"));
		this.parent = parent;
		sr.setColor(LampColor.TEAL);
	}
	
	@Override
	public void onClose() {
		if (isClosing) {
			client.setScreen(parent);
			client.getMusicTracker().stop();
			return;
		}
		isClosing = true;
		client.getSoundManager().play(PositionedSoundInstance.master(YSounds.EFFECTOR_CLOSE, 1));
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		float t = time+delta;
		float ct = closeTime+delta;
		float a;
		if (isClosing) {
			a = sCurve5(1-MathHelper.clamp(ct/20f, 0, 1));
			music.setVolume(a);
		} else {
			a = sCurve5(MathHelper.clamp(t/10f, 0, 1));
		}
		if (initialMouseX == -1) {
			initialMouseX = mouseX;
			initialMouseY = mouseY;
		}
		if (a < 1) {
			parent.render(matrices, -200, -200, delta);
			float r = (Math.max(initialMouseX, height-initialMouseY)*1.1f)*a;
			matrices.push();
			matrices.translate(initialMouseX, initialMouseY, 0);
			matrices.scale(r/10, r/10, 1);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45));
			fill(matrices, -10, -10, 10, 10, 0xFF000000);
			fill(matrices, -12, -12, 12, 12, 0x33000000);
			fill(matrices, -16, -16, 16, 16, 0x33000000);
			fill(matrices, -20, -20, 20, 20, 0x33000000);
			matrices.pop();
		} else {
			RenderSystem.clearColor(0, 0, 0, 1);
			RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
		}
		float sa = 1;
		if (isClosing) {
			sa = sCurve5(1-MathHelper.clamp(ct/5f, 0, 1));
		}
		if (time > 15 && sa > 0) {
			sr.setAlpha(sa);
			sr.setUp();
			sr.drawText(matrices, "yttr setup utility", width-113, 6, delta);
			if (currentSection == null) {
				int y = 6;
				for (Section s : Section.values()) {
					if (drawButton(matrices, Ascii.toLowerCase(s.name()), 6, y, delta)) {
						currentSection = s;
						uniq = ThreadLocalRandom.current().nextInt();
					}
					y += 24;
				}
			} else {
				drawHeading(matrices, Ascii.toLowerCase(currentSection.name()), 6, 6, delta);
				if (currentSection == Section.GENERAL) {
					drawBoolean(matrices, "trust players", YConfig.General.trustPlayers, 6, 24, delta);
					drawBoolean(matrices, "fixup debug world", YConfig.General.trustPlayers, 6, 46, delta);
					drawBoolean(matrices, "shenanigans", YConfig.General.shenanigans, 6, 68, delta);
				} else if (currentSection == Section.CLIENT) {
					drawBoolean(matrices, "slope smoothing", YConfig.Client.slopeSmoothing, 6, 24, delta);
					drawEnum(matrices, "force opengl core", YConfig.Client.openglCompatibility, 6, 46, delta);
				} else if (currentSection == Section.RIFLE) {
					drawBoolean(matrices, "allow void", YConfig.Rifle.allowVoid, 6, 24, delta);
					drawEnum(matrices, "allow explode", YConfig.Rifle.allowExplode, 6, 46, delta);
					drawBoolean(matrices, "allow fire", YConfig.Rifle.allowFire, 6, 68, delta);
				} else if (currentSection == Section.WORLDGEN) {
					drawBoolean(matrices, "gadolinite", YConfig.WorldGen.gadolinite, 6, 24, delta);
					drawBoolean(matrices, "brookite", YConfig.WorldGen.brookite, 6, 46, delta);
					drawBoolean(matrices, "squeeze trees", YConfig.WorldGen.squeezeTrees, 6, 68, delta);
					drawBoolean(matrices, "wasteland", YConfig.WorldGen.wasteland, 6, 96, delta);
					drawBoolean(matrices, "core lava", YConfig.WorldGen.coreLava, 6, 124, delta);
					drawBoolean(matrices, "scorched", YConfig.WorldGen.scorched, 6, 146, delta);
					drawBoolean(matrices, "scorched retrogen", YConfig.WorldGen.scorchedRetrogen, 6, 168, delta);
				}
			}
			
			if (drawButton(matrices, currentSection != null ? "back" : "done", "cornerbutton", width-110, height-30, 100, delta)) {
				if (currentSection != null) {
					currentSection = null;
					uniq = ThreadLocalRandom.current().nextInt();
				} else {
					onClose();
				}
			}
			sr.tearDown();
		}
		super.render(matrices, mouseX, mouseY, delta);
		clicked = false;
	}

	private void drawHeading(MatrixStack matrices, String text, int x, int y, float delta) {
		sr.drawText(matrices, uniq+text, text, x, y, delta);
		matrices.push();
		matrices.translate(x, 0, 0);
		matrices.scale(2, 1, 1);
		sr.drawElement(matrices, text+"separator"+uniq, 0, y+14, 0, 30, 80, 1, delta);
		matrices.pop();
	}
	
	private void drawBoolean(MatrixStack matrices, String label, boolean value, int x, int y, float delta) {
		sr.drawText(matrices, uniq+label, label, x, y+5, delta);
		drawButton(matrices, value ? "on" : "off", label+value+uniq, x+110, y, 40, delta);
	}
	
	private void drawEnum(MatrixStack matrices, String label, Enum<?> value, int x, int y, float delta) {
		sr.drawText(matrices, uniq+label, label, x, y+5, delta);
		drawButton(matrices, Ascii.toLowerCase(value.name()), label+value+uniq, x+110, y, 40, delta);
	}

	private boolean drawButton(MatrixStack matrices, String text, int x, int y, float delta) {
		return drawButton(matrices, text, text, x, y, 100, delta);
	}
	
	private boolean drawButton(MatrixStack matrices, String text, String id, int x, int y, int w, float delta) {
		matrices.push();
		matrices.translate(x, 0, 0);
		if (w > 50) {
			matrices.scale(w/50f, 1, 1);
			sr.drawElement(matrices, id+"top", 0, y, 0, 30, 50, 1, delta);
			sr.drawElement(matrices, id+"bot", 0, y+19, 0, 30, 50, 1, delta);
		} else {
			sr.drawElement(matrices, id+"top", 0, y, 0, 30, w, 1, delta);
			sr.drawElement(matrices, id+"bot", 0, y+19, 0, 30, w, 1, delta);
		}
		matrices.pop();
		sr.drawElement(matrices, id+"left", x, y, 80, 4, 1, 20, delta);
		sr.drawElement(matrices, id+"right", x+w-1, y, 80, 4, 1, 20, delta);
		int tw = 6*text.length();
		sr.drawText(matrices, id+uniq, text, x+(w-tw)/2, y+5, delta);
		return clicked && mouseX >= x && mouseX <= x+100 &&
				mouseY >= y && mouseY <= y+20;
	}
	
	@Override
	public void tick() {
		if (time == 0) {
			client.getSoundManager().play(PositionedSoundInstance.master(YSounds.EFFECTOR_OPEN, 1));
		}
		if (time == 10) {
			client.getSoundManager().play(music);
		}
		time++;
		if (isClosing) {
			closeTime++;
			if (closeTime > 20) {
				client.setScreen(parent);
				client.getMusicTracker().stop();
			}
		}
		sr.tick();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			clicked = true;
			this.mouseX = (int)mouseX;
			this.mouseY = (int)mouseY;
		}
		return true;
	}
	
	private static float sCurve5(float a) {
		float a3 = a * a * a;
		float a4 = a3 * a;
		float a5 = a4 * a;
		return (6 * a5) - (15 * a4) + (10 * a3);
	}

}
