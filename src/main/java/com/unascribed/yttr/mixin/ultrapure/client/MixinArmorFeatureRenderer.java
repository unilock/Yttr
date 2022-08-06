package com.unascribed.yttr.mixin.ultrapure.client;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.client.YRenderLayers;
import com.unascribed.yttr.client.YttrClient;
import com.unascribed.yttr.init.YItems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

// Ported from 1.16 Fabric API
// They removed this in favor of just shitting your armor textures into the minecraft namespace
// But that won't work for me since I need to change armor models for vanilla items conditionally...

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer extends FeatureRenderer {

	public MixinArmorFeatureRenderer(FeatureRendererContext context) {
		super(context);
	}

	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	@Unique
	private LivingEntity storedEntity;
	@Unique
	private EquipmentSlot storedSlot;
	
	@Unique
	private ArmorItem armorItem;
	@Unique
	private boolean secondLayer;
	@Unique
	private String model;

	@Inject(method = "render", at = @At("HEAD"))
	private void storeEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.storedEntity = livingEntity;
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void storeSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity livingEntity, EquipmentSlot slot, int i, BipedEntityModel bipedEntityModel, CallbackInfo ci) {
		this.storedSlot = slot;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void removeStored(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.storedEntity = null;
		this.storedSlot = null;
	}
	
	@SuppressWarnings("rawtypes")
	@Inject(at=@At("HEAD"), method="renderArmorParts")
	public void captureArgs(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, BipedEntityModel bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string, CallbackInfo ci) {
		this.armorItem = armorItem;
		this.secondLayer = bl2;
		this.model = string;
	}
	
	@SuppressWarnings("rawtypes")
	@Inject(at=@At("RETURN"), method="renderArmorParts")
	public void forgetArgs(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, BipedEntityModel bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string, CallbackInfo ci) {
		this.armorItem = null;
		this.secondLayer = false;
		this.model = null;
	}
	
	@ModifyArg(at=@At(value="INVOKE", target="net/minecraft/client/render/item/ItemRenderer.getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
			method="renderArmorParts", index=1)
	public RenderLayer modifyRenderLayer(RenderLayer orig) {
		if (storedEntity == null || storedSlot == null) return orig;
		ItemStack stack = storedEntity.getEquippedStack(storedSlot);
		if (stack.getItem() == YItems.SUIT_HELMET ||
				((stack.hasNbt() && stack.getNbt().getInt("yttr:DurabilityBonus") > 0) && (
					stack.getItem() == Items.DIAMOND_HELMET ||
					stack.getItem() == Items.DIAMOND_CHESTPLATE ||
					stack.getItem() == Items.DIAMOND_LEGGINGS ||
					stack.getItem() == Items.DIAMOND_BOOTS
				))) {
			return YRenderLayers.getArmorTranslucentNoCull(getArmorTexture(armorItem, secondLayer, model));
		}
		return orig;
	}

	@Inject(method="getArmorTexture", at=@At(value="INVOKE", target="java/util/Map.computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void getArmorTexture(ArmorItem armorItem, boolean secondLayer, /* @Nullable */ String suffix, CallbackInfoReturnable<Identifier> cir, String vanillaIdentifier) {
		String texture = YttrClient.getArmorTexture(storedEntity, storedEntity.getEquippedStack(storedSlot), storedSlot, secondLayer, suffix, new Identifier(vanillaIdentifier)).toString();

		if (!Objects.equals(texture, vanillaIdentifier)) {
			cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(texture, Identifier::new));
		}
	}
	
	@Shadow
	private Identifier getArmorTexture(ArmorItem armorItem, boolean bl, @Nullable String string) { throw new AbstractMethodError(); }
	
}
