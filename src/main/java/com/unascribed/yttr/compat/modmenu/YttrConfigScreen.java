package com.unascribed.yttr.compat.modmenu;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.client.suit.SuitMusic;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.base.Ascii;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

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
	private Section prevSection = null;
	
	private int uniq;
	private int descUniq;
	private boolean drawingDesc;
	private boolean anyDescDrawnThisFrame;
	
	private final SuitRenderer sr = new SuitRenderer();
	private final SuitMusic music = new SuitMusic(YSounds.TORUS, 1, SoundCategory.MUSIC) {
		@Override
		public boolean isDone() {
			return !(MinecraftClient.getInstance().currentScreen instanceof YttrConfigScreen);
		}
	};
	
	private static final Map<String, String> shortDescs = ImmutableMap.<String, String>builder()
			.put("general.trust-players", "makes things more reliable despite lag, but makes cheating on servers easier")
			.put("general.fixup-debug-world", "adds missing modded blockstates to the vanilla debug world")
			.put("general.shenanigans", "inside-jokes and various chaos - nothing destructive")
			
			.put("client.slope-smoothing", "attempts to smooth your camera when walking on slopes - a little buggy but cool when it works")
			.put("client.force-opengl-core", "force-disables opengl compatibility mode - not supported, may cause render bugs and crashes - restart required")
			
			.put("rifle.allow-void", "disables the yttric rifle void mode to avoid griefing concerns - note there is a command to undo voids and voids are logged to console")
			.put("rifle.allow-explode", "disables the yttric rifle explode mode to avoid griefing concerns - breaks progression - prefer soft to disable block damage")
			.put("rifle.allow-fire", "disables the yttric rifle fire mode to avoid griefing concerns")
			
			.put("worldgen.gadolinite", "generates gadolinite in the overworld, a source of yttrium and iron - required for progression")
			.put("worldgen.brookite", "generates brookite ore in the overworld - required for some recipes, and future progression")
			.put("worldgen.squeeze-trees", "generates squeeze trees in deep oceans - required for some recipes")
			.put("worldgen.wasteland", "generates the wasteland biome in the overworld")
			.put("worldgen.core-lava", "replaces nether lower bedrock with core lava for lore consistency")
			.put("worldgen.scorched", "replaces nether ceiling with a brand new biome - required for future progression")
			.put("worldgen.scorched-retrogen", "performs scorched generation in existing chunks - will not destroy existing structures")
			.put("worldgen.continuity", "generates roots of continuity under small end islands - required for progression")
			
			.build();
	
	private final Multiset<String> timesModified = HashMultiset.create();
	
	public YttrConfigScreen(Screen parent) {
		super(new LiteralText("Yttr configuration"));
		this.parent = parent;
		sr.setColor(LampColor.TEAL);
	}
	
	@Override
	protected void init() {
		parent.init(client, width, height);
		super.init();
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
		anyDescDrawnThisFrame = false;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
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
				String currentSectionStr = Ascii.toLowerCase(currentSection.name());
				drawHeading(matrices, currentSectionStr, 6, 6, delta);
				int y = 24;
				for (String k : YConfig.data.keySet()) {
					int dot = k.indexOf('.');
					String section = k.substring(0, dot);
					if (section.equals(currentSectionStr)) {
						String name = k.substring(dot+1);
						Class<?> type = YConfig.getKeyType(k);
						if (type == void.class) continue;
						if (type == boolean.class) {
							boolean v = YConfig.data.getBoolean(k).get();
							String label = name.replace('-', ' ');
							if (drawBoolean(matrices, label, v, 6, y, shortDescs.getOrDefault(k, "no description"), delta)) {
								v = !v;
								timesModified.add(label);
								YConfig.data.put(k, v ? "on" : "off");
								YConfig.copyDataToFields();
								YConfig.save();
							}
						} else if (type.isEnum()) {
							Enum<?> v = (Enum<?>)YConfig.data.getEnum(k, (Class)type).get();
							String label = name.replace('-', ' ');
							if (drawEnum(matrices, label, v, 6, y, shortDescs.getOrDefault(k, "no description"), delta)) {
								v = (Enum<?>) type.getEnumConstants()[(v.ordinal()+1)%type.getEnumConstants().length];
								timesModified.add(label);
								YConfig.data.put(k, Ascii.toLowerCase(v.name()));
								YConfig.copyDataToFields();
								YConfig.save();
							}
						}
						y += 22;
					}
				}
			}
			
			if (!anyDescDrawnThisFrame) {
				sr.drawText(matrices, "setup"+descUniq, "yttr setup utility", width-113, 6, delta);
			}
			
			if (drawButton(matrices, "cornerbutton", uniq, currentSection != null ? "back" : "done", width-110, height-30, 100, delta)) {
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
		if (!anyDescDrawnThisFrame) {
			drawingDesc = false;
		}
	}

	private void drawHeading(MatrixStack matrices, String text, int x, int y, float delta) {
		sr.drawText(matrices, uniq+text, text, x, y, delta);
		matrices.push();
		matrices.translate(x, 0, 0);
		matrices.scale(2, 1, 1);
		sr.drawElement(matrices, text+"separator"+uniq, 0, y+14, 0, 30, 80, 1, delta);
		matrices.pop();
	}
	
	private boolean drawBoolean(MatrixStack matrices, String label, boolean value, int x, int y, String desc, float delta) {
		drawDescription(matrices, x, y, desc, delta);
		sr.drawText(matrices, uniq+label, label, x, y+5, delta);
		return drawButton(matrices, label, uniq+timesModified.count(label), value ? "on" : "off", x+110, y, 40, delta);
	}
	
	private boolean drawEnum(MatrixStack matrices, String label, Enum<?> value, int x, int y, String desc, float delta) {
		drawDescription(matrices, x, y, desc, delta);
		sr.drawText(matrices, uniq+label, label, x, y+5, delta);
		return drawButton(matrices, label, uniq+timesModified.count(label), Ascii.toLowerCase(value.name()), x+110, y, 40, delta);
	}

	private void drawDescription(MatrixStack matrices, int x, int y, String desc, float delta) {
		if (mouseX >= x && mouseX <= x+160 &&
				mouseY >= y && mouseY <= y+20) {
			anyDescDrawnThisFrame = true;
			if (!drawingDesc) {
				drawingDesc = true;
				descUniq++;
			}
			int initialX = width-120;
			x = initialX;
			int i = 0;
			y = 8;
			for (String word : desc.split(" ")) {
				int w = (word.length()+1)*6;
				int x2 = x + w;
				if (x2 >= width) {
					x = initialX;
					y += 10;
				}
				sr.drawText(matrices, "description"+i+descUniq+desc.hashCode(), word, x, y, delta);
				x += w;
				i++;
			}
		}
	}

	private boolean drawButton(MatrixStack matrices, String text, int x, int y, float delta) {
		return drawButton(matrices, text, uniq, text, x, y, 100, delta);
	}
	
	private boolean drawButton(MatrixStack matrices, String id, int uniq, String text, int x, int y, int w, float delta) {
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
