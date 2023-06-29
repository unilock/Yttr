package com.unascribed.yttr.world;

import java.util.List;
import java.util.Set;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YTags;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.Explosion.DestructionType;
import net.minecraft.world.gen.ChunkRandom;

public class WastelandPopulator {

	private static final int FLAGS = Block.SKIP_DROPS | Block.FORCE_STATE | Block.NOTIFY_LISTENERS;
	
	public static boolean isEligible(ServerWorld world, WorldChunk chunk) {
		if (world.getBiome(chunk.getPos().getStartPos()).getKey().map(k -> k.getValue().toString().equals("yttr:wasteland")).orElse(false)) {
			if (chunk.getBlockState(BlockPos.ORIGIN).getBlock() == YBlocks.SPECIALTY_BEDROCK) return false;
			return true;
		}
		return false;
	}
	
	public static void populate(long worldSeed, ServerWorld world, ChunkPos chunk) {
		BlockPos chunkStart = chunk.getStartPos().withY(world.getBottomY());
		if (world.getBiome(chunkStart).getKey().map(k -> k.getValue().toString().equals("yttr:wasteland")).orElse(false)) {
			if (world.getBlockState(chunkStart).getBlock() == YBlocks.SPECIALTY_BEDROCK) return;
			world.setBlockState(chunkStart, YBlocks.SPECIALTY_BEDROCK.getDefaultState(), 0, 0);
			ChunkRandom rand = new ChunkRandom(new Xoroshiro128PlusPlusRandom(worldSeed));
			rand.setPopulationSeed(worldSeed, chunk.getStartX(), chunk.getStartZ());
			BlockPos.Mutable mut = new BlockPos.Mutable();
			BlockPos.Mutable mut2 = new BlockPos.Mutable();
			BlockPos.Mutable mut3 = new BlockPos.Mutable();
			if (rand.nextInt(100) < 5) {
				// staircase to bedrock with optional strip mine
				Direction d = Direction.Type.HORIZONTAL.random(rand);
				int x = chunkStart.getX();
				int z = chunkStart.getZ();
				x += rand.nextInt(16);
				z += rand.nextInt(16);
				mut.set(x, world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z)+2, z);
				int distanceSinceTorch = 0;
				int stripMineY = -1;
				if (rand.nextInt(100) < 60) {
					stripMineY = 11+rand.nextInt(6);
				}
				for (int i = 0; i < 1000; i++) {
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					if (rand.nextInt(10) < distanceSinceTorch && !world.getBlockState(mut.offset(d.rotateYCounterclockwise())).isAir()) {
						world.setBlockState(mut, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d.rotateYClockwise()), FLAGS, 0);
						distanceSinceTorch = 0;
					}
					mut.move(Direction.DOWN);
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					mut.move(Direction.DOWN);
					if (!didYouKnowWeHaveVeinMiner(world, mut, rand)) break;
					mut.move(Direction.DOWN);
					if (world.getBlockState(mut).isAir()) {
						world.setBlockState(mut, mut.getY() > 50 ? YBlocks.RUINED_COBBLESTONE.getDefaultState() : Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
					}
					mut.move(Direction.UP);
					if (mut.getY() == stripMineY) {
						mut2.set(mut);
						int distanceSinceTorch2 = 0;
						Direction d2 = rand.nextBoolean() ? d.rotateYClockwise() : d.rotateYCounterclockwise();
						for (int j = 0; j < 40+rand.nextInt(80); j++) {
							mut2.move(d2);
							if (!didYouKnowWeHaveVeinMiner(world, mut2, rand)) break;
							mut2.move(Direction.DOWN);
							if (world.getBlockState(mut2).isAir()) {
								world.setBlockState(mut2, Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
							}
							mut2.move(Direction.UP);
							mut2.move(Direction.UP);
							if (!didYouKnowWeHaveVeinMiner(world, mut2, rand)) break;
							if (rand.nextInt(10) < distanceSinceTorch2 && !world.getBlockState(mut2.offset(d2.rotateYCounterclockwise())).isAir()) {
								world.setBlockState(mut2, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d2.rotateYClockwise()), FLAGS, 0);
								distanceSinceTorch2 = 0;
							}
							mut2.move(Direction.DOWN);
							if (j % 3 == 0) {
								Direction d3 = d;
								for (int p = 0; p < 2; p++) {
									int distanceSinceTorch3 = 0;
									if (p == 1) d3 = d3.getOpposite();
									mut3.set(mut2);
									for (int k = 0; k < rand.nextInt(50)+20; k++) {
										mut3.move(d3);
										if (!didYouKnowWeHaveVeinMiner(world, mut3, rand)) break;
										mut3.move(Direction.DOWN);
										if (world.getBlockState(mut3).isAir()) {
											world.setBlockState(mut3, Blocks.COBBLESTONE.getDefaultState(), FLAGS, 0);
										}
										mut3.move(Direction.UP);
										mut3.move(Direction.UP);
										if (!didYouKnowWeHaveVeinMiner(world, mut3, rand)) break;
										if (rand.nextInt(10) < distanceSinceTorch3 && !world.getBlockState(mut3.offset(d3.rotateYCounterclockwise())).isAir()) {
											world.setBlockState(mut3, YBlocks.RUINED_WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, d3.rotateYClockwise()), FLAGS, 0);
											distanceSinceTorch3 = 0;
										}
										mut3.move(Direction.DOWN);
										distanceSinceTorch3++;
									}
								}
							}
						}
					}
					mut.move(Direction.UP);
					mut.move(d);
					distanceSinceTorch++;
				}
			}
			if (rand.nextInt(200) == 0) {
				mut.set(chunkStart);
				mut.move(rand.nextInt(16), 0, rand.nextInt(16));
				mut.setY(world.getTopY(Heightmap.Type.WORLD_SURFACE, mut.getX(), mut.getZ()));
				int w = rand.nextInt(20)+3;
				int h = rand.nextInt(10)+2;
				int d = rand.nextInt(20)+3;
				for (int y = -4; y <= h+1; y++) {
					for (int x = -2; x <= w+1; x++) {
						for (int z = -2; z <= d+1; z++) {
							mut2.set(mut).move(x, -y, z);
							if (x < 0 || x >= w
									|| y >= h
									|| z < 0 || z >= d) {
								BlockState bs = world.getBlockState(mut2);
								if (!(bs.isIn(BlockTags.BASE_STONE_OVERWORLD) || bs.isIn(YTags.Block.ORES) || bs.isOf(Blocks.GRAVEL) || bs.isOf(Blocks.DIRT))) continue;
								if (x == -2 || z == -2 || y == h+1 || x == w+1 || z == d+1) {
									if (!rand.nextBoolean()) continue;
								}
								world.setBlockState(mut2, bs.isOf(Blocks.DIRT) ? YBlocks.WASTELAND_DIRT.getDefaultState() : YBlocks.WASTELAND_STONE.getDefaultState(), FLAGS, 0);
							} else {
								world.setBlockState(mut2, Blocks.AIR.getDefaultState(), FLAGS, 0);
							}
						}
					}
				}
			}
			if (rand.nextInt(20) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/twilight_portal", -2, true, false);
			}
			if (rand.nextInt(400) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/laundromat", 0, true, true);
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/sulfur_goo_farm", 1, true, true);
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				Direction d = Direction.Type.HORIZONTAL.random(rand);
				for (int i = 0; i < 1+rand.nextInt(8); i++) {
					if (!tryPlaceSchematic(rand, world, mut, "yttr:ruined/coke_oven", 0, false, true)) break;
					mut.move(d, 4);
					mut.setY(world.getTopY(Heightmap.Type.WORLD_SURFACE, mut.getX()-1, mut.getZ()-1));
				}
			}
			if (rand.nextInt(100) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/blast_furnace", 0, false, true);
			}
			if (rand.nextInt(200) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/quarry", 0, false, true);
			}
			if (rand.nextInt(200) == 0) {
				mut.set(chunkStart);
				if (rand.nextInt(4) == 0) {
					mut.setY(-1);
					tryPlaceSchematic(rand, world, mut, "yttr:ruined/9x9_rare", -2, true, true);
				} else {
					mut.setY(-1);
					tryPlaceSchematic(rand, world, mut, "yttr:ruined/9x9", -1, true, true);
				}
			}
			if (rand.nextInt(300) == 0) {
				mut.set(chunkStart);
				mut.setY(-1);
				tryPlaceSchematic(rand, world, mut, "yttr:ruined/arboretum", -1, true, true);
			}
			while (rand.nextInt(3) == 0) {
				mut.set(chunkStart);
				mut.move(rand.nextInt(16), 0, rand.nextInt(16));
				mut.setY(world.getTopY(Heightmap.Type.WORLD_SURFACE, mut.getX(), mut.getZ()));
				BlockState bs = world.getBlockState(mut);
				if ((bs.isAir() || bs.isOf(YBlocks.WASTELAND_DIRT)) && world.getBlockState(mut.down()).isOf(YBlocks.WASTELAND_DIRT)) {
					world.setBlockState(mut, YBlocks.RUINED_TORCH.getDefaultState(), FLAGS, 0);
				}
			}
			if (rand.nextInt(40) == 0) {
				double x = chunkStart.getX()+(rand.nextDouble()*16);
				double z = chunkStart.getZ()+(rand.nextDouble()*16);
				double y = (world.getTopY(Heightmap.Type.WORLD_SURFACE, (int)x, (int)z))+rand.nextGaussian();
				explode(world, x, y, z, 4+(rand.nextFloat()*3));
			}
			if (rand.nextInt(500) == 0) {
				double x = chunkStart.getX()+(rand.nextDouble()*16);
				double z = chunkStart.getZ()+(rand.nextDouble()*16);
				double y = (world.getTopY(Heightmap.Type.WORLD_SURFACE, (int)x, (int)z))+rand.nextGaussian();
				for (int i = 0; i < 30+(rand.nextInt(50)); i++) {
					explode(world, x+(rand.nextGaussian()*20), y, z+(rand.nextGaussian()*20), 6);
				}
			}
		}
	}
	
	private static void explode(ServerWorld world, double x, double y, double z, float power) {
		Explosion e = new Explosion(world, null, world.getDamageSources().outOfWorld(), null, x, y, z, power, false, DestructionType.DESTROY);
		e.collectBlocksAndDamageEntities();
		for (BlockPos bp : e.getAffectedBlocks()) {
			world.setBlockState(bp, Blocks.AIR.getDefaultState(), FLAGS, 0);
		}
		for (BlockPos bp : e.getAffectedBlocks()) {
			for (Direction d : Direction.values()) {
				BlockPos o = bp.offset(d);
				BlockState bs = world.getBlockState(o);
				if (!(bs.isIn(BlockTags.BASE_STONE_OVERWORLD) || bs.isIn(YTags.Block.ORES) || bs.isOf(Blocks.GRAVEL) || bs.isOf(Blocks.DIRT))) continue;
				world.setBlockState(o, bs.isOf(Blocks.DIRT) ? YBlocks.WASTELAND_DIRT.getDefaultState() : YBlocks.WASTELAND_STONE.getDefaultState(), FLAGS, 0);
			}
		}
	}

	public static boolean didYouKnowWeHaveVeinMiner(WorldAccess world, BlockPos pos, RandomGenerator rand) {
		if (pos.getY() <= 0) return false;
		Set<BlockPos> seen = Sets.newHashSet();
		Set<BlockPos> scan = Sets.newHashSet();
		Set<BlockPos> nextScan = Sets.newHashSet();
		Direction[] directions = Direction.values();
		int i = 0;
		scan.clear();
		nextScan.clear();
		BlockPos start = pos.toImmutable();
		scan.add(start);
		boolean hitUnbreakable = false;
		while (!scan.isEmpty()) {
			if (i++ > 32) break;
			for (BlockPos bp : scan) {
				BlockState bs = world.getBlockState(bp);
				if (bs.isIn(YTags.Block.ORES) || bp.equals(start)) {
					for (Direction d : directions) {
						BlockPos c = bp.offset(d);
						BlockState bs2 = world.getBlockState(c);
						if ((bs2.isIn(YTags.Block.ORES) || (d == Direction.UP && bs2.getBlock() instanceof FallingBlock)) && seen.add(c)) {
							if (bs2.isIn(YTags.Block.LESSER_ORES) && rand.nextInt(40) < seen.size()+2) {
								// eh, we're bored of mining this. let's get back to digging the mine
								break;
							}
							nextScan.add(c);
						}
					}
				} else if (bs.getBlock() instanceof FallingBlock) {
					BlockPos c = bp.up();
					BlockState bs2 = world.getBlockState(c);
					if ((bs2.isIn(YTags.Block.ORES) || bs2.getBlock() instanceof FallingBlock) && seen.add(c)) {
						nextScan.add(c);
					}
				}
				if (bs.isIn(YTags.Block.ORES) || bs.isOf(YBlocks.WASTELAND_DIRT) || bs.isIn(BlockTags.BASE_STONE_OVERWORLD) || bs.getBlock() instanceof FallingBlock) {
					world.setBlockState(bp, Blocks.AIR.getDefaultState(), FLAGS, 0);
				} else if (!bs.isAir() && !bs.materialReplaceable() && !bs.isOf(YBlocks.RUINED_WALL_TORCH)) {
					hitUnbreakable = true;
				}
			}
			scan.clear();
			scan.addAll(nextScan);
			nextScan.clear();
		}
		return !hitUnbreakable;
	}
	
	private static boolean tryPlaceSchematic(ChunkRandom rand, ServerWorld world, BlockPos pos, String id, int yOffset, boolean eatDirt, boolean fill) {
		var opt = world.getStructureTemplateManager().getStructure(new Identifier(id));
		if (!opt.isPresent()) return false;
		var s = opt.get();
		BlockRotation rot = BlockRotation.random(rand);
		StructurePlacementData spd = new StructurePlacementData();
		spd.setRotation(rot);
		spd.setUpdateNeighbors(false);
		BlockPos size = new BlockPos(s.getRotatedSize(rot));
		BlockPos origin = pos.add(-size.getX()/2, 0, -size.getZ()/2);
		if (origin.getY() == -1) {
			origin = new BlockPos(origin.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE, origin.getX(), origin.getZ()), origin.getZ());
		}
		origin = origin.up(yOffset);
		int originY = origin.getY();
		for (BlockPos bpp : BlockPos.iterate(origin, origin.add(s.getRotatedSize(rot)))) {
			BlockState bs = world.getBlockState(bpp);
			if (!bs.isAir() && !bs.materialReplaceable() && (!eatDirt || !bs.isOf(YBlocks.WASTELAND_DIRT))) {
				return false;
			}
		}
		List<BlockPos> fillIn = Lists.newArrayList();
		if (fill) {
			spd.addProcessor(SimpleStructureProcessor.of((block) -> {
				if (block.pos().getY() == originY && block.state().isSideSolid(world, block.pos(), Direction.DOWN, SideShapeType.FULL)) {
					if (block.nbt() == null || !"yttr:quarry_hole".equals(block.nbt().getString("metadata"))) {
						fillIn.add(block.pos());
					}
				}
				return block;
			}));
		}
		s.place(world, origin, origin, spd, rand, 3);
		for (StructureBlockInfo info : s.getInfosForBlock(origin, spd, Blocks.STRUCTURE_BLOCK, true)) {
			if (info != null && info.state().get(StructureBlock.MODE) == StructureBlockMode.DATA) {
				if (info.nbt() != null) {
					if ("yttr:quarry_hole".equals(info.nbt().getString("metadata"))) {
						BlockPos.Mutable bp = info.pos().mutableCopy();
						for (int y = info.pos().getY(); y >= world.getBottomY(); y--) {
							bp.setY(y);
							BlockState bs = world.getBlockState(bp);
							if (bs.isOf(Blocks.BEDROCK)) break;
							world.setBlockState(bp, Blocks.AIR.getDefaultState());
						}
					} else if ("yttr:maybe_tree".equals(info.nbt().getString("metadata"))) {
						BlockPos.Mutable bp = info.pos().mutableCopy();
						if (rand.nextInt(5) == 0) {
							for (int i = 0; i < rand.nextInt(7)+1; i++) {
								world.setBlockState(bp, YBlocks.WASTELAND_LOG.getDefaultState());
								bp.move(Direction.UP);
							}
						} else if (rand.nextBoolean()) {
							world.setBlockState(bp, YBlocks.WASTELAND_GRASS.getDefaultState());
						} else {
							world.setBlockState(bp, Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}
		for (BlockPos b : fillIn) {
			BlockPos.Mutable scan = b.mutableCopy();
			scan.move(Direction.DOWN);
			while (world.getBlockState(scan).isAir() || world.getBlockState(scan).materialReplaceable()) {
				world.setBlockState(scan, YBlocks.WASTELAND_DIRT.getDefaultState(), FLAGS, 0);
				scan.move(Direction.DOWN);
			}
		}
		return true;
	}
	
}
