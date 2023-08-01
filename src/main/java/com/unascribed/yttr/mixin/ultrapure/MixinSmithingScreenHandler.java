package com.unascribed.yttr.mixin.ultrapure;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;

@Mixin(SmithingScreenHandler.class)
public abstract class MixinSmithingScreenHandler extends ForgingScreenHandler {

	public MixinSmithingScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	// TODO
//	@Inject(at=@At("TAIL"), method="updateResult")
//	public void updateResult(CallbackInfo ci) {
//		if (!output.isEmpty() && input.getStack(1).getItem() == YItems.ULTRAPURE_NETHERITE) {
//			ItemStack out = output.getStack(0);
//			if (!out.hasCustomName()) {
//				out.setCustomName(Text.translatable("item.yttr.ultrapure_tool.prefix", out.getName()).setStyle(Style.EMPTY.withItalic(false)));
//			}
//			if (!out.hasNbt()) {
//				out.setNbt(new NbtCompound());
//			}
//			out.getNbt().putInt("yttr:DurabilityBonus", out.getNbt().getInt("yttr:DurabilityBonus")+1);
//		}
//	}
	
}
