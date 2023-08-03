package com.unascribed.yttr.mixin.void_fluid.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.VoidBucketItem;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class MixinHandledScreen extends Screen {

	protected MixinHandledScreen(Text title) {
		super(title);
	}

	@Shadow @Final
	protected ScreenHandler handler;
	
	@Nullable @Shadow
	protected Slot focusedSlot;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/gui/screen/ingame/HandledScreen.drawForeground(Lnet/minecraft/client/gui/GuiGraphics;II)V"),
			method="render")
	public void yttr$renderBeforeForeground(GuiGraphics ctx, int mX, int mY, float tickDelta, CallbackInfo ci) {
		if (VoidBucketItem.canDestroy(handler.getCursorStack())) {
			for (Slot s : handler.slots) {
				if (s.isEnabled() && s.getStack().isOf(YItems.VOID_BUCKET)) {
					RenderSystem.setShaderColor(0.4f, 0.1f, 0.1f, 1);
					ctx.drawTexture(Yttr.id("textures/gui/trash.png"), s.x+7, s.y+1, 300, 0, 0, 10, 10, 10, 10);
					RenderSystem.setShaderColor(1, 0.2f, 0.4f, 1);
					ctx.drawTexture(Yttr.id("textures/gui/trash.png"), s.x+6, s.y, 300, 0, 0, 10, 10, 10, 10);
					RenderSystem.setShaderColor(1, 1, 1, 1);
				}
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="render")
	public void yttr$renderEnd(GuiGraphics ctx, int mX, int mY, float tickDelta, CallbackInfo ci) {
		if (focusedSlot != null && focusedSlot.isEnabled() && focusedSlot.getStack().isOf(YItems.VOID_BUCKET) &&
				VoidBucketItem.canDestroy(handler.getCursorStack())) {
			String variety = "destroyable";
			if (handler.getCursorStack().isOf(Items.BUNDLE)) {
				variety = "bundle";
			}
			ctx.drawTooltip(textRenderer, List.of(
					Text.translatable("item.yttr.void_bucket"),
					Text.translatable("tip.yttr.void_bucket."+variety,
							handler.getCursorStack().getCount(),
							handler.getCursorStack().getName()).formatted(Formatting.GRAY),
					Text.translatable("tip.yttr.void_bucket."+variety+".flair").formatted(Formatting.DARK_RED)
				), mX, mY);
		}
	}
	
}
