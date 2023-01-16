package com.unascribed.yttr.mixin.neodymium.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.mixinsupport.Magnetized;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;

@Mixin(ItemEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class MixinItemEntityRenderer {
	
	@Shadow @Final
	private ItemRenderer itemRenderer;
	
	@ModifyVariable(at=@At(value="INVOKE", target="net/minecraft/client/render/model/BakedModel.getTransformation()Lnet/minecraft/client/render/model/json/ModelTransformation;"),
			method="render", ordinal=3)
	public float modifyBob(float f, ItemEntity itemEntity, float a, float b, MatrixStack matrixStack) {
		if (itemEntity instanceof Magnetized m) {
			if (m.yttr$isMagnetizedBelow()) {
				if (m.yttr$isMagnetizedAbove()) {
					return 0f;
				}
				return -0.1f;
			} else if (m.yttr$isMagnetizedAbove()) {
				return -0.15f;
			}
		}
		return f;
	}
	
	
}
