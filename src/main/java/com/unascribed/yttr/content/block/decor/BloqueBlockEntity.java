package com.unascribed.yttr.content.block.decor;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlockEntities;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.unascribed.yttr.content.block.decor.BloqueBlock.*;

public class BloqueBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

	public record RenderData(DyeColor[] colors, Adjacency[] adjacency, boolean welded, boolean doubleWelded) {}
	public record Adjacency(boolean down, boolean up, boolean north, boolean south, boolean west, boolean east) {
		public static final Adjacency NONE = new Adjacency(false, false, false, false, false, false);
		public Adjacency(boolean[] vals) {
			this(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
		}
		public boolean get(Direction d) {
			switch (d) {
				case DOWN: return down;
				case UP: return up;
				case NORTH: return north;
				case SOUTH: return south;
				case WEST: return west;
				case EAST: return east;
				default: throw new AssertionError(d);
			}
		}
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
		world.addSyncedBlockEvent(getPos(), getCachedState().getBlock(), slot, color == null ? -1 : color.getId());
		markDirty();
	}
	
	public @Nullable DyeColor get(int slot) {
		if (slot < 0 || slot >= colors.length) {
			return null;
		}
		return colors[slot];
	}

	public void weld() {
		if (this.welded && this.doubleWelded) return;
		if (this.welded) {
			this.doubleWelded = true;
			computeAdjacency(true);
		} else {
			this.welded = true;
			computeAdjacency(false);
		}
		markDirty();
		Yttr.sync(this);
	}
	
	public void unweld() {
		this.welded = this.doubleWelded = false;
		Arrays.fill(adjacency, null);
		markDirty();
		Yttr.sync(this);
	}
	
	private void computeAdjacency(boolean mixColors) {
		for (int y = 0; y < YSIZE; y++) {
			for (int x = 0; x < XSIZE; x++) {
				for (int z = 0; z < ZSIZE; z++) {
					int slot = getSlot(x, y, z);
					DyeColor cur = get(slot);
					if (cur == null) {
						adjacency[slot] = Adjacency.NONE;
						continue;
					}
					boolean[] b = new boolean[6];
					for (Direction d : Direction.values()) {
						DyeColor other = getMulti(x+d.getOffsetX(), y+d.getOffsetY(), z+d.getOffsetZ());
						boolean match;
						if (mixColors) {
							match = other != null;
						} else {
							match = other == cur;
						}
						if (match) {
							b[d.ordinal()] = true;
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
		return !doubleWelded;
	}

	public void set(int x, int y, int z, @Nullable DyeColor color) {
		set(getSlot(x, y, z), color);
	}
	
	public @Nullable DyeColor get(int x, int y, int z) {
		return get(getSlot(x, y, z));
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
			byte[] abys = new byte[SLOTS];
			for (int i = 0; i < SLOTS; i++) {
				int v = 0;
				for (Direction d : Direction.values()) {
					if (adjacency[i] != null && adjacency[i].get(d)) {
						v |= (1 << d.ordinal());
					}
				}
				abys[i] = (byte)v;
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
		if (welded) {
			doubleWelded = nbt.getBoolean("DoubleWelded");
			byte[] abys = nbt.getByteArray("Adjacency");
			for (int i = 0; i < SLOTS; i++) {
				int v = abys[i]&0xFF;
				boolean[] vals = new boolean[6];
				for (Direction d : Direction.values()) {
					if ((v & (1 << d.ordinal())) != 0) {
						vals[d.ordinal()] = true;
					}
				}
				adjacency[i] = new Adjacency(vals);
			}
		} else {
			Arrays.fill(adjacency, null);
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
		return new RenderData(colors.clone(), adjacency.clone(), welded, doubleWelded);
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
