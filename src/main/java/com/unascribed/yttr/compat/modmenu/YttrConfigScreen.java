package com.unascribed.yttr.compat.modmenu;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.registry.Holder;
import net.minecraft.util.math.Axis;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.YConfig.Trilean;
import com.unascribed.yttr.YConfig.TrileanSoft;
import com.unascribed.yttr.client.suit.SuitMusic;
import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.base.Ascii;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multiset;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class YttrConfigScreen extends Screen {

	public static final MusicSound MUSIC = new MusicSound(Holder.createDirect(YSounds.SILENCE), 10, 10, true);
	
	private enum Section {
		GENERAL,
		CLIENT,
		RIFLE,
		ENCHANTMENTS,
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
	private boolean rclicked;
	private Section currentSection = null;
	private Section prevSection = null;
	
	private int uniq;
	private int descUniq;
	private boolean drawingDesc;
	private boolean anyDescDrawnThisFrame;
	
	private boolean textTestMode = false;
	private StringBuilder textTestContent = new StringBuilder("hello, world!");
	
	private final SuitRenderer sr = new SuitRenderer();
	private final SuitMusic music = new SuitMusic(YSounds.TORUS, 0.6f, SoundCategory.MUSIC) {
		@Override
		public boolean isDone() {
			return !(MinecraftClient.getInstance().currentScreen instanceof YttrConfigScreen);
		}
	};
	
	private int sectionTicks = 0;
	
	private static final Map<String, String> shortDescs = ImmutableMap.<String, String>builder()
			.put("general.trust-players", "makes things more reliable despite lag, but makes cheating on servers easier")
			.put("general.fixup-debug-world", "adds missing modded blockstates to the vanilla debug world")
			.put("general.shenanigans", "inside-jokes and various chaos - nothing destructive")
			.put("general.convert-void-holes", "automatically convert non-yttr overworld void holes into geysers")
			.put("general.break-bedrock-anywhere", "allow breaking bedrock in any dimension at any y level instead of only the bottom of the overworld")
			
			.put("client.slope-smoothing", "attempts to smooth your camera when walking on slopes - a little buggy but cool when it works")
			.put("client.force-opengl-core", "force-disables opengl compatibility mode - not supported, may cause render bugs and crashes - restart required")
			.put("client.config-color", "customize the color of this very setup utility")
			.put("client.control-hints", "if on, show on-screen hints for nonstandard controls")
			
			.put("rifle.allow-void", "off disables the yttric rifle void mode to avoid griefing concerns - note there is a command to undo voids and voids are logged to console")
			.put("rifle.allow-explode", "off disables the yttric rifle explode mode to avoid griefing concerns - breaks progression - prefer soft to disable block damage")
			.put("rifle.allow-fire", "off disables the yttric rifle fire mode to avoid griefing concerns")
			.put("rifle.timing-assist", "if on, play a ding noise at 500kj and three dings leading up to 650kj while charging the rifle")
			
			.put("enchantments.vorpal", "if on, the vorpal weapon enchantment can be obtained - a level-dependent chance to deal a very large amount of damage on strike")
			.put("enchantments.disjunction", "if on, the disjunction weapon enchantment can be obtained - an analogue to smite for ender creatures")
			.put("enchantments.annihilation", "if on, the curse of annihilation tool enchantment can be obtained - completely destroys any items that would have been dropped")
			.put("enchantments.shattering", "if on, the curse of shattering tool enchantment can be obtained - \"shatters\" dropped items via various means")
			.put("enchantments.springing", "if on, the springing coil enchantment can be obtained - increases jump height")
			.put("enchantments.stabilization", "if on, the stabilization coil enchantment can be obtained - removes midair mining speed penalty")
			.put("enchantments.curses-in-table", "if on, yttr curses can be obtained via regular enchanting")
			
			.put("worldgen.gadolinite", "generates gadolinite in the overworld, a source of yttrium and iron - required for progression")
			.put("worldgen.brookite", "generates brookite ore in the overworld - required for some recipes, and future progression")
			.put("worldgen.squeeze-trees", "generates squeeze trees in deep oceans - required for some recipes")
			.put("worldgen.wasteland", "generates the wasteland biome in the overworld")
			.put("worldgen.core-lava", "replaces nether lower bedrock with core lava for lore consistency")
			.put("worldgen.scorched", "replaces nether ceiling with a brand new biome - required for future progression")
			.put("worldgen.scorched-retrogen", "performs scorched generation in existing chunks - will not destroy existing structures")
			.put("worldgen.continuity", "generates roots of continuity under small end islands - required for progression")
			
			.build();
	
	private static final Map<String, Object> badSettings = ImmutableMap.<String, Object>builder()
			.put("client.force-opengl-core", Trilean.ON)
			.put("rifle.allow-explode", TrileanSoft.OFF)
			.put("worldgen.gadolinite", false)
			.put("worldgen.continuity", false)
			.build();
	
	private final Multiset<String> timesModified = HashMultiset.create();
	
	public YttrConfigScreen(Screen parent) {
		super(Text.literal("Yttr configuration"));
		this.parent = parent;
	}
	
	@Override
	protected void init() {
		parent.init(client, width, height);
		super.init();
	}
	
	@Override
	public void closeScreen() {
		if (isClosing) {
			client.setScreen(parent);
			client.getMusicTracker().stopPlaying();
			return;
		}
		isClosing = true;
		client.getSoundManager().play(PositionedSoundInstance.master(YSounds.EFFECTOR_CLOSE, 1));
	}
	
	@Override
	public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		MatrixStack matrices = ctx.getMatrices();
		sr.setColor(YConfig.Client.configColor);
		anyDescDrawnThisFrame = false;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		float t = time+delta;
		float ct = closeTime+delta;
		float a;
		float sca;
		if (isClosing) {
			a = sCurve5(1-MathHelper.clamp(ct/20f, 0, 1));
			sca = sCurve5(1-MathHelper.clamp(ct/15f, 0, 1));
			music.setVolume(a*0.6f);
		} else {
			a = sCurve5(MathHelper.clamp(t/10f, 0, 1));
			sca = sCurve5(MathHelper.clamp(t/7f, 0, 1));
		}
		if (initialMouseX == -1) {
			initialMouseX = mouseX;
			initialMouseY = mouseY;
		}
		if (a < 1) {
			MatrixStack modelView = RenderSystem.getModelViewStack();
			matrices.push();
			modelView.push();
			modelView.translate(initialMouseX, initialMouseY, 0);
			modelView.scale(1+(sca/2), 1+(sca/2), 1);
			modelView.translate(-initialMouseX, -initialMouseY, -600);
			RenderSystem.applyModelViewMatrix();
			parent.render(ctx, -200, -200, delta);
			modelView.pop();
			RenderSystem.applyModelViewMatrix();
			matrices.pop();
			float r = (Math.max(initialMouseX, height-initialMouseY)*1.1f)*a;
			matrices.push();
			matrices.translate(initialMouseX, initialMouseY, 0);
			matrices.scale(r/10, r/10, 1);
			matrices.multiply(Axis.Z_POSITIVE.rotationDegrees(45));
			ctx.fill(-10, -10, 10, 10, 0xFF000000);
			ctx.fill(-12, -12, 12, 12, 0x33000000);
			ctx.fill(-16, -16, 16, 16, 0x33000000);
			ctx.fill(-20, -20, 20, 20, 0x33000000);
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
			if (textTestMode) {
				String content = textTestContent.toString();
				int x = width/4;
				int y = 3;
				matrices.push();
				matrices.scale(2, 2, 1);
				for (String line : content.split("\n")) {
					if (time-15 > y) {
						sr.drawText(ctx, "test"+uniq+y, line, x-(line.length()*3), y, delta);
					}
					y += 12;
				}
				matrices.pop();
				
				if (drawButton(ctx, "reset", 0, "reset", width-180, height-24, 80, delta)) {
					uniq++;
					if (rclicked) {
						time = 15;
					}
				}
			} else if (currentSection == null) {
				drawHeading(ctx, "sections", 6, 6, delta);
				int y = 26;
				for (Section s : Section.values()) {
					if (sectionTicks >= y/8 && drawButton(ctx, s.name()+uniq, uniq, Ascii.toLowerCase(s.name()), 6, y, 80, delta)) {
						currentSection = s;
						sectionTicks = 0;
						uniq = ThreadLocalRandom.current().nextInt();
					}
					y += 22;
				}
				y += 8;
				if (sectionTicks >= y/8) drawHeading(ctx, "extras", 6, y, delta);
				y += 20;
				if (sectionTicks >= y/8 && drawButton(ctx, "texttest"+uniq, uniq, "text test", 6, y, 80, delta)) {
					textTestMode = true;
				}
			} else {
				String currentSectionStr = Ascii.toLowerCase(currentSection.name());
				drawHeading(ctx, currentSectionStr, 6, 6, delta);
				int y = 24;
				for (String k : YConfig.data.keySet()) {
					if (sectionTicks < y/8) break;
					int dot = k.indexOf('.');
					String section = k.substring(0, dot);
					if (section.equals(currentSectionStr)) {
						String name = k.substring(dot+1);
						Class<?> type = YConfig.getKeyType(k);
						if (type == void.class) continue;
						Object objV = null;
						String label = name.replace('-', ' ');
						if (label.equals("break bedrock anywhere")) {
							label = "break any bedrock";
						}
						if (type == boolean.class) {
							boolean v = YConfig.data.getBoolean(k).get();
							if (drawBoolean(ctx, label, v, 6, y, shortDescs.getOrDefault(k, "no description"), delta)) {
								v = !v;
								if (badSettings.get(k) == Boolean.valueOf(v)) {
									client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DANGER, 1, 1));
									client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DANGER, 1, 0.6f));
								}
								timesModified.add(label);
								YConfig.data.put(k, v ? "on" : "off");
								YConfig.copyDataToFields();
								YConfig.save();
							}
							objV = v;
						} else if (type.isEnum()) {
							Enum<?> v = (Enum<?>)YConfig.data.getEnum(k, (Class)type).get();
							if (drawEnum(ctx, label, v, 6, y, shortDescs.getOrDefault(k, "no description"), delta)) {
								int idx;
								if (rclicked) {
									idx = (v.ordinal()-1);
									if (idx < 0) idx = type.getEnumConstants().length+idx;
								} else {
									idx = (v.ordinal()+1)%type.getEnumConstants().length;
								}
								v = (Enum<?>) type.getEnumConstants()[idx];
								if (badSettings.get(k) == v) {
									client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DANGER, 1, 1));
									client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DANGER, 1, 0.6f));
								}
								timesModified.add(label);
								YConfig.data.put(k, Ascii.toLowerCase(v.name()));
								YConfig.copyDataToFields();
								YConfig.save();
							}
							objV = v;
						}
						if (badSettings.get(k) == objV) {
							drawDescription(ctx, 170, y+2, 18, 18, "this setting is not supported and may cause problems", delta);
							sr.drawElement(ctx, "ogl-warning"+uniq+timesModified.count(label), 170, y+2, 0, 18, 11, 12, delta);
						}
						y += 22;
					}
				}
				drawDescription(ctx, 6, 2, 18, 18, "go back to the section list", delta);
				if (drawButton(ctx, "back"+uniq, uniq, "<", 6, 2, 18, delta)) {
					currentSection = null;
					sectionTicks = 0;
					uniq = ThreadLocalRandom.current().nextInt();
				}
			}
			
			if (!textTestMode && !anyDescDrawnThisFrame) {
				String v = FabricLoader.getInstance().getModContainer("yttr").get().getMetadata().getVersion().getFriendlyString();
				if (v.equals("${version}")) {
					v = "dev";
				}
				sr.drawText(ctx, "setup"+descUniq, "yttr "+v+" setup utility", width-113-((v.length()+1)*6), 6, delta);
				if (currentSection != null) {
					sr.drawText(ctx, "setup2"+descUniq+uniq, "hover for descriptions", width-137, 17, delta);
				}
			}
			
			if (drawButton(ctx, "done", 0, "done", width-90, height-24, 80, delta)) {
				if (textTestMode) {
					textTestMode = false;
					uniq++;
				} else {
					closeScreen();
				}
			}
			sr.tearDown();
		}
		super.render(ctx, mouseX, mouseY, delta);
		clicked = false;
		rclicked = false;
		if (!anyDescDrawnThisFrame) {
			drawingDesc = false;
		}
	}

	private void drawHeading(GuiGraphics ctx, String text, int x, int y, float delta) {
		var matrices = ctx.getMatrices();
		sr.drawText(ctx, uniq+text, text, currentSection == null ? x : x+22, y, delta);
		matrices.push();
		matrices.translate(x, 0, 0);
		matrices.scale(2, 1, 1);
		sr.drawElement(ctx, "separator", 0, y+15, 0, 30, 80, 1, delta);
		matrices.pop();
	}
	
	private boolean drawBoolean(GuiGraphics ctx, String label, boolean value, int x, int y, String desc, float delta) {
		drawDescription(ctx, x, y, 160, 16, desc, delta);
		sr.drawText(ctx, uniq+label, label, x, y+4, delta);
		return drawButton(ctx, label, uniq+timesModified.count(label), value ? "on" : "off", x+120, y, 40, delta);
	}
	
	private boolean drawEnum(GuiGraphics ctx, String label, Enum<?> value, int x, int y, String desc, float delta) {
		drawDescription(ctx, x, y, 160, 16, desc, delta);
		sr.drawText(ctx, uniq+label, label, x, y+4, delta);
		String display;
		if (value instanceof LampColor lc) {
			display = switch (lc) {
				case COLORLESS -> "warm";
				case LIGHT_BLUE -> "lblue";
				case LIGHT_GRAY -> "silver";
				case MAGENTA -> "mgnta";
				default -> Ascii.toLowerCase(value.name());
			};
		} else {
			display = Ascii.toLowerCase(value.name());
		}
		return drawButton(ctx, label, uniq+timesModified.count(label), display, x+120, y, 40, delta);
	}

	private void drawDescription(GuiGraphics ctx, int x, int y, int w, int h, String desc, float delta) {
		if (mouseX >= x && mouseX <= x+w &&
				mouseY >= y && mouseY <= y+h) {
			anyDescDrawnThisFrame = true;
			if (!drawingDesc) {
				drawingDesc = true;
				descUniq++;
			}
			int initialX = width-130;
			x = initialX;
			int i = 0;
			y = 8;
			for (String word : desc.split(" ")) {
				int wordW = (word.length()+1)*6;
				int x2 = x + wordW - 6;
				if (x2 >= width) {
					x = initialX;
					y += 12;
				}
				sr.drawText(ctx, "description"+i+descUniq+desc.hashCode(), word, x, y, delta);
				x += wordW;
				i++;
			}
		}
	}
	
	private boolean drawButton(GuiGraphics ctx, String id, int uniq, String text, int x, int y, int w, float delta) {
		var matrices = ctx.getMatrices();
		matrices.push();
		matrices.translate(x, 0, 0);
		int uniq2 = (uniq == 0 ? 0 : this.uniq);
		if (w > 50) {
			matrices.scale(w/50f, 1, 1);
			sr.drawElement(ctx, id+"top"+uniq2, 0, y, 0, 30, 50, 1, delta);
			sr.drawElement(ctx, id+"bot"+uniq2, 0, y+16, 0, 30, 50, 1, delta);
		} else {
			sr.drawElement(ctx, id+"top"+uniq2, 0, y, 0, 30, w, 1, delta);
			sr.drawElement(ctx, id+"bot"+uniq2, 0, y+16, 0, 30, w, 1, delta);
		}
		matrices.pop();
		sr.drawElement(ctx, id+"left"+uniq2, x, y, 80, 4, 1, 16, delta);
		sr.drawElement(ctx, id+"right"+uniq2, x+w-1, y, 80, 4, 1, 16, delta);
		int tw = 6*text.length();
		sr.drawText(ctx, id+uniq, text, x+(w-tw)/2, y+4, delta);
		boolean rtrn = clicked && mouseX >= x && mouseX <= x+w &&
				mouseY >= y && mouseY <= y+16;
		if (rtrn) {
			client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DIVE_THRUST, ThreadLocalRandom.current().nextFloat(1.4f, 1.8f), 1));
		}
		return rtrn;
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
				client.getMusicTracker().stopPlaying();
			}
		}
		sr.tick();
		sectionTicks++;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 || button == 1) {
			clicked = true;
			rclicked = button == 1;
			this.mouseX = (int)mouseX;
			this.mouseY = (int)mouseY;
		}
		if (button == 3 && currentSection != null) {
			currentSection = null;
			sectionTicks = 0;
			uniq = ThreadLocalRandom.current().nextInt();
			client.getSoundManager().play(PositionedSoundInstance.master(YSounds.DIVE_THRUST, ThreadLocalRandom.current().nextFloat(1.4f, 1.8f), 1));
		}
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_ENTER) {
			textTestContent.append("\n");
		} else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && textTestContent.length() > 0) {
			textTestContent.setLength(textTestContent.length()-1);
		}
		return true;
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (textTestMode) {
			textTestContent.append(Ascii.toLowerCase(chr));
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
