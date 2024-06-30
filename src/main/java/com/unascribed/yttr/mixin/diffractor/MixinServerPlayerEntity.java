package com.unascribed.yttr.mixin.diffractor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixinsupport.DiffractorListener;
import com.unascribed.yttr.mixinsupport.DiffractorPlayer;
import com.unascribed.yttr.network.MessageS2CSetCloaked;

import com.google.common.primitives.Shorts;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityPositionUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements DiffractorListener {
	
	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}
	
	@Inject(at=@At("HEAD"), method="tick")
	public void tick(CallbackInfo ci) {
		ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
		if (!self.isAlive()) return;
		var dp = (DiffractorPlayer)this;
		if (dp.yttr$getCloakTime() == 40) {
			EntityPositionUpdateS2CPacket pkt;
			if (dp.yttr$isCloaked()) {
				var buf = PacketByteBufs.create();
				buf.writeVarInt(getId());
				buf.writeDouble(0);
				buf.writeDouble(getWorld().getBottomY()-2000);
				buf.writeDouble(0);
				buf.writeByte(0);
				buf.writeByte(0);
				buf.writeBoolean(false);
				pkt = new EntityPositionUpdateS2CPacket(buf);
			} else {
				pkt = new EntityPositionUpdateS2CPacket(this);
			}
			self.getServerWorld().getChunkManager().sendToOtherNearbyPlayers(this, pkt);
		}
	}
	
	@Inject(at=@At("HEAD"), method="onSpawn()V")
	public void onSpawn(CallbackInfo ci) {
		var dp = (DiffractorPlayer)this;
		if (dp.yttr$isCloaked()) {
			new MessageS2CSetCloaked(getId(), dp.yttr$getCloakTime(), dp.yttr$isCloaked()).sendTo(this);
		}
	}

	@Inject(at=@At("TAIL"), method="writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		var dp = (DiffractorPlayer)this;
		if (dp.yttr$isCloaked()) nbt.putBoolean("yttr:Cloaked", true);
		if (dp.yttr$getCloakTime() != 0) nbt.putShort("yttr:CloakTime", Shorts.saturatedCast(dp.yttr$getCloakTime()));
	}
	
	@Inject(at=@At("TAIL"), method="readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		var dp = (DiffractorPlayer)this;
		dp.yttr$setCloaked(nbt.getBoolean("yttr:Cloaked"));
		dp.yttr$setCloakTime(nbt.getInt("yttr:CloakTime"));
	}
	
	@Inject(at=@At("TAIL"), method="copyFrom")
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (alive) {
			var us = (DiffractorPlayer)this;
			var them = (DiffractorPlayer)oldPlayer;
			us.yttr$setCloaked(them.yttr$isCloaked());
			us.yttr$setCloakTime(them.yttr$getCloakTime());
		}
	}

	@Override
	public void yttr$onCloak() {
		var dp = (DiffractorPlayer)this;
		// playSoundFromEntity will result in the sound getting cut off due to the fake teleport
		// it's mostly not noticeable due to the length of the sound
		getWorld().playSound(this, getX(), getY(), getZ(), YSounds.CLOAK, getSoundCategory(), 1.2f, 1);
		new MessageS2CSetCloaked(getId(), dp.yttr$getCloakTime(), true).sendToAllWatching(this);
	}

	@Override
	public void yttr$onUncloak() {
		var dp = (DiffractorPlayer)this;
		new MessageS2CSetCloaked(getId(), dp.yttr$getCloakTime(), false).sendToAllWatching(this);
		getWorld().playSoundFromEntity(this, this, YSounds.UNCLOAK, getSoundCategory(), 1.2f, 1);
		float m = dp.yttr$getCloakTime()/DiffractorPlayer.MAX_TIMEf;
		m = 0.2f+((m*m)*0.8f);
		getItemCooldownManager().set(YItems.DIFFRACTOR.get(), (int)(DiffractorPlayer.COOLDOWN_TIME*m));
	}

}
