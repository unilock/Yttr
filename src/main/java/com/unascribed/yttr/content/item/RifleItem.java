package com.unascribed.yttr.content.item;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import com.unascribed.yttr.init.*;
import org.jetbrains.annotations.Nullable;

import com.unascribed.lib39.recoil.api.DirectClickItem;
import com.unascribed.lib39.util.api.SlotReference;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.ReplicatorBlockItem;
import com.unascribed.yttr.mechanics.rifle.RifleMode;
import com.unascribed.yttr.mechanics.rifle.Shootable;
import com.unascribed.yttr.mixin.accessor.AccessorEntity;
import com.unascribed.yttr.network.MessageC2STrustedRifleFire;
import com.unascribed.yttr.network.MessageS2CBeam;
import com.unascribed.yttr.util.ControlHintable;
import com.unascribed.yttr.util.InventoryProviderItem;

import com.google.common.base.Enums;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicates;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import org.joml.Vector3f;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class RifleItem extends Item implements ItemColorProvider, DirectClickItem, ControlHintable {

	private final float speedMod;
	private final int ammoMod;
	private final boolean simpleCurve;
	private final int baseColor;
	
	public RifleItem(Settings settings, float speedMod, int ammoMod, boolean simpleCurve, int baseColor) {
		super(settings);
		this.speedMod = speedMod;
		this.ammoMod = ammoMod;
		this.simpleCurve = simpleCurve;
		this.baseColor = baseColor;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (hand == Hand.MAIN_HAND) {
			RifleMode mode = getMode(stack);
			if (!mode.isEnabled()) return TypedActionResult.fail(stack);
			SlotReference can = getAmmoCanSlot(user, mode);
			if (can == null) {
				if (stack.hasNbt()) {
					stack.getNbt().remove("FiringFromCan");
					stack.getNbt().remove("LastCanFullness");
				}
				int ammo = getRemainingAmmo(stack);
				int need = mode != RifleMode.VOID && mode.shotsPerItem < ammoMod ? ammoMod : 1;
				boolean canUseLooseReagents = !stack.hasNbt() || !stack.getNbt().getBoolean("CanistersOnly");
				if (ammo <= 0) {
					if (user.getAbilities().creativeMode) {
						ammo = mode.shotsPerItem;
					} else if (canUseLooseReagents) {
						for (int i = 0; i < user.getInventory().size(); i++) {
							boolean replicator = false;
							ItemStack is = user.getInventory().getStack(i);
							if (is.getItem() == YItems.REPLICATOR) {
								is = ReplicatorBlockItem.getHeldItem(is);
								is.setCount(64);
								replicator = true;
							}
							if (is.getItem() == mode.item.get().asItem() && is.getCount() >= need) {
								Item remainder = is.getItem().getRecipeRemainder();
								if (!replicator && remainder != null) {
									if (is.getCount() == need) {
										user.getInventory().setStack(i, new ItemStack(remainder));
									} else {
										is.decrement(need);
										user.getInventory().offerOrDrop(new ItemStack(remainder));
									}
								} else {
									is.decrement(need);
								}
								if (mode == RifleMode.VOID) {
									ammo = Math.max(1, mode.shotsPerItem/ammoMod);
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, SoundEvents.ITEM_BUCKET_EMPTY, user.getSoundCategory(), 1, 1);
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_LOAD, user.getSoundCategory(), 0.1f, 1f);
								} else if (mode == RifleMode.LIGHT) {
									ammo = (mode.shotsPerItem*need)/ammoMod;
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, user.getSoundCategory(), 1, 1);
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_LOAD, user.getSoundCategory(), 0.1f, 1f);
								} else {
									ammo = (mode.shotsPerItem*need)/ammoMod;
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_LOAD, user.getSoundCategory(), 3, 2f);
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_LOAD, user.getSoundCategory(), 3, 1.75f);
									user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_LOAD, user.getSoundCategory(), 3, 1.5f);
								}
								break;
							}
						}
					}
				}
				if (ammo <= 0) {
					user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_FIRE_DUD, user.getSoundCategory(), 1, 1.25f);
					if (canUseLooseReagents) {
						if (need > 1) {
							user.sendMessage(Text.translatable("tip.yttr.rifle_no_ammo_multi", need, new ItemStack(mode.item.get()).getName()), true);
						} else {
							user.sendMessage(Text.translatable("tip.yttr.rifle_no_ammo", new ItemStack(mode.item.get()).getName()), true);
						}
					} else {
						user.sendMessage(Text.translatable("tip.yttr.rifle_no_ammo", "ammo"), true);
					}
					return TypedActionResult.fail(stack);
				}
				setRemainingAmmo(stack, ammo);
			} else {
				if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
				stack.getNbt().putBoolean("FiringFromCan", true);
				stack.getNbt().putFloat("LastCanFullness", can.getStack().getNbt().getInt("Shots")/(float)AmmoCanItem.CAPACITY);
			}
			user.playSound(YSounds.RIFLE_FIRE_DUD, 1, 1.75f);
			float speed = mode.speed*speedMod;
			if (speed > 2) {
				speed /= 1.75f;
				world.playSoundFromEntity(YConfig.General.trustPlayers ? user : null, user, YSounds.RIFLE_CHARGE_FAST, user.getSoundCategory(), 1, speed);
			} else {
				world.playSoundFromEntity(YConfig.General.trustPlayers ? user : null, user, YSounds.RIFLE_CHARGE, user.getSoundCategory(), 1, speed);
			}
			user.setCurrentHand(hand);
			return TypedActionResult.success(stack, false);
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult onDirectAttack(PlayerEntity user, Hand hand) {
		if (user.getWorld().isClient) return ActionResult.CONSUME;
		ItemStack held = user.getStackInHand(Hand.MAIN_HAND);
		if (!held.hasNbt()) held.setNbt(new NbtCompound());
		boolean scoped = held.getNbt().getBoolean("Scoped");
		held.getNbt().putBoolean("Scoped", !scoped);
		if (!scoped && user instanceof ServerPlayerEntity) {
			YCriteria.RIFLE_SCOPE.trigger((ServerPlayerEntity)user);
		}
		user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_SCOPE, SoundCategory.PLAYERS, 1, scoped ? 0.8f : 1.2f);
		return ActionResult.SUCCESS;
	}
	
	@Override
	public ActionResult onDirectUse(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}
	
	public int getPotentialAmmoCount(PlayerEntity user, RifleMode mode) {
		if (user.isCreative()) return -1;
		SlotReference can = getAmmoCanSlot(user, mode);
		if (can != null) {
			return can.getStack().getNbt().getInt("Shots");
		}
		int need = mode != RifleMode.VOID && mode.shotsPerItem < ammoMod ? ammoMod : 1;
		int ammo = 0;
		for (int i = 0; i < user.getInventory().size(); i++) {
			boolean replicator = false;
			ItemStack is = user.getInventory().getStack(i);
			if (is.getItem() == YItems.REPLICATOR) {
				is = ReplicatorBlockItem.getHeldItem(is);
				is.setCount(64);
				replicator = true;
			}
			if (is.getItem() == mode.item.get().asItem()) {
				if (replicator) return -1;
				if (mode == RifleMode.VOID) {
					ammo += Math.max(1, mode.shotsPerItem/ammoMod);
				} else if (mode == RifleMode.LIGHT) {
					ammo += (mode.shotsPerItem*(is.getCount()/need))/ammoMod;
				} else {
					ammo += (mode.shotsPerItem*(is.getCount()/need))/ammoMod;
				}
			}
		}
		ItemStack held = user.getStackInHand(Hand.MAIN_HAND);
		if (held.getItem() instanceof RifleItem) {
			RifleItem ri = (RifleItem)held.getItem();
			if (ri.getMode(held) == mode) {
				ammo += ri.getRemainingAmmo(held);
			}
		}
		return ammo;
	}
	
	public boolean isAmmoCanned(PlayerEntity user, RifleMode mode) {
		return getAmmoCanSlot(user, mode) != null;
	}
	
	public @Nullable SlotReference getAmmoCanSlot(PlayerEntity user, RifleMode mode) {
		var backpack = Yttr.trinketsAccess.getWorn(user, YItems.AMMO_PACK::is);
		if (backpack.isPresent()) {
			var is = backpack.get().getStack();
			Inventory inv = ((InventoryProviderItem)is.getItem()).asInventory(is);
			SlotReference ref = Yttr.scanInventory(inv, isMatchingCan(mode));
			if (ref != null) return ref;
		}
		return Yttr.scanInventory(user.getInventory(), isMatchingCan(mode));
	}
	
	private Predicate<ItemStack> isMatchingCan(RifleMode mode) {
		return is -> isMatchingCan(is, mode);
	}
	
	private boolean isMatchingCan(ItemStack is, RifleMode mode) {
		return is.getItem() == YItems.AMMO_CAN && is.hasNbt() && mode.name().equals(is.getNbt().getString("Mode")) && is.getNbt().getInt("Shots") > 0;
	}

	public void changeMode(PlayerEntity user, RifleMode mode) {
		if (!mode.isEnabled()) return;
		if (user.isUsingItem()) return;
		ItemStack stack = user.getMainHandStack();
		if (stack.hasNbt() && stack.getNbt().getBoolean("ModeLocked")) return;
		RifleMode oldMode = getMode(stack);
		if (setMode(stack, mode)) {
			user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_WASTE, user.getSoundCategory(), 3, 0.75f);
			user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_WASTE, user.getSoundCategory(), 3, 1f);
			user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_WASTE, user.getSoundCategory(), 3, 1.5f);
			if (user.getWorld() instanceof ServerWorld) {
				float r = (oldMode.color >> 16 & 255)/255f;
				float g = (oldMode.color >> 8 & 255)/255f;
				float b = (oldMode.color >> 0 & 255)/255f;
				((ServerWorld)user.getWorld()).spawnParticles(new DustParticleEffect(new Vector3f(r, g, b), 1), user.getPos().x, user.getPos().y+0.1, user.getPos().z, 12, 0.2, 0.1, 0.2, 1);
			}
		}
		SlotReference can = getAmmoCanSlot(user, mode);
		if (can == null) {
			if (stack.hasNbt()) {
				stack.getNbt().remove("FiringFromCan");
				stack.getNbt().remove("LastCanFullness");
			}
		} else {
			if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
			stack.getNbt().putBoolean("FiringFromCan", true);
			stack.getNbt().putFloat("LastCanFullness", can.getStack().getNbt().getInt("Shots")/(float)AmmoCanItem.CAPACITY);
		}
		user.setStackInHand(Hand.MAIN_HAND, stack);
		user.getWorld().playSound(null, user.getPos().x, user.getPos().y, user.getPos().z, YSounds.RIFLE_FIRE_DUD, user.getSoundCategory(), 1, 1.3f+(mode.ordinal()*0.1f));
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		float max = getTrueMaxUseTime(stack);
		if (YConfig.General.trustPlayers) {
			max *= 1.25f;
		}
		return (int)max;
	}
	
	public int getTrueMaxUseTime(ItemStack stack) {
		return (int)(140/(getMode(stack).speed*speedMod));
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity _user, int remainingUseTicks) {
		if (!(_user instanceof PlayerEntity)) return;
		PlayerEntity user = (PlayerEntity)_user;
		if (YConfig.General.trustPlayers) {
			if (world.isClient) {
				world.playSoundFromEntity(user, user, YSounds.RIFLE_CHARGE_CANCEL, user.getSoundCategory(), 1, 1);

				int useTicks = calcAdjustedUseTime(stack, remainingUseTicks);
				float power = calculatePower(useTicks);
				RifleMode mode = getMode(stack);
				if (!mode.canFire(user, stack, power)) {
					user.playSound(YSounds.RIFLE_FIRE_DUD, 1, 1);
				} else {
					playFireSoundAndSetCooldown(user, calculatePower(calcAdjustedUseTime(stack, remainingUseTicks)));
				}
				new MessageC2STrustedRifleFire(remainingUseTicks).sendToServer();
			}
		} else {
			doOnStoppedUsing(stack, world, user, remainingUseTicks);
		}
	}
		
	public void doOnStoppedUsing(ItemStack stack, World world, PlayerEntity user, int remainingUseTicks) {
		world.playSoundFromEntity(YConfig.General.trustPlayers ? user : null, user, YSounds.RIFLE_CHARGE_CANCEL, user.getSoundCategory(), 1, 1);
		int useTicks = calcAdjustedUseTime(stack, remainingUseTicks);
		float power = calculatePower(useTicks);
		RifleMode mode = getMode(stack);
		SlotReference can = getAmmoCanSlot(user, mode);
		if (can == null) {
			int ammo = getRemainingAmmo(stack);
			if (useTicks > 30) {
				ammo--;
				setRemainingAmmo(stack, ammo);
			}
			if (stack.hasNbt()) {
				stack.getNbt().remove("FiringFromCan");
				stack.getNbt().remove("LastCanFullness");
			}
		} else {
			if (useTicks > 30) {
				int shots = can.getStack().getNbt().getInt("Shots");
				shots--;
				if (shots <= 0) {
					can.setStack(new ItemStack(YItems.EMPTY_AMMO_CAN));
				} else {
					can.getStack().getNbt().putInt("Shots", shots);
				}
				if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
				stack.getNbt().putBoolean("FiringFromCan", true);
				stack.getNbt().putFloat("LastCanFullness", shots/(float)AmmoCanItem.CAPACITY);
			}
		}
		if (!mode.canFire(user, stack, power)) {
			user.playSound(YSounds.RIFLE_FIRE_DUD, 1, 1);
		} else {
			playFireSoundAndSetCooldown(user, power);
			if (world instanceof ServerWorld) {
				HitResult hr = raycast(world, user);
				int color = mode.color;
				if (power > 1.2) {
					color = 0xFFFFFFFF;
				} else {
					color |= (int)Math.min(255, power*255)<<24;
				}
				new MessageS2CBeam(user.getId(), color, (float)hr.getPos().x, (float)hr.getPos().y, (float)hr.getPos().z).sendToAllWatching(user);
				if (hr instanceof BlockHitResult bhr) {
					BlockState bs = world.getBlockState(bhr.getBlockPos());
					if (bs.getBlock() instanceof Shootable) {
						if (((Shootable)bs.getBlock()).onShotByRifle(world, bs, user, mode, power, bhr.getBlockPos(), bhr)) {
							return;
						}
					}
				} else if (hr instanceof EntityHitResult ehr && user instanceof ServerPlayerEntity &&
						stack.hasNbt() && stack.getNbt().getBoolean("Scoped") && ehr.getEntity().squaredDistanceTo(user) > 100*100) {
					YCriteria.SHOOT_SOMETHING_FAR_AWAY.trigger((ServerPlayerEntity)user);
				}
				YStats.add(user, YStats.RIFLE_SHOTS_FIRED, 1);
				if (power > 1.1) {
					YStats.add(user, YStats.RIFLE_SHOTS_OVERCHARGED, 1);
				}
				mode.handleFire(user, stack, power, hr);
			}
		}
	}

	public static HitResult raycast(World world, PlayerEntity user) {
		Vec3d preStart = user.getCameraPosVec(0);
		Vec3d preEnd = user.getCameraPosVec(0).add(user.getRotationVec(0).multiply(64));
		Vec3d start = getMuzzlePos(user, false);
		Vec3d max = user.getCameraPosVec(0).add(user.getRotationVec(0).multiply(256));
		Vec3d end = max;
		BlockHitResult preBlock = world.raycast(new RaycastContext(preStart, preEnd, ShapeType.COLLIDER, FluidHandling.NONE, user));
		EntityHitResult preEnt = correctEntityHit(ProjectileUtil.getEntityCollision(user.getWorld(), user, preStart, preBlock.getPos(), new Box(preStart, preEnd).expand(0.3), Predicates.alwaysTrue()), preStart, preEnd);
		HitResult pre = MoreObjects.firstNonNull(preEnt, preBlock);
		if (pre.getType() != Type.MISS && start.squaredDistanceTo(pre.getPos()) > 3*3) {
			// add a bit of extension to ensure the block/entity gets hit instead of just barely not being reached
			end = pre.getPos().add(preBlock.getPos().subtract(start).normalize());
		}
		BlockHitResult bhr = world.raycast(new RaycastContext(start, end, ShapeType.COLLIDER, FluidHandling.NONE, user));
		EntityHitResult ehr = correctEntityHit(ProjectileUtil.getEntityCollision(user.getWorld(), user, start, bhr.getPos(), new Box(start, end).expand(0.3), Predicates.alwaysTrue()), start, end);
		return MoreObjects.firstNonNull(ehr, bhr);
	}
	
	protected void playFireSoundAndSetCooldown(PlayerEntity user, float power) {
		if (power > 1.1) {
			if (power > 1.2) {
				if (power >= 1.29) {
					user.playSound(YSounds.RIFLE_FIRE, 2, 0.5f);
					user.playSound(YSounds.RIFLE_FIRE, 2, 0.5f);
					user.playSound(YSounds.RIFLE_FIRE, 2, 2f);
					user.playSound(YSounds.RIFLE_FIRE, 2, 1f);
				}
				user.playSound(YSounds.RIFLE_FIRE, 2, 0.75f);
				user.playSound(YSounds.RIFLE_FIRE, 2, 0.65f);
			}
			user.playSound(YSounds.RIFLE_FIRE, 2, 0.5f);
			user.playSound(YSounds.RIFLE_FIRE, 1, 1);
			user.playSound(YSounds.RIFLE_FIRE, 1, 2);
			user.playSound(YSounds.RIFLE_FIRE, 1, 1.25f);
			user.getItemCooldownManager().set(this, 30);
		} else {
			user.playSound(YSounds.RIFLE_FIRE, 1, 0.9f+(power/4));
			if (power > 1) {
				user.playSound(YSounds.RIFLE_FIRE, 1, 0.75f);
			}
		}
	}

	private static EntityHitResult correctEntityHit(EntityHitResult ehr, Vec3d start, Vec3d end) {
		if (ehr == null) return null;
		return new EntityHitResult(ehr.getEntity(), ehr.getEntity().getBoundingBox().expand(0.3).raycast(start, end).orElse(end));
	}

	public int calcAdjustedUseTime(ItemStack stack, int remainingUseTicks) {
		return (int)calcAdjustedUseTime(stack, (float)remainingUseTicks);
	}
	
	public float calcAdjustedUseTime(ItemStack stack, float remainingUseTicks) {
		int diff = getMaxUseTime(stack)-getTrueMaxUseTime(stack);
		remainingUseTicks -= diff;
		int max = getTrueMaxUseTime(stack);
		return (((max-remainingUseTicks)/max)*140);
	}

	public RifleMode getMode(ItemStack stack) {
		return Enums.getIfPresent(RifleMode.class, stack.hasNbt() ? stack.getNbt().getString("Mode") : RifleMode.DAMAGE.name()).or(RifleMode.DAMAGE);
	}
	
	public boolean setMode(ItemStack stack, RifleMode mode) {
		if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
		RifleMode cur = getMode(stack);
		if (cur == mode) return false;
		stack.getNbt().putString("Mode", mode.name());
		stack.getNbt().putBoolean("WasSelected", false);
		stack.getNbt().putBoolean("FiringFromCan", false);
		int ammo = getRemainingAmmo(stack);
		setRemainingAmmo(stack, 0);
		return ammo > 0;
	}
	
	public int getRemainingAmmo(ItemStack stack) {
		return stack.hasNbt() ? stack.getNbt().getInt("RemainingAmmo") : 0;
	}
	
	public void setRemainingAmmo(ItemStack stack, int ammo) {
		if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
		stack.getNbt().putInt("RemainingAmmo", ammo);
	}
	
	public int getMaxAmmo(ItemStack stack) {
		return getMode(stack).shotsPerItem/ammoMod;
	}
	
	public float calculatePower(int i) {
		float power = 0;
		if (simpleCurve) {
			if (i > 30) {
				int j = i - 30;
				power = j/40f;
				if (power > 1) {
					power = 1;
				}
				if (j > 80) {
					power += (j-80)/50f;
				}
				if (power > 1.24f) power = 1.24f;
			}
		} else {
			// https://blob.jortage.com/blobs/d/b5f/db5fc6177921437d567ff378e36d8d9519998d1bc0de8ea2003d25258994c38ed8e69685a8fbd723466a8c0a27beb61786c1c78633e7d335584f399e78a8e212
			if (i == 132 || i == 133) {
				power = 1.3f;
			} else if (i > 30) {
				int j = i - 30;
				if (j > 80) {
					power = 0.8f+(MathHelper.sin(((j-90)/25f)*(float)Math.PI)/2);
				} else if (j > 60) {
					power = 0.7f-(MathHelper.sin(((j-60)/40f)*(float)Math.PI)*0.4f);
				} else {
					power = MathHelper.sin((j/80f)*(float)Math.PI);
				}
			}
		}
		return power;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity _user) {
		if (!(_user instanceof PlayerEntity)) return stack;
		PlayerEntity user = (PlayerEntity)_user;
		user.playSound(YSounds.RIFLE_OVERCHARGE, 1, 1);
		user.damage(user.getDamageSources().create(YDamageTypes.OVERCHARGE), 8*speedMod);
		user.setOnFireFor((int)(3*speedMod));
		if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
		SlotReference can = getAmmoCanSlot(user, getMode(stack));
		if (can == null) {
			setRemainingAmmo(stack, 0);
		} else {
			int shots = can.getStack().getNbt().getInt("Shots");
			shots -= 10;
			if (shots <= 0) {
				can.setStack(new ItemStack(YItems.EMPTY_AMMO_CAN));
			} else {
				can.getStack().getNbt().putInt("Shots", shots);
			}
			if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
			stack.getNbt().putBoolean("FiringFromCan", true);
			stack.getNbt().putFloat("LastCanFullness", shots/(float)AmmoCanItem.CAPACITY);
		}
		if (!world.isClient) {
			getMode(stack).handleBackfire(user, stack);
		}
		YStats.add(user, YStats.RIFLE_SHOTS_BACKFIRED, 1);
		user.getItemCooldownManager().set(this, (int)(160*speedMod));
		return stack;
	}
	
	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		YStats.add(user, YStats.RIFLE_CHARGING_TIME, 1);
		int useTicks = calcAdjustedUseTime(stack, remainingUseTicks);
		if (world.isClient && YConfig.Rifle.timingAssist) {
			if (!simpleCurve) {
				if (useTicks == 70) {
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 0.8f);
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 0.5f);
				} else if (useTicks == 110 || useTicks == 120) {
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1f);
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.2f);
				} else if (useTicks == 130) {
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 2f);
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
				}
			} else {
				if (useTicks == 70) {
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 0.8f);
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 0.5f);
				} else if (useTicks == 122) {
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 2f);
					user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
				}
			}
		}
		
		if (useTicks >= 140 && YConfig.General.trustPlayers) {
			if (world.isClient) {
				user.clearActiveItem();
				finishUsing(stack, world, user);
				new MessageC2STrustedRifleFire(0).sendToServer();
				return;
			}
		}
		if (remainingUseTicks%5 == 0) {
			user.playSound(YSounds.RIFLE_CHARGE_CONTINUE, 1, 1);
			if (useTicks >= 100) {
				user.playSound(YSounds.RIFLE_CHARGE_RATTLE, 1, 1);
			}
		}
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		boolean wasSelected = stack.hasNbt() && stack.getNbt().getBoolean("WasSelected");
		if (selected != wasSelected) {
			if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
			stack.getNbt().putBoolean("WasSelected", selected);
			if (wasSelected) {
				world.playSoundFromEntity(null, entity, YSounds.RIFLE_CHARGE_CANCEL, entity.getSoundCategory(), 1, 1);
			}
		}
		if (entity.age % 4 == 0 && entity instanceof PlayerEntity) {
			if (((PlayerEntity)entity).getItemCooldownManager().isCoolingDown(this)) {
				entity.playSound(YSounds.RIFLE_VENT, selected ? 0.5f : 0.2f, 0.6f+ThreadLocalRandom.current().nextFloat()/3);
			}
		}
	}
	
	@Override
	public boolean hasGlint(ItemStack stack) {
		return false;
	}
	
	public static Vec3d getMuzzlePos(Entity entity, boolean firstPerson) {
		Arm arm = entity instanceof LivingEntity ? ((LivingEntity)entity).getMainArm() : Arm.RIGHT;
		Vec3d eyes = entity.getCameraPosVec(0);
		Vec3d look = entity.getRotationVec(0);
		Vec3d right = ((AccessorEntity)entity).yttr$invokeGetRotationVector(0, entity.getYaw(0)+90);
		Vec3d down = look.crossProduct(right);
		if (entity instanceof LivingEntity) {
			ItemStack stack = ((LivingEntity) entity).getStackInHand(Hand.MAIN_HAND);
			if (stack.getItem() instanceof RifleItem && stack.hasNbt() && stack.getNbt().getBoolean("Scoped")) {
				return eyes.add(down.multiply(0.075));
			}
		}
		if (arm == Arm.LEFT) right = right.multiply(-1);
		return eyes.add(look.multiply(0.5)).add(right.multiply(0.25)).add(down.multiply(0.075));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return baseColor;
		RifleItem item = ((RifleItem)stack.getItem());
		RifleMode mode = ((RifleItem)stack.getItem()).getMode(stack);
		float v;
		if (stack.hasNbt() && stack.getNbt().getBoolean("FiringFromCan")) {
			v = stack.getNbt().getFloat("LastCanFullness");
		} else {
			v = (item.getRemainingAmmo(stack)/(float)(item.getMaxAmmo(stack)));
		}
		return getPortionColor(tintIndex-1, 6, v, mode.color, baseColor);
	}

	@Environment(EnvType.CLIENT)
	public static int getPortionColor(int idx, int portions, float v, int filledColor, int emptyColor) {
		v *= portions;
		int vi = (int)v;
		if (vi > idx) return filledColor;
		float a = vi < idx ? 1 : 1-(v%1);
		float rF = (((emptyColor>>16)&0xFF)/255f);
		float gF = (((emptyColor>>8)&0xFF)/255f);
		float bF = ((emptyColor&0xFF)/255f);
		float rE = (((emptyColor>>16)&0xFF)/255f)+0.05f;
		float gE = (((emptyColor>>8)&0xFF)/255f)+0.05f;
		float bE = ((emptyColor&0xFF)/255f)+0.15f;
		float r = rF+((rE-rF)*a);
		float g = gF+((gE-gF)*a);
		float b = bF+((bE-bF)*a);
		return 0xFF000000 | ((int)(r*255)<<16) | ((int)(g*255)<<8) | ((int)(b*255));
	}

	@Override
	public String getState(PlayerEntity player, ItemStack stack, boolean fHeld) {
		if (player.isUsingItem() && player.getActiveHand() == Hand.MAIN_HAND) return "charge";
		return fHeld ? "mode" : "normal";
	}

}
