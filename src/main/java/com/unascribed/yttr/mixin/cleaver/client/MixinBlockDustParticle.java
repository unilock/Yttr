package com.unascribed.yttr.mixin.cleaver.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.content.block.decor.CleavedBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
@Mixin(BlockDustParticle.class)
public abstract class MixinBlockDustParticle extends SpriteBillboardParticle {

	protected MixinBlockDustParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f);
	}
	
	@Inject(at=@At("TAIL"), method="<init>(Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V")
	protected void construct(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos, CallbackInfo ci) {
		BlockEntity be = world.getBlockEntity(blockPos);
		if (be instanceof CleavedBlockEntity) {
			setSprite(MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModelParticleSprite(((CleavedBlockEntity) be).getDonor()));
		}
	}

}
