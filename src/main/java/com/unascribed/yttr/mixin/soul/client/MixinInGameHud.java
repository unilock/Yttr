package com.unascribed.yttr.mixin.soul.client;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferRenderer;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.blaze3d.vertex.VertexFormats;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixin.accessor.client.AccessorClientPlayerInteractionManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorHeartType;
import com.unascribed.yttr.util.math.Bits;

import static org.lwjgl.opengl.GL11.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public class MixinInGameHud {

	private static final Identifier YTTR$BEET_ICONS = Yttr.id("textures/gui/beet_icons.png");
	private static final Identifier YTTR$GUI_ICONS = new Identifier("textures/gui/icons.png");
	
	private float yttr$lastEvaporateProgress;
	private float yttr$evaporateProgress;
	private boolean yttr$evaporating;
	private boolean yttr$impure;
	private int yttr$wasEvaporating;
	private boolean yttr$hide;
	private float yttr$lastMaxHealth;
	private int yttr$heartResolution;
	private Vector3f[] yttr$hearticulates; // x, y, start
	
	@Inject(at=@At("HEAD"), method="tick")
	private void yttr$tickEvaporate(boolean paused, CallbackInfo ci) {
		var mc = MinecraftClient.getInstance();
		yttr$lastEvaporateProgress = yttr$evaporateProgress;
		if (mc.world != null && mc.interactionManager instanceof AccessorClientPlayerInteractionManager acc
				&& mc.interactionManager.isBreakingBlock()
				&& mc.world.getBlockState(acc.yttr$getCurrentBreakingPos()).isOf(YBlocks.POLISHED_SCORCHED_OBSIDIAN_HOLSTER)) {
			yttr$evaporateProgress = acc.yttr$getCurrentBreakingProgress();
			yttr$wasEvaporating = yttr$evaporateProgress > 0.95 ? 30 : 0;
			yttr$lastMaxHealth = mc.player.getMaxHealth();
		} else {
			yttr$evaporateProgress = 0;
			yttr$heartResolution = 0;
			yttr$hearticulates = null;
		}
		if (yttr$evaporateProgress > 0) {
			if (yttr$hearticulates == null) {
				mc.getTextureManager().bindTexture(YTTR$GUI_ICONS);
				yttr$heartResolution = (glGetTexLevelParameteri(GL_TEXTURE_2D, 0, GL_TEXTURE_WIDTH)*9)/256;
				yttr$hearticulates = new Vector3f[yttr$heartResolution*yttr$heartResolution];
				var r = ThreadLocalRandom.current();
				for (int i = 0; i < yttr$hearticulates.length; i++) {
					float y = ((i/yttr$heartResolution)+1)/(float)yttr$heartResolution;
					yttr$hearticulates[i] = new Vector3f((float)r.nextGaussian(0, 4), -r.nextFloat(12), r.nextFloat()*r.nextFloat(y));
				}
			}
		}
		yttr$wasEvaporating--;
	}
	
	@ModifyVariable(at=@At("HEAD"), method="renderHealthBar",
			ordinal=0, argsOnly=true)
	private float yttr$modifyMaxHealth(float maxHealth, GuiGraphics graphics, PlayerEntity player) {
		var mc = MinecraftClient.getInstance();
		if (player == mc.player) {
			maxHealth += Integer.bitCount(YttrClient.soulImpurity);
		}
		return maxHealth;
	}
	
	@ModifyVariable(at=@At("HEAD"), method="renderHealthBar",
			ordinal=5, argsOnly=true)
	private int yttr$modifyHealth(int health, GuiGraphics graphics, PlayerEntity player) {
		var mc = MinecraftClient.getInstance();
		if (player == mc.player) {
			health = yttr$modifyHealth(health);
		}
		return health;
	}
	
	@ModifyVariable(at=@At("HEAD"), method="renderHealthBar",
			ordinal=4, argsOnly=true)
	private int yttr$modifyLastHealth(int health, GuiGraphics graphics, PlayerEntity player) {
		var mc = MinecraftClient.getInstance();
		if (player == mc.player) {
			health = yttr$modifyHealth(health);
		}
		return health;
	}
	
	private int yttr$modifyHealth(int health) {
		for (int i = 0; i < Math.ceil(health/2); i++) {
			if (Bits.get(YttrClient.soulImpurity, i)) {
				health++;
			}
		}
		return health;
	}
	
	@Inject(at=@At(value="FIELD", target="net/minecraft/client/gui/hud/InGameHud$HeartType.CONTAINER:Lnet/minecraft/client/gui/hud/InGameHud$HeartType;"),
			method="renderHealthBar", locals=LocalCapture.CAPTURE_FAILHARD)
	private void yttr$contextualizeHeart(GuiGraphics ctx, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking,
			CallbackInfo ci, @Coerce Object unused0, int unused1, int maxHearts, int unused2, int unused3, int i) {
		yttr$evaporating = (yttr$hearticulates != null && i == (maxHearts-1));
		yttr$hide = false;
		yttr$impure = Bits.get(YttrClient.soulImpurity, i);
		if (!yttr$evaporating && yttr$wasEvaporating > 0) {
			float cutoff = player.getMaxHealth();
			if (player.getMaxHealth() == yttr$lastMaxHealth) {
				cutoff -= 2;
			}
			if ((i*2) >= cutoff) {
				yttr$hide = true;
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="drawHeart", cancellable=true)
	private void yttr$drawHeart(GuiGraphics ctx, @Coerce AccessorHeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo ci) {
		if (yttr$hide) {
			ci.cancel();
			return;
		}

		MatrixStack matrices = ctx.getMatrices();
		if (!yttr$evaporating && yttr$impure && type.yttr$getU(false, false) >= 52) {
			var mc = MinecraftClient.getInstance();
			float td = mc.getTickDelta();
			float vel = ((float)mc.player.getVelocity().horizontalLength()*5);
			float speed = 40;
			speed -= vel;
			if (speed < 1) speed = 1;
			float t = ((mc.player.age+td)/speed)+x;
			float sloshiness = 27+(vel*25);
			var yawr = (float)Math.toRadians(mc.player.getYaw(td));
			var horizLook = new Vec3d(MathHelper.sin(-yawr), 0, MathHelper.cos(-yawr));
			var crossVel = horizLook.crossProduct(mc.player.getVelocity());
			int sloshX = Math.round(
					(
						((MathHelper.lerp(td, mc.player.lastRenderYaw, mc.player.renderYaw)-mc.player.getYaw(td))*1.5f)
						+(MathHelper.sin(t)*sloshiness)
						-((float)crossVel.y*400)
					)/25);
			int sloshY = Math.round(
					(
						(-(MathHelper.lerp(td, mc.player.lastRenderPitch, mc.player.renderPitch)-mc.player.getPitch(td)))
						-((float)mc.player.getVelocity().y*100)
					)/25);
			boolean revSlosh = sloshX < 0;
			sloshX = Math.min(Math.abs(sloshX), 8);
			sloshY = Math.min(Math.max(sloshY, 0), 4);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			ctx.drawTexture(YTTR$BEET_ICONS, x, y, 47, type.yttr$getU(false, blinking), v, 9, 9, 256, 256);
			RenderSystem.disableCull();
			if (revSlosh) {
				matrices.push();
					matrices.translate(x, 0, 0);
					matrices.scale(-1, 1, 1);
					ctx.drawTexture(YTTR$BEET_ICONS, -9, y, 47, sloshY*9, 72-(sloshX*9), 9, 9, 256, 256);
				matrices.pop();
			} else {
				ctx.drawTexture(YTTR$BEET_ICONS, x, y, 47, sloshY*9, 72-(sloshX*9), 9, 9, 256, 256);
			}
			RenderSystem.enableCull();
			ci.cancel();
			return;
		}
		if (yttr$evaporating) {
			var mc = MinecraftClient.getInstance();
			var bldr = Tessellator.getInstance().getBufferBuilder();
			var mat = matrices.peek().getModel();
			float f = 9f/yttr$heartResolution;
			int u = type.yttr$getU(halfHeart, blinking);
			float prog = MathHelper.lerp(mc.getTickDelta(), yttr$lastEvaporateProgress, yttr$evaporateProgress);
			if (yttr$impure && type.yttr$getU(false, false) >= 52) {
				u = 52;
				v = 9;
				RenderSystem.setShaderTexture(0, YTTR$BEET_ICONS);
			}
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderColor(1, 1, 1, 1);
			bldr.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
			for (int xi = 0; xi < yttr$heartResolution; xi++) {
				for (int yi = 0; yi < yttr$heartResolution; yi++) {
					var params = yttr$hearticulates[(yi*yttr$heartResolution)+xi];
					float start = params.z();
					float a;
					if (prog > start) {
						float span = 1-start;
						if (span != 1) {
							a = 1-((prog-start)/span);
						} else {
							a = prog;
						}
					} else {
						a = 1;
					}
					if (a <= 0) continue;
					float ai = 1-a;
					float xo = params.x()*ai;
					float yo = params.y()*ai;
					
					float xif = xi*f;
					float yif = yi*f;
					
					float x0 = x+(xif)+xo;
					float x1 = x0+f;
					float y0 = y+(yif)+yo;
					float y1 = y0+f;
					
					float u0 = (u + (xif)) / 256f;
					float u1 = (u + (xif+f)) / 256f;
					float v0 = (v + (yif)) / 256f;
					float v1 = (v + (yif+f)) / 256f;

					bldr.vertex(mat, x0, y1, 0).uv(u0, v1).color(1, 1, 1, a).next();
					bldr.vertex(mat, x1, y1, 0).uv(u1, v1).color(1, 1, 1, a).next();
					bldr.vertex(mat, x1, y0, 0).uv(u1, v0).color(1, 1, 1, a).next();
					bldr.vertex(mat, x0, y0, 0).uv(u0, v0).color(1, 1, 1, a).next();
				}
			}
			BufferRenderer.drawWithShader(bldr.end());
			RenderSystem.setShaderTexture(0, YTTR$GUI_ICONS);
			ci.cancel();
		}
	}

}
