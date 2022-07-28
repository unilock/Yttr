package com.unascribed.yttr.content.block.decor;

import java.util.Arrays;

import javax.annotation.Nullable;

import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.mixinsupport.YttrWorld;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import static com.unascribed.yttr.content.block.decor.BloqueBlock.*;

public class BloqueBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

	private final DyeColor[] colors = new DyeColor[BloqueBlock.SLOTS];
	
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
	
	private String describe(BlockPos pos) {
		return pos.getX()+", "+pos.getY()+", "+pos.getZ();
	}

	private String describe(int slot) {
		if (slot < 0) return "INVALID";
		int z = slot%ZSIZE;
		int x = (slot/ZSIZE)%XSIZE;
		int y = slot/ZSIZE/XSIZE;
		return x+", "+y+", "+z;
	}
	
	

	public void set(int x, int y, int z, @Nullable DyeColor color) {
		set(getSlot(x, y, z), color);
	}
	
	public @Nullable DyeColor get(int x, int y, int z) {
		return get(getSlot(x, y, z));
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
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return toNbt();
	}

	@Override
	public @Nullable Object getRenderAttachmentData() {
		return colors.clone();
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
