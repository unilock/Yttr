package com.unascribed.yttr.content.block.decor;

import static com.unascribed.yttr.content.block.decor.BloqueBlock.*;

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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import org.joml.Vector3f;

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
	
	public enum DrawStyle {
		NORMAL,
		WELDED,
		SMOOTHED,
		;
		public boolean welded() {
			return this != NORMAL;
		}
		public boolean drawStuds() {
			return this != SMOOTHED;
		}
	}
	
	public record RenderData(DyeColor[] colors, Adjacency[] adjacency, DrawStyle style) {}
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
	private int welds;
	
	private VoxelShape shapeCache;
	
	public BloqueBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.BLOQUE, pos, state);
	}

	public VoxelShape getVoxelShape() {
		if (shapeCache != null) return shapeCache;
		VoxelShape vs = VoxelShapes.empty();
		for (int i = 0; i < SLOTS; i++) {
			if (get(i) != null) {
				vs = VoxelShapes.combine(vs, VOXEL_SHAPES[i], BooleanBiFunction.OR);
			}
		}
		if (vs == VoxelShapes.empty()) {
			// so buggy bloques can still be removed
			vs = VoxelShapes.cuboid(0.2, 0.2, 0.2, 0.8, 0.8, 0.8);
		}
		return shapeCache = vs;
	}
	
	public void set(int slot, @Nullable DyeColor color) {
		if (slot < 0 || slot >= colors.length) {
			return;
		}
		colors[slot] = color;
		if (!isWelded()) {
			world.addSyncedBlockEvent(getPos(), getCachedState().getBlock(), slot, color == null ? -1 : color.getId());
		}
		shapeCache = null;
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
		if (welds == 0) {
			welds = 1;
			computeAdjacency(true, false, true);
		} else if (welds == 1) {
			welds = 2;
			computeAdjacency(true, true, false);
		} else if (welds >= 2) {
			welds = 3;
			computeAdjacency(true, true, true);
		}
		markDirty();
		Yttr.sync(this);
	}
	
	public void unweld() {
		if (!isWelded()) return;
		this.welds = 0;
		Arrays.fill(adjacency, null);
		markDirty();
		Yttr.sync(this);
	}
	
	private void computeAdjacency(boolean weld, boolean allowExternal, boolean mixColors) {
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
						DyeColor other = allowExternal ? getMulti(ox, oy, oz) : get(ox, oy, oz);
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
		return welds > 0;
	}

	public boolean isWeldable() {
		return true;
	}

	public void set(int x, int y, int z, @Nullable DyeColor color) {
		set(getSlot(x, y, z), color);
	}
	
	public @Nullable DyeColor get(int x, int y, int z) {
		if (x < 0 || y < 0 || z < 0 ||
				x >= XSIZE || y >= YSIZE || z >= ZSIZE) {
			return null;
		}
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
		if (!world.isClient) return true;
		colors[type] = data == -1 ? null : DyeColor.byId(data);
		computeAdjacency(false, false, false);
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
		if (welds > 0) {
			nbt.putByte("Welds", (byte)welds);
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
		shapeCache = null;
		byte[] bys = nbt.getByteArray("Colors");
		Arrays.fill(colors, null);
		for (int i = 0; i < Math.min(bys.length, colors.length); i++) {
			int v = bys[i];
			if (v != -1) {
				colors[i] = DyeColor.byId(v);
			}
		}
		welds = nbt.getByte("Welds");
		if (welds > 0 && nbt.getByteArray("Adjacency").length == SLOTS*6) {
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
			if (world != null && world.isClient) {
				computeAdjacency(false, true, false);
			}
		}
		if (world instanceof YttrWorld yw) {
			yw.yttr$scheduleRenderUpdate(getPos());
		}
	}
	
	@Override
	public NbtCompound toSyncedNbt() {
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
		DrawStyle style = DrawStyle.NORMAL;
		if (welds > 0) {
			style = DrawStyle.WELDED;
			if (welds > 2) {
				style = DrawStyle.SMOOTHED;
			}
		}
		return new RenderData(colors.clone(), adj, style);
	}

	public int getSlotForPlacement(Vec3d hitPos, BlockPos blockPos, Direction face) {
		int slot = getSlot(hitPos, blockPos, face);
		if (get(slot) != null) {
			Vector3f vec = face.getUnitVector();
			return getSlot(hitPos.add(new Vec3d(vec.x()*0.2, vec.y()*0.2, vec.z()*0.2)), blockPos, face);
		}
		return slot;
	}

}
