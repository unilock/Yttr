package com.unascribed.yttr.content.entity;

import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YEntities;
import com.unascribed.yttr.init.YItems;

import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class ThrownGlowingGasEntity extends ThrownItemEntity {
	public ThrownGlowingGasEntity(EntityType<? extends ThrownGlowingGasEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownGlowingGasEntity(World world, LivingEntity owner) {
		super(YEntities.THROWN_GLOWING_GAS, owner, world);
	}

	public ThrownGlowingGasEntity(World world, double x, double y, double z) {
		super(YEntities.THROWN_GLOWING_GAS, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() {
		return YItems.GLOWING_GAS;
	}

	private ParticleEffect getParticleParameters() {
		return new ItemStackParticleEffect(ParticleTypes.ITEM, Items.GLASS_BOTTLE.getDefaultStack());
	}

	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			var eff = getParticleParameters();
			var r = ThreadLocalRandom.current();

			getWorld().playSound(getX(), getY(), getZ(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL,
					1, r.nextFloat(0.9f, 1), false);
			for (int i = 0; i < 8; ++i) {
				getWorld().addParticle(eff, getX(), getY(), getZ(), r.nextGaussian(0, 0.1), r.nextGaussian(0, 0.1), r.nextGaussian(0, 0.1));
			}
		}

	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!getWorld().isClient) {
			var pos = hitResult.getPos();
			getWorld().sendEntityStatus(this, (byte)3);
			
			if (hitResult instanceof EntityHitResult ehr) {
				if (ehr.getEntity() instanceof LivingEntity le && le.getGroup() == EntityGroup.UNDEAD) {
					le.damage(getWorld().getDamageSources().thrown(this, getOwner()), 6);
					le.setOnFireFor(4);
				}
			}
			
			AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(getWorld(), pos.x, pos.y, pos.z);
			cloud.setColor(0xFEAC6D);
			cloud.setRadius(0.3f);
			cloud.setDuration(40);
			cloud.addEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100));
			cloud.addEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200));
			getWorld().spawnEntity(cloud);
			
			for (var bp : BlockPos.iterateRandomly(random, 32, getBlockPos(), 4)) {
				var bs = getWorld().getBlockState(bp);
				if (bs.isAir() || (bs.isOf(Blocks.WATER) && bs.getFluidState().isSource())) {
					var cast = getWorld().raycast(new RaycastContext(pos, Vec3d.ofCenter(bp), ShapeType.COLLIDER, FluidHandling.NONE, this));
					if (cast == null || cast.getType() == Type.MISS) {
						getWorld().setBlockState(bp, (bs.isOf(Blocks.WATER) ? YBlocks.TEMPORARY_LIGHT_WATER : YBlocks.TEMPORARY_LIGHT_AIR).getDefaultState());
					}
				}
			}
			discard();
		}

	}
}
