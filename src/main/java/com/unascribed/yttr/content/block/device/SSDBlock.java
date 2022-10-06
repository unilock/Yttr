package com.unascribed.yttr.content.block.device;

import com.unascribed.yttr.content.block.basic.BasicFacingBlock;
import com.unascribed.yttr.inventory.SSDScreenHandler;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SSDBlock extends BasicFacingBlock implements BlockEntityProvider {

	private static final VoxelShape SHAPE_X = createCuboidShape(0, 2, 2, 16, 14, 14);
	private static final VoxelShape SHAPE_Y = createCuboidShape(2, 0, 2, 14, 16, 14);
	private static final VoxelShape SHAPE_Z = createCuboidShape(2, 2, 0, 14, 14, 16);
	
	public SSDBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(FACING).getAxis()) {
			case X -> SHAPE_X;
			case Y -> SHAPE_Y;
			case Z -> SHAPE_Z;
			default -> VoxelShapes.fullCube();
		};
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SSDBlockEntity(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return YTickable::tick;
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getSide());
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SSDBlockEntity ssd) {
			double total = 0;
			for (int i = 0; i < ssd.size(); i++) {
				var s = ssd.getStack(i);
				if (s.isEmpty()) continue;
				total += s.getCount()/(double)Math.min(s.getMaxCount(), SSDBlockEntity.SLOT_MAXES[i]);
			}
			if (total == 0) return 0;
			return (int)(1+((total/ssd.size())*14));
		}
		return 0;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SSDBlockEntity ssd) {
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new SSDScreenHandler(ssd, syncId, inv, new PropertyDelegate() {
						
						@Override
						public int size() {
							return 1;
						}
						
						@Override
						public void set(int index, int value) { }
						
						@Override
						public int get(int index) {
							return ssd.size();
						}
					});
				}
				
				@Override
				public Text getDisplayName() {
					return ssd.getName();
				}
			});
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof SSDBlockEntity ssd) {
				ssd.setSlots(8);
				ItemScatterer.spawn(world, pos, ssd);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

}
