package com.unascribed.yttr.world;

import java.util.Arrays;
import java.util.List;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlocks;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.structure.Structure;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

public class ScorchedGenerator {

	public static void populate(long worldSeed, ChunkRegion region, StructureAccessor accessor) {
		if (!YConfig.WorldGen.scorched) return;
		if (region.toServerWorld().getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			BlockPos.Mutable bp = new BlockPos.Mutable(0, 0, 0);
			Chunk chunk = region.getChunk(region.getCenterPos().x, region.getCenterPos().z);
			ChunkRandom rand = new ChunkRandom(new Xoroshiro128PlusPlusRandom(worldSeed));
			OctaveSimplexNoiseSampler fireNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 10));
			OctaveSimplexNoiseSampler terminusBNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 3, 6));
			OctaveSimplexNoiseSampler terminusTNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 4));
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					int bX = (chunk.getPos().getStartX()+x);
					int bZ = (chunk.getPos().getStartZ()+z);
					double bh = terminusBNoise.sample(bX/100D, bZ/100D, true)*12;
					if (bh > 0) {
						double th = terminusTNoise.sample(bX/200D, bZ/200D, true)*6;
						int lastYH = 0;
						int by = (int)(220-bh);
						for (int y = by; y < 220+th; y++) {
							bp.set(x, y, z);
							chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
							lastYH = y;
						}
						if (lastYH > 0) {
							boolean flameoMyGoodHotman = false;
							double d = rand.nextDouble();
							double noise = (fireNoise.sample(bX/30D, bZ/30D, false)+0.2);
							if (d < noise) {
								bp.set(bX, lastYH, bZ);
								if (!region.getBlockState(bp).isAir()) {
									bp.set(bX, lastYH+1, bZ);
									region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
									flameoMyGoodHotman = true;
								}
							}
							if (!flameoMyGoodHotman && d-0.2 > noise) {
								bp.set(bX, lastYH-1, bZ);
								if (!region.getBlockState(bp).isAir()) {
									bp.set(bX, lastYH, bZ);
									region.setBlockState(bp, YBlocks.ASH.getDefaultState(), 3);
								}
							}
						}
					}
				}
			}
			rand.setPopulationSeed(31*worldSeed, chunk.getPos().getStartX(), chunk.getPos().getStartZ());
			if (accessor.shouldGenerateStructures()) {
				if (rand.nextInt(40) == 0) {
					generateTerminusHouse(region, bp, chunk, rand);
				}
				if (rand.nextInt(30) == 0) {
					generateTerminusTotems(region, bp, chunk, rand);
				}
			}
		}
	}

	private static void generateTerminusTotems(ChunkRegion region, BlockPos.Mutable bp, Chunk chunk, ChunkRandom rand) {
		var opt = region.toServerWorld().getStructureManager().getStructure(Yttr.id("terminus_totems"));
		if (opt.isPresent()) {
			Structure s = opt.get();
			StructurePlacementData spd = new StructurePlacementData();
			int y = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE).get(8, 8)-1+rand.nextInt(3);
			if (y < 200) return;
			BlockPos origin = new BlockPos(
					chunk.getPos().getStartX(),
					y,
					chunk.getPos().getStartZ()
			);
			Vec3i size = s.getSize();
			for (BlockPos bpp : BlockPos.iterate(origin.add(0, -1, 0), origin.add(size.getX(), -1, size.getZ()))) {
				BlockState bs = region.getBlockState(bpp);
				if (bs.isAir()) {
					return;
				}
			}
			for (BlockPos bpp : BlockPos.iterate(origin.add(-1, 0, -1), origin.add(size.getX(), 1, size.getZ()))) {
				BlockState bs = region.getBlockState(bpp);
				if (bs.isOf(Blocks.FIRE)) {
					region.setBlockState(bpp, Blocks.AIR.getDefaultState(), 3);
				}
			}
			s.place(region, origin, origin.add(size.getX()/2, 0, size.getZ()/2), spd, rand, 3);
			for (StructureBlockInfo info : s.getInfosForBlock(origin, spd, Blocks.STRUCTURE_BLOCK, true)) {
				if (info != null && info.state.get(StructureBlock.MODE) == StructureBlockMode.DATA) {
					if (info.nbt != null) {
						BlockState cap = null;
						int height = 0;
						if ("yttr:totem".equals(info.nbt.getString("metadata"))) {
							height = rand.nextInt(4);
							cap = YBlocks.POLISHED_SCORCHED_OBSIDIAN_CAPSTONE.getDefaultState();
						} else if ("yttr:holster".equals(info.nbt.getString("metadata"))) {
							height = rand.nextInt(4)+4;
							cap = YBlocks.POLISHED_SCORCHED_OBSIDIAN_HOLSTER.getDefaultState();
						}
						if (cap != null) {
							bp.set(info.pos);
							for (int i = 0; i < height; i++) {
								region.setBlockState(bp, YBlocks.POLISHED_SCORCHED_OBSIDIAN.getDefaultState(), 3);
								bp.move(Direction.UP);
							}
							region.setBlockState(bp, cap, 3);
						}
					}
				}
			}
		}
	}

	private static void generateTerminusHouse(ChunkRegion region, BlockPos.Mutable bp, Chunk chunk, ChunkRandom rand) {
		var opt = region.toServerWorld().getStructureManager().getStructure(Yttr.id("terminus_house"));
		if (opt.isPresent()) {
			Structure s = opt.get();
			BlockRotation rot = BlockRotation.random(rand);
			List<BlockPos> chains = Lists.newArrayList();
			StructurePlacementData spd = new StructurePlacementData();
			spd.setRotation(rot);
			BlockPos origin = new BlockPos(
					chunk.getPos().getStartX(),
					(190+(rand.nextInt(30)))-s.getSize().getY(),
					chunk.getPos().getStartZ()
			);
			boolean success = true;
			for (int i = 0; i < 3; i++) {
				success = true;
				for (BlockPos bpp : BlockPos.iterate(origin, origin.add(s.getRotatedSize(rot)))) {
					BlockState bs = region.getBlockState(bpp);
					if (!bs.isAir()) {
						if (!bs.isOf(YBlocks.NETHERTUFF)) {
							// we probably ran into another already-generated house, so just bail entirely to avoid weird generation
							return;
						}
						success = false;
						origin = origin.down(bpp.getY()-origin.getY());
						break;
					}
				}
				if (success) break;
			}
			if (!success) return;
			boolean foundAllAnchors = true;
			for (StructureBlockInfo info : s.getInfosForBlock(origin, spd, Blocks.STRUCTURE_BLOCK, true)) {
				if (info != null && info.state.get(StructureBlock.MODE) == StructureBlockMode.DATA) {
					if (info.nbt != null && "yttr:chain".equals(info.nbt.getString("metadata"))) {
						bp.set(info.pos);
						boolean foundAnchor = false;
						for (int i = 0; i < 10; i++) {
							bp.move(Direction.UP);
							if (!region.getBlockState(bp).isAir()) {
								foundAnchor = true;
								break;
							}
						}
						if (!foundAnchor) {
							foundAllAnchors = false;
							break;
						}
						chains.add(info.pos.toImmutable());
					}
				}
			}
			if (foundAllAnchors) {
				boolean warped = rand.nextBoolean();
				spd.addProcessor(new NetherWoodSwapStructureProcessor(warped));
				spd.addProcessor(new LootTableFromPaperStructureProcessor());
				s.place(region, origin, origin, spd, rand, 3);
				for (BlockPos chain : chains) {
					bp.set(chain);
					for (int i = 0; i < 10; i++) {
						if (region.getBlockState(bp).isAir() || region.getBlockState(bp).isOf(Blocks.STRUCTURE_BLOCK)) {
							region.setBlockState(bp, Blocks.CHAIN.getDefaultState(), 3);
						} else {
							break;
						}
						bp.move(Direction.UP);
					}
				}
				bp.set(origin);
				bp.move(Direction.DOWN);
				for (int i = 0; i < 100; i++) {
					if (!region.getBlockState(bp).isAir() && !region.getBlockState(bp).isIn(BlockTags.FIRE)) {
						break;
					}
					bp.move(Direction.DOWN);
				}
				region.setBlockState(bp, Blocks.SHROOMLIGHT.getDefaultState(), 3);
				bp.move(Direction.UP);
				region.setBlockState(bp, (warped ? Blocks.WARPED_PRESSURE_PLATE : Blocks.CRIMSON_PRESSURE_PLATE).getDefaultState(), 3);
				bp.move(Direction.DOWN, 2);
				region.setBlockState(bp, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, Direction.UP), 3);
				BlockEntity be = region.getBlockEntity(bp);
				if (be instanceof DispenserBlockEntity) {
					ItemStack potion = new ItemStack(Items.SPLASH_POTION);
					potion.setCustomName(new TranslatableText("item.yttr.levitation_splash_potion").setStyle(Style.EMPTY.withItalic(false)));
					PotionUtil.setCustomPotionEffects(potion, Arrays.asList(new StatusEffectInstance(StatusEffects.LEVITATION, 25*20, 5)));
					potion.getNbt().putInt("CustomPotionColor", StatusEffects.LEVITATION.getColor());
					((DispenserBlockEntity)be).setStack(4, potion);
				}
			}
		}
	}

	public static void buildSurface(ChunkRegion region, Chunk chunk) {
		if (region.toServerWorld().getRegistryKey().getValue().equals(DimensionType.THE_NETHER_ID)) {
			BlockPos.Mutable bp = new BlockPos.Mutable(0, 0, 0);
			if (YConfig.WorldGen.coreLava && chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						bp.set(x, 0, z);
						chunk.setBlockState(bp, Blocks.BARRIER.getDefaultState(), false);
						for (int y = 1; y < 4; y++) {
							bp.set(x, y, z);
							chunk.setBlockState(bp, YBlocks.CORE_LAVA.getDefaultState(), false);
						}
						bp.set(x, 4, z);
						chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
						bp.set(x, 5, z);
						chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
					}
				}
			}
			bp.setY(127);
			if (YConfig.WorldGen.scorched && chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
				ChunkRandom rand = new ChunkRandom(new Xoroshiro128PlusPlusRandom(region.getSeed()));
				OctaveSimplexNoiseSampler noise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(1, 4, 8));
				OctaveSimplexNoiseSampler fireNoise = new OctaveSimplexNoiseSampler(rand, Arrays.asList(0, 2, 10));
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = 120; y < 128; y++) {
							bp.set(x, y, z);
							if (chunk.getBlockState(bp).isOf(Blocks.BEDROCK)) {
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
							}
						}
						int bX = (chunk.getPos().getStartX()+x);
						int bZ = (chunk.getPos().getStartZ()+z);
						double height = (noise.sample(bX/200D, bZ/200D, true)+0.2)*6;
						int lastY = 128;
						if (height < 0) {
							for (int y = 128; y > 128+(height*8); y--) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, Blocks.AIR.getDefaultState(), false);
								lastY = y-1;
							}
						} else {
							if (height > 3) {
								height = 3+((height-3)*10);
							}
							for (int y = 128; y < 128+height; y++) {
								bp.set(x, y, z);
								chunk.setBlockState(bp, YBlocks.NETHERTUFF.getDefaultState(), false);
								lastY = y;
							}
						}
						if (rand.nextDouble()*2 < (fireNoise.sample(bX/20D, bZ/20D, true)-0.2)) {
							bp.set(bX, lastY, bZ);
							if (!region.getBlockState(bp).isAir()) {
								bp.set(bX, lastY+1, bZ);
								region.setBlockState(bp, Blocks.FIRE.getDefaultState(), 3);
							}
						}
					}
				}
			}
		}
	}
	
}
