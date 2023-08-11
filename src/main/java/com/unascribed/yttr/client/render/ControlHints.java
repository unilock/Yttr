package com.unascribed.yttr.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.mojang.blaze3d.platform.InputUtil.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.mixin.accessor.client.AccessorInGameHud;
import com.unascribed.yttr.mixin.accessor.client.AccessorKeyBind;
import com.unascribed.yttr.util.ControlHintable;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ControlHints {
	
	private static final Identifier ICONS = Yttr.id("textures/gui/controlicons.png");
	
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)\\}");

	private static String lastState;
	private static String curState;
	private static long lastStateChange;

	public static void render(GuiGraphics ctx, float tickDelta) {
		if (!YConfig.Client.controlHints) return;
		MatrixStack matrices = ctx.getMatrices();

		var mc = MinecraftClient.getInstance();
		if (mc.player != null) {
			var stack = mc.player.getMainHandStack();
			if (stack.getItem() instanceof ControlHintable || I18n.hasTranslation(stack.getTranslationKey()+".controlhint.normal.1")) {
				if (((AccessorInGameHud)mc.inGameHud).yttr$getCurrentStack().getItem() != stack.getItem()) {
					curState = null;
					return;
				}
				float fade = (((AccessorInGameHud)mc.inGameHud).yttr$getHeldItemTooltipFade()-tickDelta)/10f;
				if (fade < 0) fade = 0;
				if (fade > 1) fade = 1;
				fade = 1-fade;
				
				if (fade < 0.03f) return;
				
				var win = mc.getWindow();
				var winW = win.getScaledWidth();
				var winH = win.getScaledHeight();
				
				var sc = (int)win.getScaleFactor();
				
				if (FabricLoader.getInstance().isModLoaded("qdaa")) {
					sc /= 2;
				}
				
				int ofs = 59;
				if (!mc.interactionManager.hasStatusBars()) ofs -= 14;
				
				matrices.push();
				if (sc > 1) {
					// one gui scale step smaller
					float factor = (sc-1)/(float)sc;
					matrices.scale(factor, factor, 1);
					float rfactor = 1/factor;
					winW *= rfactor;
					winH *= rfactor;
					ofs *= rfactor;
				}
				
				boolean f = mc.options.swapHandsKey.isPressed() || mc.options.swapHandsKey.wasPressed();
				String state;
				if (stack.getItem() instanceof ControlHintable ch) {
					state = ch.getState(mc.player, stack, f);
				} else {
					state = "normal";
				}
				
				long time = System.nanoTime()/1_000_000L;
				
				if (curState == null || !curState.equals(state)) {
					lastState = curState;
					lastStateChange = curState == null ? 0 : time;
					curState = state;
				}
				
				float outTime = 3;
				float inTime = 5;
				
				float d = (time-lastStateChange)/50f;
				if (d < outTime) {
					fade = 1-(d/outTime);
					state = lastState;
				} else if (d < outTime+inTime) {
					fade = (d-outTime)/inTime;
				}
				
				String keyBase = stack.getTranslationKey()+".controlhint."+state;
				
				int y = winH - ofs;
				
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				
				if (fade < 0.05) return;
				
				int a = ((int)(fade*255))<<24;
				float af = fade;
				
				int i = 1;
				List<Runnable> components = new ArrayList<>();
				while (I18n.hasTranslation(keyBase+"."+i)) {
					components.clear();
					String str = I18n.translate(keyBase+"."+i);
					int w = 0;
					var m = PLACEHOLDER_PATTERN.matcher(str);
					int last = 0;
					while (m.find()) {
						KeyBind key = null;
						for (var k : mc.options.allKeys) {
							if (m.group(1).equals(k.getTranslationKey())) {
								key = k;
								break;
							}
						}
						if (last-m.start() != 0) {
							String s = str.substring(last, m.start());
							int sw = mc.textRenderer.getWidth(s);
							w += sw;
							components.add(() -> {
								ctx.drawShadowedText(mc.textRenderer, s, 0, 1, 0xFFFFFF|a);
								matrices.translate(sw, 0, 0);
							});
						}
						if (key != null) {
							var bk = ((AccessorKeyBind)key).yttr$getBoundKey();
							if (bk.getType() == Type.MOUSE && bk.getKeyCode() >= 0 && bk.getKeyCode() <= 2) {
								w += 10;
								components.add(() -> {
									RenderSystem.setShaderColor(1/2f, 1/2f, 1/6f, af);
									ctx.drawTexture(ICONS, 1, 1, bk.getKeyCode()*9, 0, 9, 9, 90, 9);
									RenderSystem.setShaderColor(1, 1, 1/3f, af);
									ctx.drawTexture(ICONS, 0, 0, 0, bk.getKeyCode()*9, 0, 9, 9, 90, 9);
									matrices.translate(10, 0, 0);
									RenderSystem.setShaderColor(1, 1, 1, 1);
								});
							} else {
								boolean mouse = bk.getType() == Type.MOUSE;
								String s = key == null ? "?" : key.getKeyName().getString();
								if (s.length() == 1) s = s.toUpperCase(Locale.ROOT);
								final String fs = "§l"+s;
								int sw = mc.textRenderer.getWidth(fs);
								w += sw+(mouse?0:2);
								components.add(() -> {
									if (!mouse) {
										ctx.fill(0, -1, sw+3, 10, 0x797928|a);
										ctx.fill(0, -1, sw+2, 8, 0xFFFF55|a);
										RenderSystem.enableBlend();
									}
									ctx.drawText(mc.textRenderer, fs, mouse?0:1, 0, 0x000000|a, false);
									matrices.translate(sw+2, 0, 0);
								});
							}
						}
						last = m.end();
					}
					if (last < str.length()) {
						String s = str.substring(last);
						int sw = mc.textRenderer.getWidth(s);
						w += sw;
						components.add(() -> {
							ctx.drawShadowedText(mc.textRenderer, s, 0, 1, 0xFFFFFF|a);
							matrices.translate(sw, 0, 0);
						});
					}
					var x = (winW-w)/2;
					matrices.push();
						matrices.translate(x, y, 0);
						ctx.fill(-2, -1, w+2, 11, mc.options.getTextBackgroundColor(0));
						RenderSystem.enableBlend();
						for (var r : components) {
							r.run();
						}
					matrices.pop();
					matrices.translate(0, -(12*fade), 0);
					i++;
				}
				matrices.pop();
			}
		}
	}

}
