package com.unascribed.yttr.content.block.big;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.unascribed.yttr.init.YSounds;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class BigBlock extends Block {

	public final IntProperty xProp, yProp, zProp;
	public final int xSize, ySize, zSize;
	
	public BigBlock(IntProperty x, IntProperty y, IntProperty z, Settings settings) {
		super(settings);
		this.xSize = Iterables.getLast(x.getValues())+1;
		this.ySize = Iterables.getLast(y.getValues())+1;
		this.zSize = Iterables.getLast(z.getValues())+1;
		xProp = x;
		yProp = y;
		zProp = z;
	}
	
	protected BlockState copyState(BlockState us, BlockState neighbor) {
		return us;
	}

	public @Nullable BlockState getExpectedNeighbor(BlockState state, Direction dir) {
		int x = state.get(xProp)+dir.getOffsetX();
		int y = state.get(yProp)+dir.getOffsetY();
		int z = state.get(zProp)+dir.getOffsetZ();
		if (x < 0 || y < 0 || z < 0) return null;
		if (x >= xSize || y >= ySize || z >= zSize) return null;
		return state.with(xProp, x).with(yProp, y).with(zProp, z);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		BlockState expected = getExpectedNeighbor(state, direction);
		if (expected == null) return state;
		if (newState.isOf(this)) {
			expected = copyState(expected, newState);
		}
		if (newState != expected) {
			if (this instanceof Waterloggable && state.get(Properties.WATERLOGGED)) {
				return Blocks.WATER.getDefaultState();
			}
			return Blocks.AIR.getDefaultState();
		}
		return copyState(state, newState);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);
		world.scheduleBlockTick(pos, this, 1);
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.scheduleBlockTick(pos, this, 1);
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.isCreative()) {
			BlockPos origin = pos.add(-state.get(xProp), -state.get(yProp), -state.get(zProp));
			for (int y = 0; y < ySize; y++) {
				for (int x = 0; x < xSize; x++) {
					for (int z = 0; z < zSize; z++) {
						world.breakBlock(origin.add(x, y, z), false, player);
					}
				}
			}
		} else if (world.isClient && state.get(xProp) == 0 && state.get(yProp) == 0 && state.get(zProp) == 0) {
			BlockSoundGroup sg = getSoundGroup(state);
			world.playSound(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, sg.getBreakSound(), SoundCategory.BLOCKS, (sg.getVolume() + 1) / 2f, sg.getPitch() * 0.8f, false);
		}
	}
	
	@Override
	public BlockSoundGroup getSoundGroup(BlockState state) {
		BlockSoundGroup sg = super.getSoundGroup(state);
		if (state.get(xProp) == 0 && state.get(yProp) == 0 && state.get(zProp) == 0) {
			return sg;
		}
		return new BlockSoundGroup(sg.volume, sg.pitch, YSounds.SILENCE, sg.getStepSound(), sg.getPlaceSound(), sg.getHitSound(), sg.getFallSound());
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		for (Direction dir : Direction.values()) {
			BlockState expected = getExpectedNeighbor(state, dir);
			if (expected != null) {
				BlockState have = world.getBlockState(pos.offset(dir));
				if (have != expected) {
					world.breakBlock(pos, false);
					return;
				}
			}
		}
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		if (state.get(xProp) != 0 || state.get(yProp) != 0 || state.get(zProp) != 0) return ImmutableList.of();
		return super.getDroppedStacks(state, builder);
	}
	
	public void alterDroppedEntity(BlockPos pos, BlockState state, ItemEntity entity) {
		double x = pos.getX()-state.get(xProp)+(entity.world.random.nextFloat() * (xSize/2D) + (xSize/4D));
		double y = pos.getY()-state.get(yProp)+(entity.world.random.nextFloat() * (ySize/2D) + (ySize/4D));
		double z = pos.getZ()-state.get(zProp)+(entity.world.random.nextFloat() * (zSize/2D) + (zSize/4D));
		entity.setPosition(x, y, z);
	}

	public static void playSound(World world, PlayerEntity player, BlockPos pos, BlockState state, SoundEvent event, SoundCategory cat, float vol, float pitch) {
		if (!(state.getBlock() instanceof BigBlock)) {
			world.playSound(player, pos, event, cat, vol, pitch);
			return;
		}
		BigBlock b = (BigBlock)state.getBlock();
		double x = (pos.getX()-state.get(b.xProp))+(b.xSize/2D);
		double y = (pos.getY()-state.get(b.yProp))+(b.ySize/2D);
		double z = (pos.getZ()-state.get(b.zProp))+(b.zSize/2D);
		world.playSound(player, x, y, z, event, cat, vol, pitch);
	}

	public static boolean isReceivingRedstonePower(World world, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof BigBlock)) {
			return world.isReceivingRedstonePower(pos);
		}
		BigBlock b = (BigBlock)state.getBlock();
		int oX = pos.getX()-state.get(b.xProp);
		int oY = pos.getY()-state.get(b.yProp);
		int oZ = pos.getZ()-state.get(b.zProp);
		BlockPos.Mutable bp = new BlockPos.Mutable();
		for (int x = 0; x < b.xSize; x++) {
			for (int y = 0; y < b.ySize; y++) {
				for (int z = 0; z < b.zSize; z++) {
					bp.set(oX+x, oY+y, oZ+z);
					if (world.isReceivingRedstonePower(bp)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean anyNeighborsMatch(World w, BlockPos pos, BlockState state, Predicate<BlockState> pred) {
		if (!(state.getBlock() instanceof BigBlock)) {
			return false;
		}
		BigBlock b = (BigBlock)state.getBlock();
		int x = state.get(b.xProp);
		int y = state.get(b.yProp);
		int z = state.get(b.zProp);
		for (Direction d : Direction.values()) {
			int nX = x+d.getOffsetX();
			int nY = y+d.getOffsetY();
			int nZ = z+d.getOffsetZ();
			if (nX < 0 || nX >= b.xSize) continue;
			if (nY < 0 || nY >= b.ySize) continue;
			if (nZ < 0 || nZ >= b.zSize) continue;
			BlockPos bp = pos.offset(d);
			if (!w.getBlockState(bp).isOf(state.getBlock())) continue;
			if (pred.test(w.getBlockState(bp))) {
				return true;
			}
		}
		return false;
	}
	
}
