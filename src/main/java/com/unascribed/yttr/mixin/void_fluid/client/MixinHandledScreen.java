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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/gui/screen/ingame/HandledScreen.drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"),
			method="render")
	public void yttr$renderBeforeForeground(MatrixStack matrices, int mX, int mY, float tickDelta, CallbackInfo ci) {
		if (VoidBucketItem.canDestroy(handler.getCursorStack())) {
			for (Slot s : handler.slots) {
				if (s.isEnabled() && s.getStack().isOf(YItems.VOID_BUCKET)) {
					RenderSystem.setShaderTexture(0, Yttr.id("textures/gui/trash.png"));
					RenderSystem.setShaderColor(0.4f, 0.1f, 0.1f, 1);
					drawTexture(matrices, s.x+7, s.y+1, 0, 0, 0, 10, 10, 10, 10);
					RenderSystem.setShaderColor(1, 0.2f, 0.4f, 1);
					drawTexture(matrices, s.x+6, s.y, 0, 0, 0, 10, 10, 10, 10);
				}
			}
		}
	}
	
	@Inject(at=@At("TAIL"), method="render")
	public void yttr$renderEnd(MatrixStack matrices, int mX, int mY, float tickDelta, CallbackInfo ci) {
		if (focusedSlot != null && focusedSlot.isEnabled() && focusedSlot.getStack().isOf(YItems.VOID_BUCKET) &&
				VoidBucketItem.canDestroy(handler.getCursorStack())) {
			String variety = "destroyable";
			if (handler.getCursorStack().isOf(Items.BUNDLE)) {
				variety = "bundle";
			}
			renderTooltip(matrices, List.of(
					new TranslatableText("item.yttr.void_bucket"),
					new TranslatableText("tip.yttr.void_bucket."+variety,
							handler.getCursorStack().getCount(),
							handler.getCursorStack().getName()).formatted(Formatting.GRAY),
					new TranslatableText("tip.yttr.void_bucket."+variety+".flair").formatted(Formatting.DARK_RED)
				), mX, mY);
		}
	}
	
}
