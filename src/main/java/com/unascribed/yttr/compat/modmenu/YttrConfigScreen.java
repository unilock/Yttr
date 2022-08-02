package com.unascribed.yttr.compat.modmenu;

import com.unascribed.yttr.client.suit.SuitRenderer;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.LampColor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class YttrConfigScreen extends Screen {

	private final Screen parent;
	private int time;
	private int closeTime;
	private boolean isClosing;
	private int initialMouseX = -1;
	private int initialMouseY = -1;
	
	private int mouseX, mouseY;
	private boolean clicked;
	
	private final SuitRenderer sr = new SuitRenderer();
	
	public YttrConfigScreen(Screen parent) {
		super(new LiteralText("Yttr configuration"));
		this.parent = parent;
		sr.setColor(LampColor.TEAL);
	}
	
	@Override
	public void onClose() {
		if (isClosing) {
			client.setScreen(parent);
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
		} else {
			a = sCurve5(MathHelper.clamp(t/10f, 0, 1));
		}
		if (a < 1) {
			parent.render(matrices, -200, -200, delta);
		}
		if (initialMouseX == -1) {
			initialMouseX = mouseX;
			initialMouseY = mouseY;
		}
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
		float sa = 1;
		if (isClosing) {
			sa = sCurve5(1-MathHelper.clamp(ct/5f, 0, 1));
		}
		if (time > 15 && sa > 0) {
			sr.setAlpha(sa);
			sr.setUp();
			sr.drawText(matrices, "yttr configuration menu", 6, 6, delta);
			matrices.push();
			matrices.translate(6, 0, 0);
			matrices.scale(3, 1, 1);
			sr.drawElement(matrices, "separator", 0, 20, 0, 30, 80, 1, delta);
			matrices.pop();
			if (drawButton(matrices, "done", width-110, height-30, delta)) {
				onClose();
			}
			sr.tearDown();
		}
		super.render(matrices, mouseX, mouseY, delta);
		clicked = false;
	}
	
	private boolean drawButton(MatrixStack matrices, String text, int x, int y, float delta) {
		matrices.push();
		matrices.translate(x, 0, 0);
		matrices.scale(2, 1, 1);
		sr.drawElement(matrices, text+"top", 0, y, 0, 30, 50, 1, delta);
		sr.drawElement(matrices, text+"bot", 0, y+20, 0, 30, 50, 1, delta);
		matrices.pop();
		sr.drawElement(matrices, text+"left", x, y, 80, 4, 1, 20, delta);
		sr.drawElement(matrices, text+"right", x+99, y, 80, 4, 1, 20, delta);
		int width = 6*text.length();
		sr.drawText(matrices, text, x+(100-width)/2, y+6, delta);
		return clicked && mouseX >= x && mouseX <= x+100 &&
				mouseY >= y && mouseY <= y+20;
	}
	
	@Override
	public void tick() {
		if (time == 0) {
			client.getSoundManager().play(PositionedSoundInstance.master(YSounds.EFFECTOR_OPEN, 1));
		}
		time++;
		if (isClosing) {
			closeTime++;
			if (closeTime > 20) {
				client.setScreen(parent);
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
