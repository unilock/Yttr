package com.unascribed.yttr.content.block.decor;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.cache.CleavedBlockMeshes.UniqueShapeKey;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mixinsupport.YttrWorld;
import com.unascribed.yttr.util.math.opengjk.OpenGJK;
import com.unascribed.yttr.util.math.partitioner.Polygon;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelShape;

public class CleavedBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

	public class CleavedMeshTarget {

		public volatile UniqueShapeKey key;
		public Object cachedMesh;
		public volatile int era;
		
	}

	public static int SHAPE_GRANULARITY = 8;
	
	public static ImmutableSet<Polygon> cube() {
		return ImmutableSet.of(
			new Polygon(
				new Vec3d(0, 0, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(1, 0, 1),
				new Vec3d(0, 0, 1)
			),
			
			new Polygon(
				new Vec3d(0, 1, 1),
				new Vec3d(1, 1, 1),
				new Vec3d(1, 1, 0),
				new Vec3d(0, 1, 0)
			),
			
			new Polygon(
				new Vec3d(0, 0, 1),
				new Vec3d(0, 1, 1),
				new Vec3d(0, 1, 0),
				new Vec3d(0, 0, 0)
			),
			
			new Polygon(
				new Vec3d(1, 0, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 1, 1),
				new Vec3d(1, 0, 1)
			),
			
			new Polygon(
				new Vec3d(0, 1, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(0, 0, 0)
			),
			
			new Polygon(
				new Vec3d(0, 0, 1),
				new Vec3d(1, 0, 1),
				new Vec3d(1, 1, 1),
				new Vec3d(0, 1, 1)
			)
		);
	}
	
	private ImmutableSet<Polygon> polygons = cube();
	private BlockState donor = Blocks.AIR.getDefaultState();
	private Boolean axisAligned;
	
	private final CleavedMeshTarget target = new CleavedMeshTarget();

	private static final ConcurrentMap<ImmutableSet<Polygon>, VoxelShape> sharedShapeCache = new ConcurrentHashMap<>();
	
	private VoxelShape cachedShape;
	
	public CleavedBlockEntity(BlockPos pos, BlockState state) {
		super(YBlockEntities.CLEAVED_BLOCK, pos, state);
	}
	
	public ImmutableSet<Polygon> getPolygons() {
		return polygons;
	}
	
	public boolean isAxisAligned() {
		Boolean v = axisAligned;
		if (v == null) {
			v = true;
			for (var p : getPolygons()) {
				var normal = p.plane().normal();
				int unaligned = 0;
				if (Math.abs(normal.getX()) > 0.001) unaligned++;
				if (Math.abs(normal.getY()) > 0.001) unaligned++;
				if (Math.abs(normal.getZ()) > 0.001) unaligned++;
				if (unaligned != 1) {
					v = false;
					break;
				}
			}
			axisAligned = v;
		}
		return v;
	}
	
	public void setPolygons(Iterable<Polygon> polygons) {
		this.polygons = ImmutableSet.copyOf(polygons);
		fromTagInner(toTagInner(new NbtCompound()));
		cachedShape = null;
		target.cachedMesh = null;
		axisAligned = null;
		markDirty();
		Yttr.sync(this);
	}
	
	public VoxelShape getShape() {
		if (cachedShape != null) return cachedShape;
		var cachedShared = sharedShapeCache.get(polygons);
		if (cachedShared != null) {
			cachedShape = cachedShared;
			return cachedShared;
		}
		world.getProfiler().push("yttr:cleaved_shapegen");
		final int acc = SHAPE_GRANULARITY;
		
		var pt = CleavedBlock.polygonsToPolytope(polygons);
		var pt2 = new OpenGJK.Polytope();
		BitSetVoxelSet voxels = new BitSetVoxelSet(acc, acc, acc);
		for (int x = 0; x < acc; x++) {
			for (int y = 0; y < acc; y++) {
				for (int z = 0; z < acc; z++) {
					double min = 0.4;
					double max = 0.6;
					CleavedBlock.boxToPolytope((x+min)/acc, (y+min)/acc, (z+min)/acc, (x+max)/acc, (y+max)/acc, (z+max)/acc, pt2);
					if (OpenGJK.compute_minimum_distance(pt, pt2, new OpenGJK.Simplex()) < 0.001) {
						voxels.set(x, y, z);
					}
				}
			}
		}

		VoxelShape shape = new SimpleVoxelShape(voxels).simplify();
		cachedShape = shape;
		sharedShapeCache.putIfAbsent(polygons, shape);
		world.getProfiler().pop();
		return shape;
	}
	
	public BlockState getDonor() {
		return donor;
	}
	
	public void setDonor(BlockState donor) {
		this.donor = donor;
		markDirty();
		Yttr.sync(this);
	}

	public void fromTagInner(NbtCompound tag) {
		if (tag.contains("Polygons", NbtElement.LIST_TYPE)) {
			ImmutableSet.Builder<Polygon> builder = ImmutableSet.builder();
			NbtList li = tag.getList("Polygons", NbtElement.BYTE_ARRAY_TYPE);
			for (int i = 0; i < li.size(); i++) {
				NbtElement en = li.get(i);
				List<Vec3d> points = Lists.newArrayList();
				if (!(en instanceof NbtByteArray)) continue;
				byte[] arr = ((NbtByteArray)en).getByteArray();
				for (int j = 0; j < arr.length; j += 3) {
					points.add(new Vec3d(byteToUnit(arr[j]), byteToUnit(arr[j+1]), byteToUnit(arr[j+2])));
				}
				builder.add(new Polygon(points));
			}
			polygons = builder.build();
		} else {
			polygons = cube();
		}
		var lk = this.world != null
				? this.world.filteredLookup(RegistryKeys.BLOCK)
				: Registries.BLOCK.asLookup();
		donor = NbtHelper.toBlockState(lk, tag.getCompound("Donor"));
		axisAligned = null;
		cachedShape = null;
		target.cachedMesh = null;
		if (world instanceof YttrWorld) ((YttrWorld)world).yttr$scheduleRenderUpdate(pos);
	}
	
	public NbtCompound toTagInner(NbtCompound tag) {
		NbtList li = new NbtList();
		for (Polygon poly : polygons) {
			ByteBuffer buf = ByteBuffer.allocate(poly.nPoints()*3);
			poly.forEachDEdge((de) -> {
				buf.put(unitToByte(de.srcPoint().x));
				buf.put(unitToByte(de.srcPoint().y));
				buf.put(unitToByte(de.srcPoint().z));
			});
			buf.flip();
			li.add(new NbtByteArray(buf.array()));
		}
		tag.put("Polygons", li);
		tag.put("Donor", NbtHelper.fromBlockState(donor));
		return tag;
	}
	
	private byte unitToByte(double d) {
		return (byte)((int)(d*255)&0xFF);
	}
	
	private double byteToUnit(byte b) {
		return (b&0xFF)/255D;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		fromTagInner(tag);
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		toTagInner(tag);
		super.writeNbt(tag);
	}
	
	@Override
	public NbtCompound toSyncedNbt() {
		return toTagInner(super.toSyncedNbt());
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public @Nullable Object getRenderAttachmentData() {
		target.key = new UniqueShapeKey(donor, polygons);
		return target;
	}
	

}
