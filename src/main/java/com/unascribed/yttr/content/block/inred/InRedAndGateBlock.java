package com.unascribed.yttr.content.block.inred;

import com.unascribed.lib39.util.api.RelativeFace;
import com.unascribed.yttr.inred.InactiveSelection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InRedAndGateBlock extends InRedLogicTileBlock {
	public static final EnumProperty<InactiveSelection> INACTIVE = EnumProperty.of("inactive", InactiveSelection.class);

	public InRedAndGateBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getStateManager().getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(WATERLOGGED, false)
				.with(INACTIVE, InactiveSelection.NONE));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(MODE, INACTIVE);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new InRedAndGateBlockEntity(pos, state);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if(!world.isClient() && !player.isSneaking() && be instanceof InRedAndGateBlockEntity) {
			InRedAndGateBlockEntity beAndGate = (InRedAndGateBlockEntity)be;
			RelativeFace rf = RelativeFace.from(state.get(FACING), hit.getSide());
			if (rf == RelativeFace.FRONT) {
				beAndGate.toggleBooleanMode();
			} else if (rf == RelativeFace.LEFT) {
				beAndGate.toggleInactive(InactiveSelection.LEFT);
			} else if (rf == RelativeFace.BACK) {
				beAndGate.toggleInactive(InactiveSelection.BACK);
			} else if (rf == RelativeFace.RIGHT) {
				beAndGate.toggleInactive(InactiveSelection.RIGHT);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return getStrongRedstonePower(state, world, pos, side);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side!=state.get(FACING).getOpposite()) return 0;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedAndGateBlockEntity) {
			return ((InRedAndGateBlockEntity) be).isActive()? 16 : 0;
		}
		return 0;
	}

	@Override
	public boolean isRedstonePowerSource(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerFacing()).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!this.canBlockStay(world, pos)) {
			world.breakBlock(pos, true);

			for (Direction dir : Direction.values()) {
				world.updateNeighborsAlways(pos.offset(dir), this);
			}
		} else {
			if (state.get(WATERLOGGED)) {
				world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
			}
		}
	}
}
