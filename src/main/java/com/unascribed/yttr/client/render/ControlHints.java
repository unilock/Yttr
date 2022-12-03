package com.unascribed.yttr.client.render;

import java.util.Locale;
import java.util.regex.Pattern;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.mixin.accessor.client.AccessorInGameHud;
import com.unascribed.yttr.util.ControlHintable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;

public class ControlHints {
	
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)\\}");

	private static String lastState;
	private static String curState;
	private static long lastStateChange;

	public static void render(MatrixStack matrices, float tickDelta) {
		if (!YConfig.Client.controlHints) return;
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
				var w = win.getScaledWidth();
				var h = win.getScaledHeight();
				
				var sc = (int)win.getScaleFactor();
				
				int ofs = 59;
				if (!mc.interactionManager.hasStatusBars()) ofs -= 14;
				
				matrices.push();
				if (sc > 1) {
					// one gui scale step smaller
					float factor = (sc-1)/(float)sc;
					matrices.scale(factor, factor, 1);
					float rfactor = 1/factor;
					w *= rfactor;
					h *= rfactor;
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
				
				int y = h - ofs;
				
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				
				int a = (int)(fade*255);
				
				if (a <= 10) return;
				
				int col = 0x00FFFFFF | (a << 24);
				
				int i = 1;
				var buf = new StringBuilder();
				while (I18n.hasTranslation(keyBase+"."+i)) {
					buf.setLength(0);
					String str = I18n.translate(keyBase+"."+i);
					var m = PLACEHOLDER_PATTERN.matcher(str);
					while (m.find()) {
						KeyBind key = null;
						for (var k : mc.options.allKeys) {
							if (m.group(1).equals(k.getTranslationKey())) {
								key = k;
								break;
							}
						}
						String s = key == null ? "?" : key.getKeyName().getString();
						if (s.length() == 1) s = s.toUpperCase(Locale.ROOT);
						m.appendReplacement(buf, "§e§l"+s+"§r");
					}
					m.appendTail(buf);
					String s = buf.toString();
					var sw = mc.textRenderer.getWidth(s);
					var x = (w-sw)/2;
					DrawableHelper.fill(matrices, x-2, y-2, x+sw+2, y+10, mc.options.getTextBackgroundColor(0));
					mc.textRenderer.drawWithShadow(matrices, s, x, y, col);
					matrices.translate(0, -(12*fade), 0);
					i++;
				}
				matrices.pop();
			}
		}
	}

}
