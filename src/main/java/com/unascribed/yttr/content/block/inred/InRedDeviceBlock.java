package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.fuckmojang.YTickable;
import com.unascribed.yttr.inred.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class InRedDeviceBlock extends BlockWithEntity implements InRedProvider, EncoderScannable, MultimeterProbeProvider {

	public InRedDeviceBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public InRedDevice getDevice(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getDevice(inspectingFrom);
		}
		return null;
	}

	@Override
	public int getEncoderValue(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getEncoderValue(inspectingFrom);
		}
		return 0;
	}

	@Override
	public Text getProbeMessage(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof InRedDeviceBlockEntity) {
			return ((InRedDeviceBlockEntity) be).getProbeMessage();
		}
		//TODO: better fallback message? this should never happen anyway lol
		return new TranslatableText("tip.yttr.inred.multimeter.block");
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return YTickable::tick;
	}
}