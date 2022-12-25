package com.unascribed.yttr.content.block.lazor;

import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.mechanics.LampColor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public abstract class AbstractColoredLazorBlock extends AbstractLazorBlock implements BlockColorProvider {

	public static final EnumProperty<LampColor> COLOR = LampBlock.COLOR;

	public AbstractColoredLazorBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected boolean areEquivalent(BlockState a, BlockState b) {
		return super.areEquivalent(a, b) && a.getBlock() instanceof AbstractColoredLazorBlock && b.getBlock() instanceof AbstractColoredLazorBlock &&
				a.get(COLOR) == b.get(COLOR);
	}
	
	@Override
	protected BlockState copyProperties(BlockState from, BlockState to) {
		return super.copyProperties(from, to).with(COLOR, from.get(COLOR));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(COLOR);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(COLOR, LampBlockItem.getColor(ctx.getStack()));
	}

	@Override
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		return state.get(COLOR).baseLitColor;
	}

}
