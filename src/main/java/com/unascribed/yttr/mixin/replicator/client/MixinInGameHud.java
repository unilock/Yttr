package com.unascribed.yttr.mixin.replicator.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Shadow
	private ItemStack currentStack;
	
	@ModifyVariable(at=@At(value="INVOKE_ASSIGN", target="net/minecraft/text/MutableText.formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;"),
			method="renderHeldItemTooltip", ordinal=0)
	public MutableText modifyTooltip(MutableText orig) {
		if (currentStack != null && currentStack.getItem() == YItems.REPLICATOR) {
			ItemStack held = ReplicatorBlockItem.getHeldItem(currentStack);
			if (!held.isEmpty()) {
				return Text.translatable("block.yttr.replicator.holding",
						held.getName().copyContentOnly().formatted(held.getRarity().formatting),
						currentStack.getName().copyContentOnly().formatted(currentStack.getRarity().formatting));
			}
		}
		return orig;
	}
	
}
