package com.unascribed.yttr.content.block.decor;

import com.unascribed.lib39.dessicant.api.SimpleLootBlock;
import com.unascribed.lib39.waypoint.api.AbstractHaloBlockEntity;
import com.unascribed.yttr.SpecialSubItems;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YItemGroups;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.util.Resolvable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class LampBlock extends Block implements BlockEntityProvider, BlockColorProvider, SimpleLootBlock, SpecialSubItems {

	public static final BooleanProperty LIT = Properties.LIT;
	public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
	public static final EnumProperty<LampColor> COLOR = EnumProperty.of("color", LampColor.class);
	
	public LampBlock(Settings settings) {
		super(FabricBlockSettings.copyOf(settings)
				.emissiveLighting((state, view, pos) -> state.get(LIT))
				.luminance((state) -> state.get(LIT) ? 15 : 0));
		setDefaultState(getDefaultState().with(INVERTED, false).with(LIT, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIT, INVERTED, COLOR);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);
		Item item = stack.getItem();
		LampColor newColor = null;
		if (item instanceof DyeItem) {
			newColor = LampColor.BY_DYE.get(((DyeItem)item).getColor());
		} else {
			newColor = LampColor.BY_ITEM.get(Resolvable.mapKey(item, Registries.ITEM));
		}
		if (newColor == null) {
			return ActionResult.PASS;
		}
		if (state.get(COLOR) != newColor) {
			world.setBlockState(pos, state.with(COLOR, newColor));
			if (!player.getAbilities().creativeMode) {
				stack.decrement(1);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.FAIL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? AbstractHaloBlockEntity::tick : null;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LampBlockEntity(pos, state);
	}
	
	@Override
	public ItemStack getLoot(BlockState state) {
		ItemStack is = new ItemStack(this);
		LampBlockItem.setInverted(is, state.get(INVERTED));
		LampBlockItem.setColor(is, state.get(COLOR));
		return is;
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getLoot(state);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		boolean powered = ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos());
		boolean inverted = LampBlockItem.isInverted(ctx.getStack());
		LampColor color = LampBlockItem.getColor(ctx.getStack());
		return getDefaultState()
				.with(LIT, powered^inverted)
				.with(INVERTED, inverted)
				.with(COLOR, color);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean cur = state.get(LIT);
			if (cur != (world.isReceivingRedstonePower(pos) ^ state.get(INVERTED))) {
				if (cur) {
					world.scheduleBlockTick(pos, this, 4);
				} else {
					world.setBlockState(pos, state.cycle(LIT), 2);
				}
			}

		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (!world.isReceivingRedstonePower(pos) ^ state.get(INVERTED)) {
			world.setBlockState(pos, state.cycle(LIT), 2);
		}
	}
	
	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
		if (group == YItemGroups.LAMPS) {
			int rem = 9-(LampColor.values().length%9);
			if (rem == 9) rem = 0;
			list.add(getLoot(getDefaultState().with(COLOR, LampColor.COLORLESS)));
			for (LampColor color : LampColor.VALUES) {
				if (color == LampColor.COLORLESS) continue;
				list.add(getLoot(getDefaultState().with(COLOR, color)));
			}
			for (int i = 0; i < rem; i++) {
				list.add(ItemStack.EMPTY);
			}
			list.add(getLoot(getDefaultState().with(COLOR, LampColor.COLORLESS).with(INVERTED, true)));
			for (LampColor color : LampColor.VALUES) {
				if (color == LampColor.COLORLESS) continue;
				list.add(getLoot(getDefaultState().with(COLOR, color).with(INVERTED, true)));
			}
			for (int i = 0; i < rem; i++) {
				list.add(ItemStack.EMPTY);
			}
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		LampColor color = state.get(LampBlock.COLOR);
		return state.get(LampBlock.LIT) ? color.baseLitColor : color.baseUnlitColor;
	}

}
