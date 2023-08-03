package com.unascribed.yttr.content.block.mechanism;

import javax.annotation.Nullable;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YTags;

import com.google.common.base.Ascii;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class VelresinBlock extends Block {

	public enum Facing implements StringIdentifiable {
		NORTH(Direction.NORTH, 4, 0, 8, 4),
		SOUTH(Direction.SOUTH, 4, 12, 8, 4),
		EAST(Direction.EAST, 0, 4, 4, 8),
		WEST(Direction.WEST, 12, 4, 4, 8),
		SELF(null, 4, 4, 8, 8),
		;
		private final Direction dir;
		private final int x1, y1, x2, y2;
		
		Facing(Direction dir, int x, int y, int w, int h) {
			this.dir = dir;
			this.x1 = x;
			this.y1 = y;
			this.x2 = x+w;
			this.y2 = y+h;
		}
		
		public Direction dir() {
			return dir;
		}
		
		public boolean contains(int x, int y) {
			return x >= x1 && x < x2 &&
					y >= y1 && y < y2;
		}

		@Override
		public String asString() {
			return Ascii.toLowerCase(name());
		}
	}

	private static final VoxelShape OUTLINE_SHAPE = createCuboidShape(0, 0, 0, 16, 3, 16);
	
	public static final EnumProperty<Facing> FACING = EnumProperty.of("facing", Facing.class);

	public VelresinBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Facing.SELF));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(FACING);
	}
	
	public static @Nullable Facing getTargetedFacing(BlockHitResult bhr) {
		if (bhr.getSide() != Direction.UP) return null;
		int x = 15-Math.floorMod((long)(bhr.getPos().x*16), 16);
		int y = Math.floorMod((long)(bhr.getPos().z*16), 16);
		for (var f : Facing.values()) {
			if (f.contains(x, y)) return f;
		}
		return null;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		var tgt = getTargetedFacing(hit);
		if (tgt != null && player.canModifyBlocks()) {
			var s = player.getStackInHand(hand);
			if (tgt != state.get(FACING)) {
				world.playSound(player, pos, SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, player.getSoundCategory(), 1, 2);
				if (!world.isClient) {
					world.setBlockState(pos, state.with(FACING, tgt));
				}
				return ActionResult.SUCCESS;
			} else if (s.isOf(YItems.VELRESIN) && tgt.dir() != null) {
				var ofs = pos.offset(tgt.dir());
				if (YItems.VELRESIN.place(new ItemPlacementContext(player, hand, s, hit.withBlockPos(ofs))).isAccepted()) {
					if (!world.isClient) {
						var otherState = world.getBlockState(ofs);
						if (otherState.isOf(this)) {
							world.setBlockState(ofs, otherState.with(FACING, tgt));
						}
					}
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		var bp = pos.down();
		var bs = world.getBlockState(bp);
		return bs.isIn(YTags.Block.VELRESIN_STABLE) ||
				(bs.isSideSolidFullSquare(world, bp, Direction.UP) && bs.getBlock().getSlipperiness() <= 0.6f);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		if (!canPlaceAt(state, world, pos)) {
			world.breakBlock(pos, true);
		}
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (entity.getY()-pos.getY() < 0.5 && !entity.isSneaking() && !(entity instanceof BoatEntity)) {
			var f = state.get(FACING);
			if (f != Facing.SELF) {
				double m = entity instanceof ItemEntity ie ? ie.getStack().isOf(YItems.VELRESIN) ? 1.2 : 0.4 : 0.2;
				entity.addVelocity(f.dir().getOffsetX()*m, f.dir().getOffsetY()*m, f.dir().getOffsetZ()*m);
			} else {
				double m = entity instanceof ItemEntity ie ? 0.55 : 0.3;
				var v = entity.getVelocity().multiply(m, 0, m);
				entity.addVelocity(v.x, v.y, v.z);
			}
		}
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
}
