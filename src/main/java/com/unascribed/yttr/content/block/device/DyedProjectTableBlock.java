package com.unascribed.yttr.content.block.device;

import java.util.List;
import java.util.Locale;

import com.unascribed.lib39.dessicant.api.SimpleLootBlock;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.DyedBlockItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class DyedProjectTableBlock extends ProjectTableBlock implements BlockColorProvider, SimpleLootBlock {

	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);
	
	public DyedProjectTableBlock(Settings settings) {
		super(settings);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(COLOR);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		return state.get(COLOR).getFireworkColor();
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(COLOR, ctx.getStack().getItem() instanceof DyedBlockItem ? ((DyedBlockItem)ctx.getStack().getItem()).color : DyeColor.WHITE);
	}

	@Override
	public String getTranslationKey() {
		return "block.yttr.project_table";
	}
	
	@Override
	public ItemStack getLoot(BlockState state) {
		return new ItemStack(Registries.ITEM.get(Yttr.id(state.get(COLOR).name().toLowerCase(Locale.ROOT)+"_project_table")));
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return getLoot(state);
	}
}
