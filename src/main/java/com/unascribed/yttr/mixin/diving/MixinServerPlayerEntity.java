package com.unascribed.yttr.mixin.diving;

import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixin.accessor.AccessorServerPlayNetHandler;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.network.MessageS2CDiveEnd;
import com.unascribed.yttr.util.math.Vec2i;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;

import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements DiverPlayer {

	private boolean yttr$isDiving = false;
	private boolean yttr$isInvisibleFromDiving = false;
	private boolean yttr$isNoGravityFromDiving = false;
	private int yttr$lastDivePosUpdate;
	private Vec2i yttr$divePos;
	private long yttr$fastDiveFinishNanos;
	private BlockPos yttr$fastDiveTarget;
	
	private final Set<UUID> yttr$knownGeysers = Sets.newHashSet();

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Inject(at=@At("HEAD"), method="tick")
	public void tick(CallbackInfo ci) {
		ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
		if (!self.isAlive()) return;
		if (yttr$isDiving) {
			if (self.networkHandler instanceof AccessorServerPlayNetHandler acc) {
				acc.yttr$setFloatingTicks(0);
			}
			YStats.add(self, YStats.TIME_IN_VOID, 1);
			if (self.getPos().y > getWorld().getBottomY()) {
				yttr$isDiving = false;
				new MessageS2CDiveEnd().sendTo(self);
			} else {
				if (!self.isInvisible()) {
					self.setInvisible(true);
					yttr$isInvisibleFromDiving = true;
				} else if (!self.hasNoGravity()) {
					self.setNoGravity(true);
					yttr$isNoGravityFromDiving = true;
				}
				self.setPos(self.getPos().x, getWorld().getBottomY() - 24, self.getPos().z);
			}
			ItemStack chest = self.getEquippedStack(EquipmentSlot.CHEST);
			if (Yttr.isWearingFullSuit(self)) {
				SuitArmorItem sai = (SuitArmorItem)chest.getItem();
				int pressure = Yttr.calculatePressure(self.getServerWorld(), yttr$divePos.x, yttr$divePos.z);
				for (SuitResource sr : SuitResource.VALUES) {
					int amt = sai.getResourceAmount(chest, sr);
					if (amt <= 0) {
						sr.applyDepletedEffect(self);
					} else if (yttr$fastDiveTarget == null) {
						sai.consumeResource(chest, sr, sr.getConsumptionPerTick(pressure));
					}
				}
			} else {
				SuitResource.INTEGRITY.applyDepletedEffect(self);
			}
			self.fallDistance = 0;
			if (yttr$fastDiveTarget != null) {
				BlockPos pos = yttr$fastDiveTarget;
				long diveTimeLeft = yttr$fastDiveFinishNanos-System.nanoTime();
				if (diveTimeLeft > 0) {
					if (pos.getSquaredDistanceToCenter(self.getPos()) > 5000*5000) {
						YCriteria.DIVE_FAR.trigger(self);
					}
					// teleport prematurely to load chunks
					self.teleport(pos.getX()+0.5, getWorld().getBottomY() - 24, pos.getZ()+0.5);
				} else {
					yttr$isDiving = false;
					new MessageS2CDiveEnd().sendTo(self);
					self.playSound(YSounds.DIVE_END, 2, 1);
					double closestDist = Double.POSITIVE_INFINITY;
					BlockPos closestPad = null;
					for (BlockPos bp : BlockPos.iterate(pos.add(-5, -5, -5), pos.add(5, 5, 5))) {
						if (self.getWorld().getBlockState(bp).isOf(YBlocks.DIVING_PLATE)) {
							double dist = bp.getSquaredDistance(pos);
							if (dist < closestDist && self.getWorld().isAir(bp.up())) {
								closestPad = bp.toImmutable();
								closestDist = dist;
							}
						}
					}
					if (closestPad == null) {
						self.teleport(pos.getX()+0.5, pos.getY()+4, pos.getZ()+0.5);
						self.setVelocity(self.getWorld().random.nextGaussian()/2, 1, self.getWorld().random.nextGaussian()/2);
						self.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(self));
					} else {
						self.teleport(closestPad.getX()+0.5, closestPad.getY()+1, closestPad.getZ()+0.5);
					}
				}
			} else {
				GeysersState gs = GeysersState.get(self.getServerWorld());
				for (Geyser g : gs.getGeysersInRange(yttr$divePos.x, yttr$divePos.z, 64)) {
					if (!yttr$knownGeysers.contains(g.id)) {
						Yttr.discoverGeyser(g.id, self, true);
						YCriteria.DISCOVER_GEYSER.trigger(self);
					}
				}
			}
		} else {
			if (yttr$isNoGravityFromDiving) {
				self.setNoGravity(false);
				yttr$isNoGravityFromDiving = false;
			}
			if (yttr$isInvisibleFromDiving) {
				self.setInvisible(false);
				yttr$isInvisibleFromDiving = false;
			}
			if (yttr$divePos != null) {
				yttr$divePos = null;
			}
			if (yttr$lastDivePosUpdate != 0) {
				yttr$lastDivePosUpdate = 0;
			}
			if (yttr$fastDiveTarget != null) {
				yttr$fastDiveTarget = null;
			}
			if (yttr$fastDiveFinishNanos != 0) {
				yttr$fastDiveFinishNanos = 0;
			}
		}
	}
	
	@Inject(at=@At("HEAD"), method="onSpawn()V")
	public void onSpawn(CallbackInfo ci) {
		if (yttr$isDiving) {
			ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
			Yttr.syncDive(self);
		}
	}
	
	@Inject(at=@At("TAIL"), method="writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		if (yttr$isDiving) nbt.putBoolean("yttr:Diving", yttr$isDiving);
		if (yttr$isInvisibleFromDiving) nbt.putBoolean("yttr:InvisibleFromDiving", yttr$isInvisibleFromDiving);
		if (yttr$isNoGravityFromDiving) nbt.putBoolean("yttr:NoGravityFromDiving", yttr$isNoGravityFromDiving);
		if (yttr$divePos != null) nbt.put("yttr:DivePos", yttr$divePos.toTag());
		if (yttr$fastDiveTarget != null) nbt.put("yttr:FastDiveTarget", NbtHelper.fromBlockPos(yttr$fastDiveTarget));
		if (yttr$fastDiveFinishNanos != 0) nbt.putLong("yttr:FastDiveNanosRemaining", yttr$fastDiveFinishNanos-System.nanoTime());
		
		if (!yttr$knownGeysers.isEmpty()) {
			NbtList li = new NbtList();
			for (UUID id : yttr$knownGeysers) {
				li.add(NbtHelper.fromUuid(id));
			}
			nbt.put("yttr:KnownGeysers", li);
		}
	}
	
	@Inject(at=@At("TAIL"), method="readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		yttr$isDiving = nbt.getBoolean("yttr:Diving");
		yttr$isInvisibleFromDiving = nbt.getBoolean("yttr:InvisibleFromDiving");
		yttr$isNoGravityFromDiving = nbt.getBoolean("yttr:NoGravityFromDiving");
		yttr$divePos = Vec2i.fromTag(nbt.get("yttr:DivePos"));
		yttr$fastDiveTarget = nbt.contains("yttr:FastDiveTarget", NbtType.COMPOUND) ? NbtHelper.toBlockPos(nbt.getCompound("yttr:FastDiveTarget")) : null;
		if (nbt.contains("yttr:FastDiveTime")) {
			yttr$setFastDiveTime(nbt.getInt("yttr:FastDiveTime"));
		} else if (nbt.contains("yttr:FastDiveNanosRemaining")) {
			yttr$fastDiveFinishNanos = System.nanoTime()+nbt.getLong("yttr:FastDiveNanosRemaining");
		} else {
			yttr$fastDiveFinishNanos = 0;
		}
		
		yttr$knownGeysers.clear();
		NbtList li = nbt.getList("yttr:KnownGeysers", NbtType.INT_ARRAY);
		for (int i = 0; i < li.size(); i++) {
			yttr$knownGeysers.add(NbtHelper.toUuid(li.get(i)));
		}
	}
	
	@Inject(at=@At("TAIL"), method="copyFrom")
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		if (oldPlayer instanceof DiverPlayer) {
			yttr$knownGeysers.addAll(((DiverPlayer)oldPlayer).yttr$getKnownGeysers());
		}
	}
	
	@Override
	public boolean yttr$isDiving() {
		return yttr$isDiving;
	}

	@Override
	public void yttr$setDiving(boolean b) {
		yttr$isDiving = b;
	}
	
	@Override
	public boolean yttr$isInvisibleFromDiving() {
		return yttr$isInvisibleFromDiving;
	}
	
	@Override
	public boolean yttr$isNoGravityFromDiving() {
		return yttr$isNoGravityFromDiving;
	}
	
	@Override
	public Set<UUID> yttr$getKnownGeysers() {
		return yttr$knownGeysers;
	}

	@Override
	public int yttr$getLastDivePosUpdate() {
		return yttr$lastDivePosUpdate;
	}

	@Override
	public void yttr$setLastDivePosUpdate(int i) {
		yttr$lastDivePosUpdate = i;
	}

	@Override
	public @Nullable Vec2i yttr$getDivePos() {
		return yttr$divePos;
	}

	@Override
	public void yttr$setDivePos(@Nullable Vec2i v) {
		yttr$divePos = v;
	}

	@Override
	public long yttr$getFastDiveFinishNanos() {
		return yttr$fastDiveFinishNanos;
	}
	
	@Override
	public void yttr$setFastDiveFinishNanos(long nanos) {
		this.yttr$fastDiveFinishNanos = nanos;
	}

	@Override
	public @Nullable BlockPos yttr$getFastDiveTarget() {
		return yttr$fastDiveTarget;
	}

	@Override
	public void yttr$setFastDiveTarget(@Nullable BlockPos g) {
		yttr$fastDiveTarget = g;
	}

}
