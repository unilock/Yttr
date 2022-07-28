package com.unascribed.yttr.content.block.decor;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mixinsupport.YttrWorld;

import com.google.common.base.MoreObjects;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.unascribed.yttr.content.block.decor.BloqueBlock.*;

public class BloqueBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

	public enum AdjType {
		NONE,
		CULL,
		CULL_EDGE,
		MERGE_INTERNAL,
		MERGE_EXTERNAL,
		MERGE_EXTERNAL_CULL,
		;
		public boolean merged() {
			return this == MERGE_INTERNAL || this == MERGE_EXTERNAL || this == MERGE_EXTERNAL_CULL;
		}
		public boolean skipFace() {
			return this == CULL || this == MERGE_INTERNAL || this == MERGE_EXTERNAL_CULL;
		}
		public boolean cullable() {
			return this == CULL_EDGE || this == MERGE_EXTERNAL;
		}
		public AdjType culled() {
			switch (this) {
				case CULL_EDGE: return CULL;
				case MERGE_EXTERNAL: return MERGE_EXTERNAL_CULL;
				default: return this;
			}
		}
	}
	
	public record RenderData(DyeColor[] colors, Adjacency[] adjacency, boolean welded, boolean doubleWelded) {}
	public record Adjacency(AdjType downType, AdjType upType, AdjType northType, AdjType southType, AdjType westType, AdjType eastType) {
		public static final Adjacency NONE = new Adjacency(AdjType.NONE, AdjType.NONE, AdjType.NONE, AdjType.NONE, AdjType.NONE, AdjType.NONE);
		public Adjacency(AdjType[] vals) {
			this(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
		}
		public AdjType getType(Direction d) {
			switch (d) {
				case DOWN: return downType;
				case UP: return upType;
				case NORTH: return northType;
				case SOUTH: return southType;
				case WEST: return westType;
				case EAST: return eastType;
				default: throw new AssertionError(d);
			}
		}
		public boolean merged(Direction d) {
			return getType(d).merged();
		}
		public boolean skipFace(Direction d) {
			return getType(d).skipFace();
		}
		public boolean cullable(Direction d) {
			return getType(d).cullable();
		}
		public boolean down() { return downType.merged(); }
		public boolean up() { return upType.merged(); }
		public boolean north() { return northType.merged(); }
		public boolean south() { return southType.merged(); }
		public boolean west() { return westType.merged(); }
		public boolean east() { return eastType.merged(); }
	}
	
	private final DyeColor[] colors = new DyeColor[SLOTS];
	private final Adjacency[] adjacency = new Adjacency[SLOTS];
	private boolean welded, doubleWelded;
	
	public BloqueBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.BLOQUE, pos, state);
	}
	
	public void set(int slot, @Nullable DyeColor color) {
		if (slot < 0 || slot >= colors.length) {
			return;
		}
		colors[slot] = color;
		if (!isWelded()) {
			world.addSyncedBlockEvent(getPos(), getCachedState().getBlock(), slot, color == null ? -1 : color.getId());
		}
		markDirty();
	}
	
	public @Nullable DyeColor get(int slot) {
		if (slot < 0 || slot >= colors.length) {
			return null;
		}
		return colors[slot];
	}

	public void weld() {
		if (!isWeldable()) return;
		if (this.welded) {
			this.doubleWelded = true;
			computeAdjacency(true, true);
		} else {
			this.welded = true;
			computeAdjacency(true, false);
		}
		markDirty();
		Yttr.sync(this);
	}
	
	public void unweld() {
		if (!isWelded()) return;
		this.welded = this.doubleWelded = false;
		Arrays.fill(adjacency, null);
		markDirty();
		Yttr.sync(this);
	}
	
	private void computeAdjacency(boolean weld, boolean mixColors) {
		for (int y = 0; y < YSIZE; y++) {
			for (int x = 0; x < XSIZE; x++) {
				for (int z = 0; z < ZSIZE; z++) {
					int slot = getSlot(x, y, z);
					DyeColor cur = get(slot);
					if (cur == null) {
						adjacency[slot] = Adjacency.NONE;
						continue;
					}
					AdjType[] b = new AdjType[6];
					Arrays.fill(b, AdjType.NONE);
					for (Direction d : Direction.values()) {
						int ox = x+d.getOffsetX();
						int oy = y+d.getOffsetY();
						int oz = z+d.getOffsetZ();
						DyeColor other = getMulti(ox, oy, oz);
						boolean external = false;
						if (ox < 0 || oy < 0 || oz < 0 ||
								ox >= XSIZE || oy >= YSIZE || oz >= ZSIZE) {
							external = true;
						}
						boolean match = weld && (mixColors ? other != null : other == cur);
						if (match) {
							b[d.ordinal()] = external ? AdjType.MERGE_EXTERNAL : AdjType.MERGE_INTERNAL;
						} else if (external) {
							b[d.ordinal()] = AdjType.CULL_EDGE;
						} else if (other != null) {
							b[d.ordinal()] = AdjType.CULL;
						}
					}
					adjacency[slot] = new Adjacency(b);
				}
			}
		}
	}

	public boolean isWelded() {
		return welded;
	}

	public boolean isWeldable() {
		return !welded || !doubleWelded;
	}

	public void set(int x, int y, int z, @Nullable DyeColor color) {
		set(getSlot(x, y, z), color);
	}
	
	public @Nullable DyeColor get(int x, int y, int z) {
		return get(getSlot(x, y, z));
	}
	
	public @Nullable DyeColor getMulti(int x, int y, int z, Direction d) {
		return getMulti(x+d.getOffsetX(), y+d.getOffsetY(), z+d.getOffsetZ());
	}
	
	public @Nullable DyeColor getMulti(int x, int y, int z) {
		BlockPos pos = getPos();
		while (x < 0) {
			pos = pos.offset(Axis.X, -1);
			x += XSIZE;
		}
		while (x >= XSIZE) {
			pos = pos.offset(Axis.X, 1);
			x -= XSIZE;
		}
		while (y < 0) {
			pos = pos.offset(Axis.Y, -1);
			y += YSIZE;
		}
		while (y >= YSIZE) {
			pos = pos.offset(Axis.Y, 1);
			y -= YSIZE;
		}
		while (z < 0) {
			pos = pos.offset(Axis.Z, -1);
			z += ZSIZE;
		}
		while (z >= ZSIZE) {
			pos = pos.offset(Axis.Z, 1);
			z -= ZSIZE;
		}
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof BloqueBlockEntity bbe) {
			return bbe.get(x, y, z);
		}
		return null;
	}
	
	public Adjacency getAdjacency(int slot) {
		if (slot < 0 || slot >= adjacency.length) {
			return Adjacency.NONE;
		}
		return MoreObjects.firstNonNull(adjacency[slot], Adjacency.NONE);
	}
	
	public Adjacency getAdjacency(int x, int y, int z) {
		return getAdjacency(getSlot(x, y, z));
	}
	
	public boolean isFullCube() {
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] == null) return false;
		}
		return true;
	}
	
	public int getPopCount() {
		int c = 0;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i] != null) c++;
		}
		return c;
	}
	
	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		colors[type] = data == -1 ? null : DyeColor.byId(data);
		computeAdjacency(false, false);
		if (world instanceof YttrWorld yw) {
			yw.yttr$scheduleRenderUpdate(getPos());
		}
		return true;
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		byte[] bys = new byte[SLOTS];
		for (int i = 0; i < SLOTS; i++) {
			bys[i] = (byte)(colors[i] == null ? -1 : colors[i].getId());
		}
		nbt.putByteArray("Colors", bys);
		if (welded) {
			nbt.putBoolean("Welded", true);
			nbt.putBoolean("DoubleWelded", doubleWelded);
			byte[] abys = new byte[SLOTS*6];
			for (int i = 0; i < SLOTS; i++) {
				int j = i*6;
				for (Direction d : Direction.values()) {
					abys[j+d.ordinal()] = (byte)getAdjacency(i).getType(d).ordinal();
				}
			}
			nbt.putByteArray("Adjacency", abys);
		}
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		byte[] bys = nbt.getByteArray("Colors");
		Arrays.fill(colors, null);
		for (int i = 0; i < Math.min(bys.length, colors.length); i++) {
			int v = bys[i];
			if (v != -1) {
				colors[i] = DyeColor.byId(v);
			}
		}
		welded = nbt.getBoolean("Welded");
		if (welded && nbt.getByteArray("Adjacency").length == SLOTS*6) {
			doubleWelded = nbt.getBoolean("DoubleWelded");
			byte[] abys = nbt.getByteArray("Adjacency");
			for (int i = 0; i < SLOTS; i++) {
				int j = i*6;
				AdjType[] types = new AdjType[6];
				for (Direction d : Direction.values()) {
					types[d.ordinal()] = AdjType.values()[abys[j+d.ordinal()]&0xFF];
				}
				adjacency[i] = new Adjacency(types);
			}
		} else {
			welded = false;
			doubleWelded = false;
			if (world != null && world.isClient) {
				computeAdjacency(false, false);
			}
		}
		if (world instanceof YttrWorld yw) {
			yw.yttr$scheduleRenderUpdate(getPos());
		}
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return toNbt();
	}
	
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public @Nullable Object getRenderAttachmentData() {
		Adjacency[] adj = adjacency.clone();
		for (int y = 0; y < YSIZE; y++) {
			for (int x = 0; x < XSIZE; x++) {
				for (int z = 0; z < ZSIZE; z++) {
					int slot = getSlot(x, y, z);
					if (colors[slot] == null) continue;
					boolean changed = false;
					AdjType[] nw = new AdjType[6];
					for (Direction d : Direction.values()) {
						AdjType type = getAdjacency(slot).getType(d);
						if (type.cullable()) {
							if (type.cullable() && getMulti(x, y, z, d) != null) {
								type = type.culled();
								changed = true;
							} else if (type == AdjType.CULL_EDGE) {
								BlockPos ofs = pos.offset(d);
								BlockState other = world.getBlockState(ofs);
								if (!other.isOf(YBlocks.BLOQUE)) {
									VoxelShape otherShape = other.getCullingShape(world, pos);
									if (otherShape != null && !otherShape.isEmpty()) {
										VoxelShape shape = VOXEL_SHAPES[slot];
										VoxelShape touching = VoxelShapes.combine(shape.getFace(d), otherShape.getFace(d.getOpposite()), BooleanBiFunction.ONLY_FIRST);
										if (touching.isEmpty()) {
											type = type.culled();
											changed = true;
										}
									}
								}
							}
						}
						nw[d.ordinal()] = type;
					}
					if (changed) {
						adj[slot] = new Adjacency(nw);
					}
				}
			}
		}
		return new RenderData(colors.clone(), adj, welded, doubleWelded);
	}

	public int getSlotForPlacement(Vec3d hitPos, BlockPos blockPos, Direction face) {
		int slot = getSlot(hitPos, blockPos, face);
		if (get(slot) != null) {
			Vec3f vec = face.getUnitVector();
			return getSlot(hitPos.add(new Vec3d(vec.getX()*0.2, vec.getY()*0.2, vec.getZ()*0.2)), blockPos, face);
		}
		return slot;
	}

}
