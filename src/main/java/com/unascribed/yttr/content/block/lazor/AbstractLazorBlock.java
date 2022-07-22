package com.unascribed.yttr.content.block.lazor;

import java.util.Random;

import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.mechanics.LampColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public abstract class AbstractLazorBlock extends Block implements Waterloggable, BlockColorProvider {

	public static final DamageSource DAMAGE_SOURCE = new DamageSource("yttr.lazor") {{setFire();}};

	public static final DirectionProperty FACING = Properties.FACING;
	public static final EnumProperty<LampColor> COLOR = LampBlock.COLOR;

	public AbstractLazorBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING, COLOR);
	}

	protected abstract boolean isEmitter();

	@Override
	public boolean hasRandomTicks(BlockState state) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection()).with(COLOR, LampBlockItem.getColor(ctx.getStack()));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		BlockPos behind = pos.offset(state.get(FACING).getOpposite());
		BlockPos ahead = pos.offset(state.get(FACING));
		BlockState behindState = world.getBlockState(behind);
		BlockState aheadState = world.getBlockState(ahead);
		if (!isEmitter() && (!(behindState.getBlock() instanceof AbstractLazorBlock) || behindState.get(COLOR) != state.get(COLOR) || behindState.get(FACING) != state.get(FACING))) {
			world.setBlockState(pos, state.getFluidState().getBlockState());
		} else if (aheadState.isAir() || aheadState.getMaterial().isReplaceable()) {
			if (isEmitter()) {
				state = YBlocks.LAZOR_BEAM.getDefaultState().with(FACING, state.get(FACING)).with(COLOR, state.get(COLOR));
			}
			if (state.getBlock() instanceof Waterloggable) {
				state = state.with(Properties.WATERLOGGED, aheadState.getFluidState().isIn(FluidTags.WATER));
			}
			world.setBlockState(ahead, state);
		}
	}

	@Override
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		return state.get(COLOR).baseLitColor;
	}

}
