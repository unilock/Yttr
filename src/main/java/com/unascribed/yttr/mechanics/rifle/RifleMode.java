package com.unascribed.yttr.mechanics.rifle;

import java.util.Arrays;
import java.util.function.Supplier;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.YConfig.TrileanSoft;
import com.unascribed.yttr.content.entity.RifleDummyEntity;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YDamageTypes;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mechanics.VoidLogic;
import com.unascribed.yttr.util.AdventureHelper;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public enum RifleMode {
	DAMAGE(Formatting.RED, 0xFF0000, () -> Items.REDSTONE, 12, 2) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			boolean canUse = AdventureHelper.canUse(user, "Beam", stack, user.getWorld(), hit.getPos());
			if (hit instanceof EntityHitResult) {
				int damage = canUse ? (int)Math.ceil(power*14) : 0;
				((EntityHitResult) hit).getEntity().damage(user.getDamageSources().create(YDamageTypes.RIFLE, new RifleDummyEntity(user.getWorld()), user), damage);
			}
			if (power > 1.2f && canUse) {
				user.getWorld().createExplosion(null, user.getDamageSources().explosion(user, user), null, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, false, World.ExplosionSourceType.NONE);
			}
		}
	},
	EXPLODE(Formatting.GRAY, 0xAAAAAA, () -> Items.GUNPOWDER, 1, 1) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (!AdventureHelper.canUse(user, "Explode", stack, user.getWorld(), hit.getPos())) return;
			user.getWorld().createExplosion(null, user.getDamageSources().explosion(user, user),
					null, hit.getPos().x, hit.getPos().y, hit.getPos().z, power > 1.2 ? 5 : 3*power, power > 1.2,
							YConfig.Rifle.allowExplode == TrileanSoft.SOFT ? World.ExplosionSourceType.NONE : power > 1.2 ? World.ExplosionSourceType.TNT : World.ExplosionSourceType.BLOCK);
		}
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			if (!AdventureHelper.canUse(user, "Explode", stack, user.getWorld(), user.getPos())) return;
			user.getWorld().createExplosion(null, user.getDamageSources().explosion(user, user),
					null, user.getPos().x, user.getPos().y, user.getPos().z, 5.5f, false,
					YConfig.Rifle.allowExplode == TrileanSoft.SOFT ? World.ExplosionSourceType.NONE : World.ExplosionSourceType.BLOCK);
		}
		@Override
		public boolean isEnabled() {
			return YConfig.Rifle.allowExplode != TrileanSoft.OFF;
		}
	},
	TELEPORT(Formatting.LIGHT_PURPLE, 0xFF00FF, () -> Items.CHORUS_FRUIT, 3, 1.5f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			boolean canUse = AdventureHelper.canUse(user, "Teleport", stack, user.getWorld(), hit.getPos());
			if (hit.getType() == Type.MISS) return;
			if (power > 1.1f && canUse) {
				user.getWorld().createExplosion(user, user.getPos().x, user.getPos().y, user.getPos().z, 1*power, World.ExplosionSourceType.NONE);
			}
			user.teleport(hit.getPos().x, hit.getPos().y, hit.getPos().z);
			if (power > 1.2f && canUse) {
				user.damage(user.getDamageSources().explosion(user, user), 4);
				user.getWorld().createExplosion(user, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, World.ExplosionSourceType.NONE);
			}
		}
		
		@Override
		public boolean canFire(LivingEntity user, ItemStack stack, float power) {
			return power >= 0.8f;
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			for (int i = 0; i < 4; i++) {
				Items.CHORUS_FRUIT.finishUsing(new ItemStack(Items.CHORUS_FRUIT), user.getWorld(), user);
			}
		}
	},
	FIRE(Formatting.GOLD, 0xFFAA00, () -> Items.BLAZE_POWDER, 2, 2) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (!AdventureHelper.canUse(user, "Fire", stack, user.getWorld(), hit.getPos())) return;
			if (hit instanceof EntityHitResult) {
				Entity e = ((EntityHitResult) hit).getEntity();
				e.setFireTicks((int)(200*power));
				int damage = (int)Math.ceil(power*6);
				e.damage(e.getDamageSources().create(YDamageTypes.RIFLE, new RifleDummyEntity(user.getWorld()), user), damage);
			} else if (power > 0.5f && hit instanceof BlockHitResult) {
				BlockHitResult bhr = (BlockHitResult)hit;
				if (bhr.getType() == Type.MISS) return;
				if (user.getWorld().isAir(bhr.getBlockPos()) || user.getWorld().getBlockState(bhr.getBlockPos()).isIn(YTags.Block.FIRE_MODE_INSTABREAK)) {
					user.getWorld().setBlockState(bhr.getBlockPos(), Blocks.FIRE.getDefaultState());
				} else {
					BlockPos bp2 = bhr.getBlockPos().offset(bhr.getSide());
					if (user.getWorld().isAir(bp2)) {
						user.getWorld().setBlockState(bp2, Blocks.FIRE.getDefaultState());
					}
				}
			}
			if (power > 1) {
				user.getWorld().createExplosion(null, user.getDamageSources().explosion(user, user), null, hit.getPos().x, hit.getPos().y, hit.getPos().z, 2*power, true, World.ExplosionSourceType.NONE);
				BlockPos base = BlockPos.fromPosition(hit.getPos());
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						for (int z = -1; z <= 1; z++) {
							BlockPos bp = base.add(x, y, z);
							if (user.getWorld().getBlockState(bp).isIn(YTags.Block.FIRE_MODE_INSTABREAK)) {
								user.getWorld().setBlockState(bp, Blocks.FIRE.getDefaultState());
							}
						}
					}
				}
			}
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			user.setOnFireFor(20);
		}
		
		@Override
		public boolean isEnabled() {
			return YConfig.Rifle.allowFire;
		}
	},
	VOID(Formatting.BLACK, 0x000000, () -> YItems.VOID_BUCKET, 1, 0.75f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (!AdventureHelper.canUse(user, "Void", stack, user.getWorld(), hit.getPos())) return;
			if (!(user instanceof PlayerEntity)) return;
			VoidLogic.doVoid((PlayerEntity)user, user.getWorld(), hit.getPos(), Math.round(7.5f*power)+1);
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			if (!AdventureHelper.canUse(user, "Void", stack, user.getWorld(), user.getPos())) {
				user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), YSounds.VOID, SoundCategory.PLAYERS, 4, 1);
				user.damage(user.getDamageSources().create(YDamageTypes.VOID_RIFLE, user), 144);
				return;
			}
			if (!(user instanceof PlayerEntity)) return;
			VoidLogic.doVoid((PlayerEntity)user, user.getWorld(), user.getPos(), 12);
		}
		
		@Override
		public boolean isEnabled() {
			return YConfig.Rifle.allowVoid;
		}
		
	},
	LIGHT(Formatting.YELLOW, 0xFFFF00, () -> Items.GLOWSTONE_DUST, 8, 2f) {
		@Override
		public void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit) {
			if (!AdventureHelper.canUse(user, "Light", stack, user.getWorld(), hit.getPos())) return;
			Vec3d start = RifleItem.getMuzzlePos(user, false);
			double len = Math.sqrt(start.squaredDistanceTo(hit.getPos()));
			double diffX = hit.getPos().x-start.x;
			double diffY = hit.getPos().y-start.y;
			double diffZ = hit.getPos().z-start.z;
			BlockPos.Mutable mut = new BlockPos.Mutable();
			int count = (int)(len*4);
			for (int i = 0; i < count; i++) {
				double t = (i/(double)count);
				double x = start.x+(diffX*t);
				double y = start.y+(diffY*t);
				double z = start.z+(diffZ*t);
				mut.set(x, y, z);
				illuminate(user.getWorld(), mut, power > 1.1f);
			}
			if (hit instanceof EntityHitResult) {
				Entity e = ((EntityHitResult)hit).getEntity();
				if (e instanceof LivingEntity) {
					((LivingEntity) e).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, (int)(200*power)));
				}
			} else if (hit instanceof BlockHitResult && power > 0.8f) {
				BlockHitResult bhr = (BlockHitResult)hit;
				BlockPos end = bhr.getBlockPos().offset(bhr.getSide());
				illuminate(user.getWorld(), end, true);
			}
		}
		
		@Override
		public void handleBackfire(LivingEntity user, ItemStack stack) {
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300));
			if (!AdventureHelper.canUse(user, "Light", stack, user.getWorld(), user.getPos())) return;
			for (BlockPos bp : BlockPos.iterate(user.getBlockPos().add(-2, -2, -2), user.getBlockPos().add(2, 2, 2))) {
				illuminate(user.getWorld(), bp, false);
			}
		}

		private void illuminate(World world, BlockPos bp, boolean permanent) {
			var bs = world.getBlockState(bp);
			if (bs.isAir()) {
				world.setBlockState(bp, (permanent ? YBlocks.PERMANENT_LIGHT_AIR : YBlocks.TEMPORARY_LIGHT_AIR).getDefaultState());
			} else if (bs.isOf(Blocks.WATER) || bs.isOf(YBlocks.TEMPORARY_LIGHT_WATER)) {
				var nbs = (permanent ? YBlocks.PERMANENT_LIGHT_WATER : YBlocks.TEMPORARY_LIGHT_WATER).getDefaultState();
				nbs = nbs.with(FluidBlock.LEVEL, bs.get(FluidBlock.LEVEL));
				world.setBlockState(bp, nbs);
			}
		}
	}
	;
	public static final ImmutableList<RifleMode> VALUES = Arrays.stream(values()).filter(RifleMode::isEnabled).collect(ImmutableList.toImmutableList());
	public static final ImmutableList<RifleMode> ALL_VALUES = ImmutableList.copyOf(values());
	
	public final Formatting chatColor;
	public final int color;
	public final Supplier<ItemConvertible> item;
	public final int shotsPerItem;
	public final float speed;
	
	private int effectiveOrdinal = -1;
	
	RifleMode(Formatting chatColor, int color, Supplier<ItemConvertible> item, int shotsPerItem, float speed) {
		this.chatColor = chatColor;
		this.color = color;
		this.item = item;
		this.shotsPerItem = shotsPerItem;
		this.speed = speed;
	}
	
	public boolean canFire(LivingEntity user, ItemStack stack, float power) {
		return power > 0;
	}
	
	public int effectiveOrdinal() {
		return effectiveOrdinal;
	}
	
	public abstract void handleFire(LivingEntity user, ItemStack stack, float power, HitResult hit);
	public void handleBackfire(LivingEntity user, ItemStack stack) {}
	
	public boolean isEnabled() {
		return true;
	}
	
	public RifleMode next() {
		if (!VALUES.contains(this)) return VALUES.get(0);
		return VALUES.get((VALUES.indexOf(this)+1)%VALUES.size());
	}
	
	public RifleMode prev() {
		if (!VALUES.contains(this)) return VALUES.get(0);
		int idx = VALUES.indexOf(this);
		if (idx == 0) return VALUES.get(VALUES.size()-1);
		return VALUES.get(idx-1);
	}
	
	static {
		for (int i = 0; i < VALUES.size(); i++) {
			VALUES.get(i).effectiveOrdinal = i;
		}
	}
	
}
