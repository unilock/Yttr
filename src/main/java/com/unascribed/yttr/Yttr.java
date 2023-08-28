package com.unascribed.yttr;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.unascribed.yttr.init.*;
import org.jetbrains.annotations.Nullable;

import com.unascribed.lib39.core.api.AutoRegistry;
import com.unascribed.lib39.crowbar.api.WorldGenerationEvents;
import com.unascribed.lib39.dessicant.api.DessicantControl;
import com.unascribed.lib39.mesh.api.BlockNetworkManager;
import com.unascribed.lib39.util.api.SlotReference;
import com.unascribed.yttr.compat.trinkets.YttrTrinketsCompat;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.conditional.YTrinkets;
import com.unascribed.yttr.inred.InRedLogic;
import com.unascribed.yttr.mechanics.SuitResource;
import com.unascribed.yttr.mixinsupport.ComplexDamageEnchant;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.network.MessageS2CDiscoveredGeyser;
import com.unascribed.yttr.network.MessageS2CDive;
import com.unascribed.yttr.util.EquipmentSlots;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.world.Geyser;
import com.unascribed.yttr.world.GeysersState;
import com.unascribed.yttr.world.ScorchedGenerator;
import com.unascribed.yttr.world.SqueezeSaplingGenerator;
import com.unascribed.yttr.world.WastelandPopulator;
import com.unascribed.yttr.world.network.FilterNetwork;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class Yttr implements ModInitializer {
	
	public static final int DIVING_BLOCKS_PER_TICK = 2;
	
	public static boolean lessCreepyAwareHopper;
	public static boolean vectorSuit;
	
	public static final List<DelayedTask> delayedServerTasks = Lists.newArrayList();
	
	public static final AutoRegistry autoreg = AutoRegistry.of("yttr");
	
	public interface TrinketsAccess {
		Optional<SlotReference> getWorn(PlayerEntity pe, Predicate<Item> predicate);
		int count(PlayerEntity pe, ToIntFunction<ItemStack> func);
		void dropMagneticTrinkets(PlayerEntity pe);
	}
	
	public interface EarsAccess {
		boolean isVisuallyWearingBoots(PlayerEntity pe);
		float getChestSize(PlayerEntity pe);
	}
	
	public static TrinketsAccess trinketsAccess = new TrinketsAccess() {

		@Override
		public Optional<SlotReference> getWorn(PlayerEntity pe, Predicate<Item> predicate) {
			return Optional.empty();
		}
		
		@Override
		public int count(PlayerEntity pe, ToIntFunction<ItemStack> func) {
			return 0;
		}

		@Override
		public void dropMagneticTrinkets(PlayerEntity pe) {
			
		}
		
	};
	
	public static EarsAccess earsAccess = new EarsAccess() {
		
		@Override
		public boolean isVisuallyWearingBoots(PlayerEntity pe) {
			return !pe.getEquippedStack(EquipmentSlot.FEET).isEmpty();
		}
		
		@Override
		public float getChestSize(PlayerEntity pe) {
			return 0;
		}
	};
	
	@Override
	public void onInitialize() {
		// base content
		YBlocks.init();
		YBlockEntities.init();
		YItems.init();
		YSounds.init();
		YFluids.init();
		YEntities.init();
		
		// auxillary content
		YStatusEffects.init();
		YRecipeTypes.init();
		YRecipeSerializers.init();
		YCommands.init();
		YTags.init();
		YHandledScreens.init();
		YEnchantments.init();
		
		// miscellaneous other stuff
		YStats.init();
		YDamageTypes.init();
		YCriteria.init();
		YBrewing.init();
		YTrades.init();
		YNetwork.init();
		YFuels.init();
		
		BlockNetworkManager.registerNetworkType(id("filter"), FilterNetwork.TYPE);
		
		// conditional content
		
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			try {
				YttrTrinketsCompat.init();
				YTrinkets.init();
			} catch (Throwable t) {
				YLog.warn("Failed to load Trinkets compat", t);
			}
		}
		
		// psuedoregistries
		
		YItemGroups.init();
		YGameRules.init();
		
		DessicantControl.optIn("yttr");

		// events
		
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
			if (player instanceof ServerPlayerEntity) {
				YCriteria.BROKE_BLOCK.trigger((ServerPlayerEntity)player, pos, state, player.getStackInHand(Hand.MAIN_HAND));
			}
		});
		
		WorldGenerationEvents.AFTER_BUILD_SURFACE.register((ctx) -> {
			ScorchedGenerator.generateSummit(ctx.region(), ctx.chunk());
		});
		WorldGenerationEvents.AFTER_GENERATE_FEATURES.register((ctx) -> {
			ScorchedGenerator.generateTerminus(ctx.region().getSeed(), ctx.region(), ctx.structureManager());
			SqueezeSaplingGenerator.generateNaturalTrees(ctx.region().getSeed(), ctx.region(), ctx.chunk());
		});
		
		ServerTickEvents.START_SERVER_TICK.register((server) -> {
			InRedLogic.onServerTick();
			Iterator<DelayedTask> iter = delayedServerTasks.iterator();
			int tasksRunThisTick = 0;
			while (iter.hasNext()) {
				DelayedTask dt = iter.next();
				if (tasksRunThisTick > 10 && !dt.important) continue;
				if (dt.delay-- <= 0) {
					dt.r.run();
					tasksRunThisTick++;
					iter.remove();
				}
			}
		});
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(Substitutes.RELOADER);
		
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			if (WastelandPopulator.isEligible(world, chunk)) {
				world.getServer().send(new ServerTask(world.getServer().getTicks(), () -> {
					WastelandPopulator.populate(world.getSeed(), world, chunk.getPos());
				}));
			}
			if (ScorchedGenerator.isEligibleForRetrogen(world, chunk)) {
				world.getServer().send(new ServerTask(world.getServer().getTicks(), () -> {
					ScorchedGenerator.retrogen(world.getSeed(), world, chunk.getPos());
				}));
			}
		});
	}
	
	public static Identifier id(String path) {
		return new Identifier("yttr", path);
	}

	public static Multiset<SuitResource> determineAvailableResources(PlayerEntity player) {
		ItemStack is = player.getEquippedStack(EquipmentSlot.CHEST);
		if (!(is.getItem() instanceof SuitArmorItem)) return ImmutableMultiset.of();
		SuitArmorItem sai = (SuitArmorItem)is.getItem();
		Multiset<SuitResource> resourcesAvailable = EnumMultiset.create(SuitResource.class);
		for (SuitResource sr : SuitResource.VALUES) {
			resourcesAvailable.add(sr, sai.getResourceAmount(is, sr));
		}
		return resourcesAvailable;
	}

	public static Multiset<SuitResource> determineNeededResourcesForFastDive(double distance) {
		int simulatedTicks = (int)(distance/DIVING_BLOCKS_PER_TICK);
		int distanceI = (int)distance;
		Multiset<SuitResource> resourcesNeeded = EnumMultiset.create(SuitResource.class);
		for (SuitResource sr : SuitResource.VALUES) {
			if (distance < 4 && sr == SuitResource.FUEL) continue;
			resourcesNeeded.add(sr, sr.getConsumptionPerTick(900)*simulatedTicks);
			resourcesNeeded.add(sr, sr.getConsumptionPerBlock(900)*distanceI);
		}
		return resourcesNeeded;
	}

	/**
	 * Serialize an Inventory to an NbtList. Unlike {@link Inventories#writeNbt}, this supports arbitrarily
	 * large stack sizes. Unlike {@link SimpleInventory#toNbtList}, this keeps slot indexes and therefore
	 * empty slots.
	 * @see #deserializeInv
	 */
	public static NbtList serializeInv(Inventory inv) {
		NbtList out = new NbtList();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (!is.isEmpty()) {
				NbtCompound c = is.writeNbt(new NbtCompound());
				if (is.getCount() > 127) {
					c.putInt("Count", is.getCount());
				}
				c.putInt("Slot", i);
				out.add(c);
			}
		}
		return out;
	}
	
	/**
	 * Deserialize an NbtList created by {@link #serializeInv} into the given Inventory. The
	 * Inventory will be cleared first. Can load large stacks written by serializeInv.
	 */
	public static void deserializeInv(NbtList tag, Inventory inv) {
		inv.clear();
		for (int i = 0; i < tag.size(); i++) {
			NbtCompound c = tag.getCompound(i);
			int count = c.getInt("Count");
			if (count > 127) {
				c = c.copy();
				c.putInt("Count", 1);
			}
			ItemStack is = ItemStack.fromNbt(c);
			is.setCount(count);
			inv.setStack(c.getInt("Slot"), is);
		}
	}
	
	public static void sync(BlockEntity be) {
		if (!be.hasWorld()) return;
		if (be.getWorld().isClient) return;
		be.getWorld().updateListeners(be.getPos(), Blocks.AIR.getDefaultState(), be.getCachedState(), 3);
	}

	/**
	 * @return {@code true} if the give entity is wearing a full Diving Suit
	 */
	public static boolean isWearingFullSuit(Entity entity) {
		if (!(entity instanceof LivingEntity)) return false;
		LivingEntity le = (LivingEntity)entity;
		for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
			if (!(le.getEquippedStack(slot).getItem() instanceof SuitArmorItem)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isStandingOnDivingPlate(Entity e) {
		return e.isOnGround() && e.getWorld().getBlockState(e.getBlockPos().down()).isOf(YBlocks.DIVING_PLATE);
	}

	public static void syncDive(ServerPlayerEntity p) {
		if (!(p instanceof DiverPlayer)) return;
		GeysersState gs = GeysersState.get(p.getServerWorld());
		List<Geyser> geysers = ((DiverPlayer)p).yttr$getKnownGeysers().stream()
				.map(gs::getGeyser).filter(g -> g != null)
				.collect(Collectors.toList());
		new MessageS2CDive((int)p.getPos().x, (int)p.getPos().z, geysers).sendTo(p);
	}

	public static boolean discoverGeyser(UUID id, ServerPlayerEntity player, boolean notify) {
		if (!(player instanceof DiverPlayer)) return false;
		DiverPlayer diver = (DiverPlayer)player;
		Set<UUID> knownGeysers = diver.yttr$getKnownGeysers();
		if (!knownGeysers.contains(id)) {
			Geyser g = GeysersState.get(player.getServerWorld()).getGeyser(id);
			if (g == null) return false;
			knownGeysers.add(id);
			new MessageS2CDiscoveredGeyser(g).sendTo(player);
			return true;
		} else {
			return false;
		}
	}
	
	public static int calculatePressure(ServerWorld world, int x, int z) {
		GeysersState gs = GeysersState.get(world);
		int absoluteMin = 100;
		int minPressure = 120;
		int maxPressure = 1000;
		int pressureGap = maxPressure-minPressure;
		int maxPressureGap = pressureGap+(minPressure-absoluteMin);
		int pressureEffect = 0;
		int falloff = 768;
		int falloffSq = falloff*falloff;
		for (Geyser g : gs.getGeysersInRange(x, z, falloff)) {
			double distSq = g.pos.getSquaredDistance(x, g.pos.getY(), z);
			if (distSq < falloffSq) {
				double effect = (falloffSq-distSq)/falloffSq;
				pressureEffect += pressureGap*effect;
			}
		}
		return maxPressure-Math.min(maxPressureGap, pressureEffect);
	}

	/**
	 * Return a view of the given Inventory as a List. Modifications to the List will pass through
	 * to the Inventory.
	 */
	public static List<ItemStack> asList(Inventory inv) {
		return asListExcluding(inv, -1);
	}
	
	/**
	 * Return a view of the given Inventory as a List, excluding the given slot by treating it as
	 * empty. Modifications to the List will pass through to the Inventory, other than those to
	 * the excluded slot.
	 */
	public static List<ItemStack> asListExcluding(Inventory inv, int exclude) {
		return new AbstractList<ItemStack>() {
			
			@Override
			public ItemStack get(int index) {
				return index == exclude ? ItemStack.EMPTY : inv.getStack(index);
			}

			@Override
			public int size() {
				return inv.size();
			}
			
			@Override
			public ItemStack remove(int index) {
				if (index == exclude) return ItemStack.EMPTY;
				return inv.removeStack(index);
			}
			
			@Override
			public void clear() {
				inv.clear();
			}

			@Override
			public ItemStack set(int index, ItemStack element) {
				if (index == exclude) return ItemStack.EMPTY;
				ItemStack old = inv.getStack(index);
				inv.setStack(index, element);
				return old;
			}
			
		};
	}

	public static int getSpringingLevel(PlayerEntity p) {
		return trinketsAccess.count(p, is -> EnchantmentHelper.getLevel(YEnchantments.SPRINGING.get(), is));
	}

	public static boolean isWearingPlatforms(PlayerEntity p) {
		return trinketsAccess.getWorn(p, YItems.PLATFORMS::is).isPresent();
	}

	public static Optional<SlotReference> getWornCoil(PlayerEntity e) {
		return trinketsAccess.getWorn(e, YItems.CUPROSTEEL_COIL::is);
	}

	public static @Nullable SlotReference scanInventory(Inventory inv, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inv.size(); i++) {
			ItemStack is = inv.getStack(i);
			if (predicate.test(is)) {
				return new SlotReference(inv, i);
			}
		}
		return null;
	}
	
	private static final ThreadLocal<DamageSource> replacementDamageSource = new ThreadLocal<>();
	
	public static float getAdditionalAttackDamage(Entity target, Entity attacker) {
		float dmg = 0;
		replacementDamageSource.set(null);
		if (attacker instanceof LivingEntity le) {
			for (var en : EnchantmentHelper.get(le.getMainHandStack()).entrySet()) {
				if (en.getKey() instanceof ComplexDamageEnchant cde) {
					var res = cde.handleAttack(en.getValue(), target, attacker);
					if (res != null) {
						dmg += res.amount();
						if (res.src() != null) {
							replacementDamageSource.set(res.src());
						}
					}
				}
			}
		}
		return dmg;
	}
	
	public static DamageSource modifyDamageSource(DamageSource orig) {
		var repl = replacementDamageSource.get();
		if (repl != null) return repl;
		return orig;
	}

	public static boolean isEnlightened(PlayerEntity player, boolean requireEmptyHands) {
		if (player == null) return false;
		if (!player.isCreative() || !player.getEquippedStack(EquipmentSlot.HEAD).isOf(YItems.GOGGLES)) return false;
		if (requireEmptyHands) return player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty();
		return true;
	}
	
	public static boolean prefersSurvivalInventory(PlayerEntity player) {
		if (!isEnlightened(player, false)) return false;
		var is = player.getEquippedStack(EquipmentSlot.HEAD);
		return is.hasNbt() && is.getNbt().getBoolean("PreferSurvival");
	}
	
}
