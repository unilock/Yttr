package com.unascribed.yttr.content.block.abomination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;
import com.unascribed.yttr.mechanics.ShatteringLogic;
import com.unascribed.yttr.network.MessageS2CScreeperBreak;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ScreeperNestBlockEntity extends BlockEntity implements YTickable, SidedInventory {

	private static class InfestedBlock {
		private final BlockPos pos;
		private final boolean outOfRange;
		private int timer;
		private int accessoryDepth = ACCESSORY_DEPTH;
		
		public InfestedBlock(BlockPos pos, boolean outOfRange, int timer, int accessoryDepth) {
			this.pos = pos;
			this.outOfRange = outOfRange;
			this.timer = timer;
			this.accessoryDepth = accessoryDepth;
		}

		public boolean isValid(World world, BlockState expected) {
			return matches(world.getBlockState(pos), expected);
		}
		
	}
	
	private static final int ACCESSORY_DEPTH = 12;
	private static final int DETONATE_TIME = 260;
	private static final int SCAN_TIME = 20;
	private static final int MAX_SCANNED_PER_TICK = 240;
	
	private int cooldown = SCAN_TIME;
	
	private BlockState infesting = null;
	private final List<InfestedBlock> infested = new ArrayList<>();
	private InfestedBlock anchor = null;
	private int anchorScanProgress = 0;
	
	private int speedBoostTime = 0;
	private int digBoostTime = 0;
	private int shatterBoostTime = 0;

	private transient final List<InfestedBlock> queue = new ArrayList<>();
	private transient final Set<BlockPos> known = new HashSet<>();
	
	public ScreeperNestBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.SCREEPER_NEST, pos, state);
	}

	@Override
	public void tick() {
		if (world.isClient) return;
		if (!infested.isEmpty()) markDirty();
		if (digBoostTime > 0) digBoostTime--;
		if (speedBoostTime > 0) speedBoostTime--;
		if (shatterBoostTime > 0) shatterBoostTime--;
		if (speedBoostTime > 0) {
			cooldown -= 2;
		}
		known.clear();
		int brokenThisTick = 0;
		var iter = infested.iterator();
		while (iter.hasNext()) {
			var ib = iter.next();
			if (!ib.isValid(world, infesting)) {
				iter.remove();
			} else {
				if (speedBoostTime > 0 && digBoostTime > 0) {
					ib.timer--;
				}
				if (ib.timer-- <= 0) {
					if (brokenThisTick++ > 4) continue;
					iter.remove();
					if (world instanceof ServerWorld sw) {
						var bs = world.getBlockState(ib.pos);
						boolean asplodified = !isAccessory(bs, infesting);
						if (asplodified) {
							sw.emitGameEvent(null, GameEvent.EXPLODE, ib.pos);
						} else {
							sw.emitGameEvent(null, GameEvent.BLOCK_DESTROY, ib.pos);
						}
						var msg = new MessageS2CScreeperBreak(ib.pos, bs, asplodified);
						double cX = ib.pos.getX()+0.5;
						double cY = ib.pos.getY()+0.5;
						double cZ = ib.pos.getZ()+0.5;
						for (PlayerEntity player : sw.getMatchingPlayers(p -> p.squaredDistanceTo(cX, cY, cZ) < 64*64)) {
							msg.sendTo(player);
						}
						for (Entity e : sw.getEntitiesByClass(Entity.class, new Box(pos).expand(0.5), e -> !e.getType().isIn(YTags.Entity.SCREEPER_IMMUNE))) {
							e.damage(sw.getDamageSources().explosion(null, null), 4);
						}
						
						BlockEntity be = bs.hasBlockEntity() ? world.getBlockEntity(ib.pos) : null;
						var ctx = new LootContextParameterSet.Builder(sw)
							.add(LootContextParameters.ORIGIN, Vec3d.ofCenter(ib.pos))
							.add(LootContextParameters.TOOL, ItemStack.EMPTY)
							.addOptional(LootContextParameters.BLOCK_ENTITY, be);
						
						if (asplodified) {
							ctx.add(LootContextParameters.EXPLOSION_RADIUS, 1f);
						}
	
						for (ItemStack drop : bs.getDroppedStacks(ctx)) {
							try {
								ShatteringLogic.isShattering = asplodified && (shatterBoostTime > 0 || world.random.nextInt(5) == 0);
								Block.dropStack(world, ib.pos, drop);
							} finally {
								ShatteringLogic.isShattering = false;
							}
						}
						
						world.setBlockState(ib.pos, Blocks.AIR.getDefaultState(), 3);
					}
				} else {
					known.add(ib.pos);
					var bs = world.getBlockState(ib.pos);
					if (world instanceof ServerWorld sw && !isAccessory(bs, infesting)) {
						double cX = ib.pos.getX()+0.5;
						double cY = ib.pos.getY()+0.5;
						double cZ = ib.pos.getZ()+0.5;
						if (world.random.nextInt(8) == 0) {
							sw.spawnParticles(ParticleTypes.SMOKE,
									cX, cY, cZ,
									2,
									0.2, 0.2, 0.2,
									0.025
								);
						}
						if (ib.timer == 20) {
							sw.playSound(null, cX, cY, cZ, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.BLOCKS, 0.8f, ThreadLocalRandom.current().nextFloat(1.2f, 1.6f));
						}
					}
				}
			}
		}
		if (cooldown-- <= 0) {
			cooldown = SCAN_TIME;
			if (infesting == null) {
				var front = getPos().offset(getCachedState().get(ScreeperNestBlock.FACING));
				var bs = world.getBlockState(front);
				if (canInfest(bs)) {
					infesting = bs;
					int time = DETONATE_TIME;
					infested.add(new InfestedBlock(front, false, time, ACCESSORY_DEPTH));
					markDirty();
				}
			} else {
				if (infested.isEmpty()) {
					infesting = null;
					cooldown *= 3;
				} else {
					var mut = new BlockPos.Mutable();
					int start = 0;
					if (anchor != null) {
						int idx = infested.indexOf(anchor);
						if (idx == -1) {
							start = 0;
						} else {
							start = idx;
						}
						anchor = null;
					}
					boolean skipping = anchorScanProgress > 0;
					int scanned = 0;
					glass: for (int i = start; i < infested.size(); i++) {
						var ib = infested.get(i);
						if (ib.outOfRange) continue;
						int r = 2;
						int progress = 0;
						for (int x = -r; x <= r; x++) {
							for (int y = -r; y <= r; y++) {
								for (int z = -r; z <= r; z++) {
									if (skipping && anchorScanProgress > 0) {
										anchorScanProgress--;
										continue;
									}
									mut.set(ib.pos).move(x, y, z);
									if (skipping) {
									}
									skipping = false;
									progress++;
									int dist = mut.getManhattanDistance(ib.pos);
									if (dist == 0 || dist > 3) continue;
									if (known.contains(mut)) continue;
									if (scanned > MAX_SCANNED_PER_TICK) {
										anchor = ib;
										anchorScanProgress = progress-1;
										cooldown = 1;
										break glass;
									}
									scanned++;
									var bs = world.getBlockState(mut);
									boolean acc = ib.accessoryDepth > 0 && isAccessory(bs, infesting);
									if (!acc && dist > 2) continue;
									if (ib.accessoryDepth < ACCESSORY_DEPTH-3 && !acc) continue;
									if (matches(bs, infesting) && (canInfest(bs) || acc)) {
										if (bs.getBlock().getBlastResistance() > 4) {
											digBoostTime -= 100;
										}
										var p = mut.toImmutable();
										known.add(p);
										int addn = world.random.nextInt(15);
										addn += SCAN_TIME*dist;
										mut.setY(pos.getY());
										boolean outOfRange = mut.getManhattanDistance(pos) > 8
												|| Math.abs(p.getY()-pos.getY()) > 32;
										int time = DETONATE_TIME+addn;
										int depth = ib.accessoryDepth;
										if (isAccessory(bs, infesting)) {
											time = ib.timer+dist;
											depth -= dist;
										}
										queue.add(new InfestedBlock(p, outOfRange, time, depth));
										if (world instanceof ServerWorld sw && !isAccessory(bs, infesting)) {
											world.playSound(null, p, SoundEvents.ENTITY_SILVERFISH_STEP, SoundCategory.BLOCKS, 0.2f, 1.5f);
											sw.spawnParticles(ParticleTypes.LARGE_SMOKE,
													p.getX()+0.5,
													p.getY()+1,
													p.getZ()+0.5,
													3,
													0.1, 0, 0.1,
													0.05
												);
										}
									}
								}
							}
						}
					}
					infested.addAll(queue);
					queue.clear();
				}
			}
		}
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("AccDepth", ACCESSORY_DEPTH+1);
		NbtCompound inf = new NbtCompound();
		for (var ib : infested) {
			var p = ib.pos;
			short s = (short)ib.timer;
			if (ib.outOfRange) s *= -1;
			s *= ACCESSORY_DEPTH;
			s += ib.accessoryDepth;
			inf.putShort(p.getX()+","+p.getY()+","+p.getZ(), s);
		}
		nbt.put("Cur", inf);
		if (infesting != null) nbt.put("State", NbtHelper.fromBlockState(infesting));
		nbt.putShort("SpeedTime", (short)(speedBoostTime&0xFFFF));
		nbt.putShort("DigTime", (short)(digBoostTime&0xFFFF));
		nbt.putShort("ShatterTime", (short)(shatterBoostTime&0xFFFF));
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		int accDepth = nbt.getInt("AccDepth");
		NbtCompound inf = nbt.getCompound("Cur");
		infested.clear();
		for (String k : inf.getKeys()) {
			String[] s = k.split(",", 3);
			if (s.length != 3) continue;
			int x, y, z;
			try {
				x = Integer.parseInt(s[0]);
				y = Integer.parseInt(s[1]);
				z = Integer.parseInt(s[2]);
			} catch (NumberFormatException e) {
				continue;
			}
			int v = inf.getInt(k);
			boolean outOfRange = v < 0;
			if (outOfRange) v *= -1;
			infested.add(new InfestedBlock(new BlockPos(x, y, z), outOfRange, v/accDepth, v%accDepth));
		}
		if (nbt.contains("State")) {
			infesting = NbtHelper.toBlockState(this.getWorld().filteredLookup(RegistryKeys.BLOCK), nbt.getCompound("State"));
		} else {
			infesting = null;
		}
		speedBoostTime = nbt.getShort("SpeedTime")&0xFFFF;
		digBoostTime = nbt.getShort("DigTime")&0xFFFF;
		shatterBoostTime = nbt.getShort("ShatterTime")&0xFFFF;
	}
	
	public static boolean matches(BlockState a, BlockState b) {
		if (a == b) return true;
		if (isAccessory(a, b)) return true;
		if (a.isIn(YTags.Block.SCREEPER_NEST_LENIENT)) return a.getBlock() == b.getBlock();
		return false;
	}
	
	public static boolean isAccessory(BlockState bs, BlockState infesting) {
		return infesting.getBlock() != bs.getBlock() && !infesting.isIn(YTags.Block.SCREEPER_NEST_ACCESSORY) && bs.isIn(YTags.Block.SCREEPER_NEST_ACCESSORY);
	}
	
	public boolean canInfest(BlockState bs) {
		if (bs.isAir() || !bs.isOpaque()) return false;
		return bs.getBlock().getBlastResistance() < (digBoostTime > 0 ? 6.5f : 4);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		boolean didSomething = true;
		final int MIN = 60*20;
		if (stack.isOf(Items.GUNPOWDER)) {
			digBoostTime = stack.getCount()*10*MIN;
		} else if (stack.isOf(Items.FLINT)) {
			shatterBoostTime = stack.getCount()*5*MIN;
		} else if (stack.isOf(YItems.DELICACE)) {
			speedBoostTime = stack.getCount()*2*MIN;
		} else {
			didSomething = false;
		}
		if (didSomething) {
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1, 1.4f);
			if (world instanceof ServerWorld sw) {
				sw.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 8, 0.2, 0, 0.2, 0);
			}
		}
	}
	
	@Override
	public int getMaxCountPerStack() {
		return 1;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return new int[1];
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir) {
		if (stack.isOf(Items.GUNPOWDER)) return digBoostTime <= 0;
		if (stack.isOf(Items.FLINT)) return shatterBoostTime <= 0;
		if (stack.isOf(YItems.DELICACE)) return speedBoostTime <= 0;
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

}
