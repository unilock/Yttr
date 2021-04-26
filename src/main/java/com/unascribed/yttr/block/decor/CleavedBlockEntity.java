package com.unascribed.yttr.block.decor;

import java.nio.ByteBuffer;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.client.cache.CleavedBlockMeshes;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.util.math.partitioner.Polygon;
import com.unascribed.yttr.util.math.partitioner.Where;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelShape;

public class CleavedBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {

	public static List<Polygon> cube() {
		return Lists.newArrayList(
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
	
	private List<Polygon> polygons = cube();
	private BlockState donor = Blocks.AIR.getDefaultState();
	
	public Object clientCacheData;
	
	
	private VoxelShape cachedShape;
	
	public CleavedBlockEntity() {
		super(YBlockEntities.CLEAVED_BLOCK);
	}
	
	public List<Polygon> getPolygons() {
		return polygons;
	}
	
	public void setPolygons(Iterable<Polygon> polygons) {
		this.polygons = ImmutableList.copyOf(polygons);
		cachedShape = null;
		markDirty();
		if (!world.isClient) sync();
	}
	
	public VoxelShape getShape() {
		if (cachedShape != null) return cachedShape;
		world.getProfiler().push("yttr:cleaved_shapegen");
		final int acc = 8;
		
		BitSetVoxelSet voxels = new BitSetVoxelSet(acc, acc, acc);
		for (int x = 0; x < acc; x++) {
			for (int y = 0; y < acc; y++) {
				for (int z = 0; z < acc; z++) {
					Vec3d point = new Vec3d((x+0.5)/acc, (y+0.5)/acc, (z+0.5)/acc);
					boolean inside = true;
					for (Polygon p : polygons) {
						if (p.plane().whichSide(point) == Where.ABOVE) {
							inside = false;
							break;
						}
					}
					if (inside) {
						voxels.set(x, y, z, true, true);
					}
				}
			}
		}

		VoxelShape shape = new SimpleVoxelShape(voxels).simplify();
		cachedShape = shape;
		world.getProfiler().pop();
		return shape;
	}
	
	public BlockState getDonor() {
		return donor;
	}
	
	public void setDonor(BlockState donor) {
		this.donor = donor;
		markDirty();
		if (!world.isClient) sync();
	}

	public void fromTagInner(CompoundTag tag) {
		if (tag.contains("Polygons", NbtType.LIST)) {
			ImmutableList.Builder<Polygon> builder = ImmutableList.builder();
			ListTag li = tag.getList("Polygons", NbtType.BYTE_ARRAY);
			for (int i = 0; i < li.size(); i++) {
				Tag en = li.get(i);
				List<Vec3d> points = Lists.newArrayList();
				if (!(en instanceof ByteArrayTag)) continue;
				byte[] arr = ((ByteArrayTag)en).getByteArray();
				for (int j = 0; j < arr.length; j += 3) {
					points.add(new Vec3d(byteToUnit(arr[j]), byteToUnit(arr[j+1]), byteToUnit(arr[j+2])));
				}
				builder.add(new Polygon(points));
			}
			polygons = builder.build();
		} else {
			polygons = cube();
		}
		donor = NbtHelper.toBlockState(tag.getCompound("Donor"));
		clientCacheData = null;
		cachedShape = null;
		if (world != null && world.isClient) scheduleRerender();
	}
	
	@Environment(EnvType.CLIENT)
	private void scheduleRerender() {
		((ClientWorld)world).scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
	}

	public CompoundTag toTagInner(CompoundTag tag) {
		ListTag li = new ListTag();
		for (Polygon poly : polygons) {
			ByteBuffer buf = ByteBuffer.allocate(poly.nPoints()*3);
			poly.forEachDEdge((de) -> {
				buf.put(unitToByte(de.srcPoint().x));
				buf.put(unitToByte(de.srcPoint().y));
				buf.put(unitToByte(de.srcPoint().z));
			});
			buf.flip();
			li.add(new ByteArrayTag(buf.array()));
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
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		fromTagInner(tag);
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		toTagInner(tag);
		return super.toTag(tag);
	}
	
	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTagInner(super.toInitialChunkDataTag());
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTagInner(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		toTagInner(tag);
		return tag;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public @Nullable Object getRenderAttachmentData() {
		return CleavedBlockMeshes.getMesh(this);
	}
	

}
