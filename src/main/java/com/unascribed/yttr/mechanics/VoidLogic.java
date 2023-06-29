package com.unascribed.yttr.mechanics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YDamageTypes;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YStats;
import com.unascribed.yttr.network.MessageS2CVoidBall;
import com.unascribed.yttr.util.EquipmentSlots;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Predicates;
import com.google.common.io.MoreFiles;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Property;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VoidLogic {

	private static final DateFormat fmt = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void doVoid(PlayerEntity user, World _world, Vec3d _pos, int r) {
		// TODO this really needs some optimizing; the NBT undo files are oversized and the index
		// can't be used efficiently. not going to make a huge deal out of it right now
		if (!(_world instanceof ServerWorld)) return;
		try {
			ServerWorld world = (ServerWorld)_world;
			String dim = world.getRegistryKey().getValue().toString().replace(':', '_').replace('/', '_');
			String undoName = dim+"_"+fmt.format(new Date())+"_"+user.getGameProfile().getName()+"_"+ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
			MessageS2CVoidBall msg = new MessageS2CVoidBall((float)_pos.x, (float)_pos.y, (float)_pos.z, r+0.5f);
			for (PlayerEntity pe : world.getPlayers()) {
				msg.sendTo(pe);
			}
			world.playSound(null, _pos.x, _pos.y, _pos.z, YSounds.VOID, SoundCategory.PLAYERS, 4, 1);
			BlockPos pos = BlockPos.fromPosition(_pos);
			Path root = getUndoDirectory(world.getServer());
			Path out = root.resolve(undoName+".dat");
			MoreFiles.createParentDirectories(out);
			Path indexFile = root.resolve("index.dat");
			NbtCompound index = Files.exists(indexFile) ? NbtIo.readCompressed(indexFile.toFile()) : new NbtCompound();
			
			NbtCompound byUser = index.getCompound("ByUser");
			String id = user.getGameProfile().getId().toString();
			NbtCompound userData = byUser.getCompound(id);
			userData.putString("Username", user.getGameProfile().getName());
			NbtList userList = userData.getList("List", NbtType.STRING);
			userList.add(NbtString.of(undoName));
			userData.put("List", userList);
			byUser.put(id, userData);
			index.put("ByUser", byUser);
			
			NbtCompound byChunk = index.getCompound("ByChunk");
			ChunkPos chunkPos = new ChunkPos(pos);
			String chunkKey = chunkPos.x+" "+chunkPos.z;
			NbtList chunkList = byUser.getList(chunkKey, NbtType.COMPOUND);
			NbtCompound chunkListEntry = new NbtCompound();
			chunkListEntry.putByte("HPos", (byte)(((pos.getX()&0xF)<<4)|(pos.getZ()&0xF)));
			chunkListEntry.putShort("YPos", (short)pos.getY());
			chunkListEntry.putString("Dim", world.getRegistryKey().getValue().toString());
			chunkListEntry.putString("Name", undoName);
			chunkList.add(chunkListEntry);
			byChunk.put(chunkKey, chunkList);
			index.put("ByChunk", byChunk);
			
			NbtCompound data = new NbtCompound();
			data.putIntArray("Pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
			data.putString("Dim", world.getRegistryKey().getValue().toString());
			NbtList blocks = new NbtList();
			data.put("Blocks", blocks);
			BlockPos.Mutable bp = new BlockPos.Mutable();
			for (int y = -r; y <= r; y++) {
				for (int x = -r; x <= r; x++) {
					for (int z = -r; z <= r; z++) {
						bp.set(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						if (pos.getSquaredDistance(bp.getX(), bp.getY(), bp.getZ()) < r*r) {
							BlockState bs = world.getBlockState(bp);
							if (bs.getHardness(world, bp) < 0) continue;
							BlockEntity be = world.getBlockEntity(bp);
							NbtCompound block = new NbtCompound();
							block.putByteArray("Pos", new byte[] {(byte)x, (byte)y, (byte)z});
							block.putString("Block", Registries.BLOCK.getId(bs.getBlock()).toString());
							if (!bs.getEntries().isEmpty()) {
								NbtCompound state = new NbtCompound();
								for (Map.Entry<Property, Comparable<?>> en : (Set<Map.Entry<Property, Comparable<?>>>)(Set)(bs.getEntries().entrySet())) {
									state.putString(en.getKey().getName(), en.getKey().name(en.getValue()));
								}
								block.put("State", state);
							}
							if (be != null) {
								block.put("Entity", be.toNbt());
							}
							blocks.add(block);
						}
					}
				}
			}
			NbtIo.writeCompressed(data, out.toFile());
			NbtIo.writeCompressed(index, indexFile.toFile());
			int blocksVoidedStat = 0;
			for (int y = -r; y <= r; y++) {
				for (int x = -r; x <= r; x++) {
					for (int z = -r; z <= r; z++) {
						bp.set(pos.getX()+x, pos.getY()+y, pos.getZ()+z);
						if (pos.getSquaredDistance(bp.getX(), bp.getY(), bp.getZ()) < r*r) {
							BlockState bs = world.getBlockState(bp);
							if (bs.getHardness(world, bp) < 0) continue;
							if (!bs.isAir()) {
								blocksVoidedStat++;
							}
							world.removeBlockEntity(bp);
							world.setBlockState(bp, Blocks.VOID_AIR.getDefaultState());
						}
					}
				}
			}
			YStats.add(user, YStats.BLOCKS_VOIDED, blocksVoidedStat);
			Box box = new Box(_pos.x-r, _pos.y-r, _pos.z-r, _pos.x+r, _pos.y+r, _pos.z+r);
			for (Entity e : world.getEntitiesByClass(Entity.class, box, Predicates.alwaysTrue())) {
				double d = _pos.squaredDistanceTo(e.getPos());
				if (d < r*r) {
					float dmg = (float) ((r*r)-d);
					e.damage(e.getDamageSources().create(YDamageTypes.VOID_RIFLE, user), dmg);
					if (e instanceof LivingEntity) {
						LivingEntity le = (LivingEntity)e;
						for (EquipmentSlot es : EquipmentSlots.ARMOR) {
							le.getEquippedStack(es).damage((int)dmg, le, (blah) -> {
								le.sendEquipmentBreakStatus(es);
							});
						}
					}
				}
			}
			YLog.info("{} performed a {} radius void at {}, {}, {} in {}. Undo with /yttr:void_undo just {} or undo all voids by this player with /yttr:void_undo by {}",
					user.getGameProfile().getName(), r, pos.getX(), pos.getY(), pos.getZ(), world.getRegistryKey().getValue(), undoName, user.getGameProfile().getName());
		} catch (IOException e) {
			YLog.warn("Failed to void", e);
		}
	}

	public static Path getUndoDirectory(MinecraftServer server) {
		return server.getSavePath(WorldSavePath.ROOT).resolve("yttr_void_undo");
	}
	
}
