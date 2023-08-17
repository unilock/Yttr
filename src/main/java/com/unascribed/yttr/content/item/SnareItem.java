package com.unascribed.yttr.content.item;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import org.jetbrains.annotations.Nullable;

import com.unascribed.lib39.sandman.api.TicksAlwaysItem;
import com.unascribed.yttr.SpecialSubItems;
import com.unascribed.yttr.client.cache.SnareEntityTextureCache;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mixin.accessor.AccessorLivingEntity;
import com.unascribed.yttr.mixin.accessor.AccessorMobEntity;
import com.unascribed.yttr.util.AdventureHelper;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Charsets;
import com.google.common.base.Enums;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class SnareItem extends Item implements ItemColorProvider, TicksAlwaysItem, SpecialSubItems {

	private static final int maxDamage = 40960;
	
	public SnareItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		return ActionResult.PASS;
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		BlockHitResult hr = raycast(world, user, FluidHandling.NONE);
		Vec3d start = user.getCameraPosVec(1);
		Vec3d end = hr.getPos();
		Entity hit = null;
		EntityHitResult ehr = ProjectileUtil.getEntityCollision(world, user, start, end, new Box(start, end).expand(0.2), e -> true);
		if (ehr != null) {
			end = ehr.getPos();
			hit = ehr.getEntity();
		}
		if (!AdventureHelper.canUse(user, stack, world, end)) return TypedActionResult.fail(stack);
		if (stack.hasNbt() && stack.getNbt().contains("Contents")) {
			if (world.isClient) return TypedActionResult.success(stack, false);
			boolean miss = ehr == null && hr.getType() == Type.MISS;
			if (miss) {
				end = start.add(user.getRotationVec(1).multiply(3));
			}
			Entity e = release(user, world, stack, end, -user.getHeadYaw(), false);
			if (e instanceof FallingBlockEntity) {
				FallingBlockEntity fbe = (FallingBlockEntity)e;
				if (ehr == null && hr.getType() == Type.BLOCK) {
					BlockState bs = fbe.getBlockState();
					BlockPos target = world.getBlockState(hr.getBlockPos()).materialReplaceable() ? hr.getBlockPos() : hr.getBlockPos().offset(hr.getSide());
					AutomaticItemPlacementContext ctx = new AutomaticItemPlacementContext(world, target, user.getHorizontalFacing(), new ItemStack(bs.getBlock()), hr.getSide()) {
						@Override
						public float getPlayerYaw() {
							return user.getYaw(1);
						}
					};
					if (world.getBlockState(target).canReplace(ctx) && fbe.getBlockState().canPlaceAt(world, target)) {
						fbe.discard();
						try {
							BlockState placement = bs.getBlock().getPlacementState(ctx);
							if (placement.getBlock() == bs.getBlock()) {
								for (Property prop : bs.getProperties()) {
									if (prop == Properties.ROTATION || placement.get(prop) instanceof Direction || prop == Properties.CHEST_TYPE || prop == Properties.WATERLOGGED) {
										bs = bs.with(prop, placement.get(prop));
									}
								}
							}
						} catch (Throwable t) {
							YLog.warn("Failed to update rotation for snare placement", t);
						}
						world.setBlockState(target, bs);
						for (Direction dir : Direction.values()) {
							bs = bs.getStateForNeighborUpdate(dir, bs, world, target, target.offset(dir));
						}
						world.setBlockState(target, bs);
						if (fbe.blockEntityData != null) {
							BlockEntity be = world.getBlockEntity(target);
							if (be != null) {
								NbtCompound data = be.toNbt();
								NbtCompound incoming = fbe.blockEntityData.copy();
								incoming.remove("x");
								incoming.remove("y");
								incoming.remove("z");
								data.copyFrom(incoming);
								be.readNbt(data);
							}
						}
					} else if (fbe.blockEntityData != null) {
						return TypedActionResult.fail(stack);
					} else {
						world.spawnEntity(e);
					}
				} else if (fbe.blockEntityData != null) {
					return TypedActionResult.fail(stack);
				} else {
					world.spawnEntity(e);
				}
			} else {
				world.spawnEntity(e);
			}
			if (e != null && miss) {
				e.setVelocity(user.getRotationVec(1).multiply(0.75).add(user.getVelocity()));
			}
			stack.getNbt().remove("Contents");
			world.playSound(null, end.x, end.y, end.z, YSounds.SNARE_PLOP, SoundCategory.PLAYERS, 1.0f, 0.75f);
			world.playSound(null, end.x, end.y, end.z, YSounds.SNARE_PLOP, SoundCategory.PLAYERS, 1.0f, 0.95f);
			world.playSound(null, end.x, end.y, end.z, YSounds.SNARE_RELEASE, SoundCategory.PLAYERS, 0.3f, 1.75f);
			return TypedActionResult.success(stack, true);
		} else {
			BlockPos toDelete = null;
			BlockState deleteState = null;
			if (user.isSneaking() && hit == null && hr.getType() != Type.MISS) {
				BlockState bs = world.getBlockState(hr.getBlockPos());
				BlockEntity be = world.getBlockEntity(hr.getBlockPos());
				if ((be == null || bs.isIn(YTags.Block.SNAREABLE)) && !bs.isIn(YTags.Block.UNSNAREABLE)) {
					if (bs.getHardness(world, hr.getBlockPos()) >= 0) {
						// warding/protection mods
						if (user.isBlockBreakingRestricted(world, hr.getBlockPos(), GameMode.SURVIVAL)) return TypedActionResult.fail(stack);
						// CanPlaceOn support
						if (!stack.canPlaceOn(Registries.BLOCK, new CachedBlockPosition(world, hr.getBlockPos(), false))) return TypedActionResult.fail(stack);
						// refine CanUseInBox to the center of the block to help with literal edge cases
						if (!AdventureHelper.canUse(user, stack, world, hr.getBlockPos())) return TypedActionResult.fail(stack);
						toDelete = hr.getBlockPos();
						boolean waterlogged = bs.getBlock() instanceof Waterloggable && bs.get(Properties.WATERLOGGED);
						deleteState = waterlogged ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
						if (waterlogged) bs = bs.with(Properties.WATERLOGGED, false);
						FallingBlockEntity fbe = new FallingBlockEntity(world, hr.getBlockPos().getX()+0.5, hr.getBlockPos().getY(), hr.getBlockPos().getZ()+0.5, bs);
						fbe.dropItem = true;
						fbe.timeFalling = 2;
						if (be != null) {
							NbtCompound data = be.toNbt();
							fbe.blockEntityData = data;
						}
						hit = fbe;
						if (be != null && user instanceof ServerPlayerEntity) {
							YCriteria.SNARE_BLOCK_ENTITY.trigger((ServerPlayerEntity)user);
						}
					}
				}
			}
			if (hit == null) return TypedActionResult.pass(stack);
			if (world.isClient) return TypedActionResult.success(stack, false);
			if (!hit.isAlive()) return TypedActionResult.fail(stack);
			if (hit instanceof PlayerEntity || hit.getType().isIn(com.unascribed.yttr.init.YTags.Entity.UNSNAREABLE) || hit.hasPassengers()) return TypedActionResult.fail(stack);
			if (!hit.getType().isIn(com.unascribed.yttr.init.YTags.Entity.SNAREABLE_NONLIVING) && !(hit instanceof LivingEntity) && !(hit instanceof FallingBlockEntity)) return TypedActionResult.fail(stack);
			if (hit instanceof ItemEntity && ((ItemEntity)hit).getStack().isIn(com.unascribed.yttr.init.YTags.Item.UNSNAREABLE)) return TypedActionResult.fail(stack);
			NbtCompound data = new NbtCompound();
			if (hit.saveSelfNbt(data)) {
				boolean tryingToCheatSnareTimer = checkForCheating(data);
				if (tryingToCheatSnareTimer) return TypedActionResult.fail(stack);
				damage(stack, 400, () -> user.sendToolBreakStatus(hand));
				if (stack.isEmpty()) return TypedActionResult.fail(ItemStack.EMPTY);
				if (toDelete != null) {
					world.removeBlockEntity(toDelete);
					world.setBlockState(toDelete, deleteState);
				}
				stack.getNbt().remove("AmbientSound");
				stack.getNbt().remove("AmbientSoundTimer");
				stack.getNbt().remove("AmbientSoundDelay");
				stack.getNbt().remove("AmbientSoundPitches");
				stack.getNbt().remove("AmbientSoundVolumes");
				stack.getNbt().remove("AmbientSoundCategory");
				if (hit instanceof LivingEntity) {
					((AccessorLivingEntity)hit).yttr$playHurtSound(world.getDamageSources().generic());
					if (user instanceof ServerPlayerEntity) {
						YCriteria.SNARE_LIVING_ENTITY.trigger((ServerPlayerEntity)user);
					}
				}
				hit.playSound(YSounds.SNARE_PLOP, 1.0f, 0.5f);
				hit.playSound(YSounds.SNARE_PLOP, 1.0f, 0.75f);
				hit.playSound(YSounds.SNARE_GRAB, 0.2f, 2f);
				if (hit instanceof MobEntity) {
					MobEntity mob = ((MobEntity) hit);
					SoundEvent sound = ((AccessorMobEntity)hit).yttr$getAmbientSound();
					if (sound != null) {
						Identifier id = Registries.SOUND_EVENT.getId(sound);
						stack.getNbt().putString("AmbientSound", id.toString());
						stack.getNbt().putInt("AmbientSoundTimer", -mob.getMinAmbientSoundDelay());
						stack.getNbt().putInt("AmbientSoundDelay", mob.getMinAmbientSoundDelay());
						int[] soundPitches = new int[10];
						int[] soundVolumes = new int[10];
						for (int i = 0; i < soundPitches.length; i++) {
							soundPitches[i] = Float.floatToIntBits(((AccessorLivingEntity)hit).yttr$getSoundPitch());
							soundVolumes[i] = Float.floatToIntBits(((AccessorLivingEntity)hit).yttr$getSoundVolume());
						}
						stack.getNbt().putIntArray("AmbientSoundPitches", soundPitches);
						stack.getNbt().putIntArray("AmbientSoundVolumes", soundVolumes);
						stack.getNbt().putString("AmbientSoundCategory", hit.getSoundCategory().name());
					}
				}
				boolean baby = hit instanceof LivingEntity && ((LivingEntity)hit).isBaby();
				hit.discard();
				if (!stack.hasNbt()) stack.setNbt(new NbtCompound());
				stack.getNbt().putLong("LastUpdate", user.getWorld().getServer().getTicks());
				stack.getNbt().put("Contents", data);
				stack.getNbt().putBoolean("Baby", baby);
				return TypedActionResult.success(stack, true);
			} else {
				return TypedActionResult.fail(stack);
			}
		}
	}
	
	private boolean checkForCheating(NbtCompound data) {
		for (String key : data.getKeys()) {
			if (key.contains("yttr:snare")) return true;
			if (checkForCheating(data.get(key))) return true;
		}
		return false;
	}
	
	private boolean checkForCheating(NbtList data) {
		for (int i = 0; i < data.size(); i++) {
			if (checkForCheating(data.get(i))) return true;
		}
		return false;
	}
	
	private boolean checkForCheating(NbtElement tag) {
		if (tag instanceof NbtString) {
			return ((NbtString)tag).asString().contains("yttr:snare");
		} else if (tag instanceof NbtList) {
			return checkForCheating((NbtList)tag);
		} else if (tag instanceof NbtCompound) {
			return checkForCheating((NbtCompound)tag);
		}
		return false;
	}

	@Override
	public Text getName(ItemStack stack) {
		EntityType<?> type = getEntityType(stack);
		if (type != null) {
			if (type == EntityType.ITEM) {
				return Text.translatable("item.yttr.snare.filled", ItemStack.fromNbt(stack.getNbt().getCompound("Contents").getCompound("Item")).getName());
			} else if (type == EntityType.FALLING_BLOCK) {
				return Text.translatable("item.yttr.snare.filled",
						NbtHelper.toBlockState(Registries.BLOCK.asLookup(),
								stack.getNbt().getCompound("Contents").getCompound("BlockState")).getBlock().getName());
			}
			return Text.translatable("item.yttr.snare.filled", type.getName());
		}
		return super.getName(stack);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		Text msg = getContainmentMessage(world, stack);
		if (msg != null) {
			tooltip.add(msg);
		}
	}
	
	private Text getContainmentMessage(World world, ItemStack stack) {
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			int ticksLeft = ((maxDamage-stack.getDamage())/dmg)*(EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack)+1);
			ticksLeft -= getCheatedTicks(world, stack);
			if (ticksLeft < 0) {
				return Text.translatable("tip.yttr.snare.failed").formatted(Formatting.RED);
			} else {
				int seconds = ticksLeft/20;
				int minutes = seconds/60;
				seconds = seconds%60;
				return Text.translatable("tip.yttr.snare.unstable", minutes, Integer.toString(seconds+100).substring(1))
						.formatted(minutes <= 1 ? minutes == 0 && seconds <= 30 ? Formatting.RED : Formatting.YELLOW : Formatting.GRAY);
			}
		} else if (stack.hasNbt() && stack.getNbt().contains("Contents")) {
			return Text.translatable("tip.yttr.snare.stable").formatted(Formatting.GRAY);
		} else {
			return null;
		}
	}

	private int getCheatedTicks(World world, ItemStack stack) {
		if (world.isClient) return 0;
		long lastUpdate = stack.hasNbt() ? stack.getNbt().getLong("LastUpdate") : 0;
		if (lastUpdate == 0) return 0;
		long cheatedTicks = world.getServer().getTicks()-lastUpdate;
		if (cheatedTicks < 5) return 0;
		return Ints.saturatedCast(cheatedTicks);
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		handleEntityEffects(stack, world, entity.getPos(), selected);
		if (world.isClient) return;
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			if (damage(stack, dmg*(getCheatedTicks(world, stack)+1), null)) {
				stack.decrement(1);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.SNARE_PLOP, entity.getSoundCategory(), 1.0f, 0.75f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.SNARE_PLOP, entity.getSoundCategory(), 1.0f, 0.95f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.SNARE_BREAK, entity.getSoundCategory(), 0.7f, 1.75f);
				world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.SNARE_BREAK, entity.getSoundCategory(), 0.5f, 1.3f);
				release((entity instanceof PlayerEntity) ? (PlayerEntity)entity : null, world, stack, entity.getPos(), entity.getYaw(1), true);
			}
		}
		if (stack.hasNbt() && stack.getNbt().contains("Contents")) {
			stack.getNbt().putLong("LastUpdate", world.getServer().getTicks());
		}
		if (entity instanceof PlayerEntity && selected) {
			Text msg = getContainmentMessage(world, stack);
			if (msg != null) {
				((PlayerEntity) entity).sendMessage(msg, true);
			}
		}
	}
	
	@Override
	public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
		handleEntityEffects(stack, world, Vec3d.ofCenter(pos), false);
		int dmg = calculateDamageRate(world, stack);
		if (dmg > 0) {
			if (damage(stack, dmg*(getCheatedTicks(world, stack)+1), null)) {
				stack.decrement(1);
				world.playSound(null, pos, YSounds.SNARE_PLOP, SoundCategory.BLOCKS, 1.0f, 0.75f);
				world.playSound(null, pos, YSounds.SNARE_PLOP, SoundCategory.BLOCKS, 1.0f, 0.95f);
				world.playSound(null, pos, YSounds.SNARE_BREAK, SoundCategory.BLOCKS, 0.7f, 1.75f);
				world.playSound(null, pos, YSounds.SNARE_BREAK, SoundCategory.BLOCKS, 0.5f, 1.3f);
				release(null, world, stack, Vec3d.ofBottomCenter(pos.up()), 0, true);
			}
		}
		if (stack.hasNbt() && stack.getNbt().contains("Contents")) {
			stack.getNbt().putLong("LastUpdate", world.getServer().getTicks());
		}
	}
	
	private boolean damage(ItemStack stack, int amt, Runnable breakCallback) {
		if (stack.hasNbt() && stack.getNbt().getBoolean("Unbreakable")) return false;
		int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);
		amt = (amt+unbreaking)/(unbreaking+1);
		stack.setDamage(stack.getDamage()+amt);
		boolean broke = stack.getDamage() > maxDamage;
		if (broke && breakCallback != null) breakCallback.run();
		return broke;
	}

	private void handleEntityEffects(ItemStack stack, World world, Vec3d pos, boolean selected) {
		if (!world.isClient && stack.hasNbt() && stack.getNbt().contains("AmbientSound") && stack.getNbt().contains("Contents")) {
			int ambientSoundTimer = stack.getNbt().getInt("AmbientSoundTimer");
			ambientSoundTimer += getCheatedTicks(world, stack)+1;
			if (ThreadLocalRandom.current().nextInt(1000) < ambientSoundTimer) {
				ambientSoundTimer = -stack.getNbt().getInt("AmbientSoundDelay");
				int[] pitches = stack.getNbt().getIntArray("AmbientSoundPitches");
				int[] volumes = stack.getNbt().getIntArray("AmbientSoundVolumes");
				Identifier id = Identifier.tryParse(stack.getNbt().getString("AmbientSound"));
				if (id == null) return;
				SoundEvent sound = Registries.SOUND_EVENT.getOrEmpty(id).orElse(null);
				if (sound == null) return;
				SoundCategory category = Enums.getIfPresent(SoundCategory.class, stack.getNbt().getString("AmbientSoundCategory")).or(SoundCategory.MASTER);
				world.playSound(null, pos.x, pos.y, pos.z, sound, category, Float.intBitsToFloat(volumes[ThreadLocalRandom.current().nextInt(volumes.length)])/(selected ? 2 : 3), Float.intBitsToFloat(pitches[ThreadLocalRandom.current().nextInt(pitches.length)]));
			}
			stack.getNbt().putInt("AmbientSoundTimer", ambientSoundTimer);
		}
		EntityType<?> type = getEntityType(stack);
		if (type == EntityType.FALLING_BLOCK) {
			var data = stack.getNbt().getCompound("Contents");
			var bs = NbtHelper.toBlockState(world.filteredLookup(RegistryKeys.BLOCK), data.getCompound("BlockState"));
			if (bs.isOf(Blocks.SPAWNER) && data.contains("TileEntityData", NbtElement.COMPOUND_TYPE)) {
				var bp = BlockPos.fromPosition(pos);
				var logic = new MobSpawnerLogic() {
					@Override
					public void sendStatus(World world, BlockPos pos, int i) {
						// nah
					}
				};
				var ted = data.getCompound("TileEntityData");
				logic.readNbt(world, bp, ted);
				if (world.isClient) {
					logic.clientTick(world, bp);
				} else if (world instanceof ServerWorld sw) {
					int cheated = getCheatedTicks(world, stack);
					for (int i = 0; i < cheated+1; i++) {
						logic.serverTick(sw, bp);
					}
					logic.writeNbt(ted);
				}
			}
		}
	}

	private int calculateDamageRate(World world, ItemStack stack) {
		if (stack.hasNbt() && stack.getNbt().getBoolean("Unbreakable")) return 0;
		EntityType<?> type = getEntityType(stack);
		if (type != null) {
			if (type == EntityType.ARMOR_STAND || type == EntityType.ITEM) return 0;
			NbtCompound data = stack.getNbt().getCompound("Contents");
			int dmg = MathHelper.ceil(data.getFloat("Health")*MathHelper.sqrt(type.getDimensions().height*type.getDimensions().width));
			if (type == EntityType.FALLING_BLOCK) {
				var bs = NbtHelper.toBlockState(world.filteredLookup(RegistryKeys.BLOCK),
						data.getCompound("BlockState"));
				if (bs.isOf(Blocks.SPAWNER)) {
					dmg += 15;
				}
			}
			switch (type.getSpawnGroup()) {
				case AMBIENT:
				case WATER_AMBIENT:
					dmg /= 2;
					break;
				case CREATURE:
				case WATER_CREATURE:
					dmg /= 4;
					break;
				default:
					break;
			}
			if (type.isIn(com.unascribed.yttr.init.YTags.Entity.BOSSES)) {
				dmg *= 4;
			}
			if (stack.getNbt().getBoolean("Baby")) {
				dmg /= 2;
			}
			for (var nbt : data.getList("ActiveEffects", NbtElement.COMPOUND_TYPE)) {
				var se = StatusEffectInstance.fromNbt((NbtCompound)nbt);
				if (se.getEffectType() == StatusEffects.WEAKNESS) {
					dmg /= (se.getAmplifier()+2);
				} else if (se.getEffectType() == StatusEffects.STRENGTH) {
					dmg *= (se.getAmplifier()+2);
				} else if (se.getEffectType() == StatusEffects.INVISIBILITY) {
					dmg *= 2;
				}
			}
			return dmg;
		}
		return 0;
	}

	public EntityType<?> getEntityType(ItemStack stack) {
		if (!stack.hasNbt()) return null;
		NbtCompound data = stack.getNbt().getCompound("Contents");
		Identifier id = Identifier.tryParse(data.getString("id"));
		if (id == null) return null;
		return Registries.ENTITY_TYPE.getOrEmpty(id).orElse(null);
	}

	private Entity release(@Nullable PlayerEntity player, World world, ItemStack stack, Vec3d pos, float yaw, boolean spawn) {
		if (!(world instanceof ServerWorld)) return null;
		Entity e = createEntity(world, stack);
		if (e != null) {
			if (e instanceof ItemEntity && ((ItemEntity)e).getStack().getItem() instanceof ArrowItem && ((ItemEntity)e).getStack().getCount() == 1 && player != null) {
				e = ((ArrowItem)((ItemEntity)e).getStack().getItem()).createArrow(world, ((ItemEntity)e).getStack(), player);
			} else {
				e.setBodyYaw(yaw);
				e.setHeadYaw(yaw);
				e.setPitch(0);
				e.setVelocity(0, 0, 0);
				e.fallDistance = 0;
			}
			e.refreshPositionAfterTeleport(pos);
			if (spawn) world.spawnEntity(e);
			if (player != null) {
				// so that lastAttackedTime gets updated and RevengeGoal fires
				e.age = -1;
				e.damage(world.getDamageSources().playerAttack(player), 0);
				e.age = 0;
			}
			if (spawn) stack.getNbt().remove("Contents");
			return e;
		}
		return null;
	}

	public Entity createEntity(World world, ItemStack stack) {
		EntityType<?> type = getEntityType(stack);
		if (type == null) return null;
		Entity e = type.create(world);
		e.readNbt(stack.getNbt().getCompound("Contents"));
		return e;
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (group == YItemGroups.SNARES) {
			for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> en : Registries.ENTITY_TYPE.getEntries()) {
				EntityType<?> e = en.getValue();
				if (e == EntityType.ITEM || e == EntityType.FALLING_BLOCK) continue;
				if ((e.getSpawnGroup() != SpawnGroup.MISC || e.isIn(com.unascribed.yttr.init.YTags.Entity.SNAREABLE_NONLIVING)) && !e.isIn(com.unascribed.yttr.init.YTags.Entity.UNSNAREABLE)) {
					ItemStack is = new ItemStack(this);
					is.getOrCreateSubNbt("Contents").putString("id", en.getKey().getValue().toString());
					stacks.add(is);
				}
			}
		} else if (group == YItemGroups.GENERAL) {
			stacks.add(new ItemStack(this));
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex == 0) return -1;
		EntityType<?> type = YItems.SNARE.getEntityType(stack);
		if (type != null) {
			int primary;
			int secondary;
			Identifier tex = SnareEntityTextureCache.get(stack);
			if (tex != null && tex != TextureColorThief.MISSINGNO) {
				primary = TextureColorThief.getPrimaryColor(tex);
				secondary = TextureColorThief.getSecondaryColor(tex);
			} else {
				SpawnEggItem spi = SpawnEggItem.forEntity(type);
				if (spi != null) {
					primary = spi.getColor(0);
					secondary = spi.getColor(1);
				} else {
					primary = Hashing.murmur3_32_fixed().hashString(Registries.ENTITY_TYPE.getId(type).toString(), Charsets.UTF_8).asInt();
					secondary = ~primary;
				}
			}
			return tintIndex == 1 ? primary : secondary;
		} else {
			return -1;
		}
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return stack.getDamage() > 0;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return Math.round(13.0F - stack.getDamage() * 13.0F / maxDamage);
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float)maxDamage - (float)stack.getDamage()) / maxDamage);
		return MathHelper.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
	
}
