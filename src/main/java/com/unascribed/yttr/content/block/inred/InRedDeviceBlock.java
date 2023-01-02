package com.unascribed.yttr.content.block.inred;

import com.unascribed.yttr.inred.EncoderScannable;
import com.unascribed.yttr.inred.InRedDevice;
import com.unascribed.yttr.inred.InRedProvider;
import com.unascribed.yttr.inred.MultimeterProbeProvider;
import com.unascribed.yttr.util.YTickable;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
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
		if (be instanceof AbstractInRedDeviceBlockEntity) {
			return ((AbstractInRedDeviceBlockEntity) be).getDevice(inspectingFrom);
		}
		return null;
	}

	@Override
	public int getEncoderValue(BlockView world, BlockPos pos, BlockState state, Direction inspectingFrom) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof AbstractInRedDeviceBlockEntity) {
			return ((AbstractInRedDeviceBlockEntity) be).getEncoderValue(inspectingFrom);
		}
		return 0;
	}

	@Override
	public Text getProbeMessage(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof AbstractInRedDeviceBlockEntity) {
			return ((AbstractInRedDeviceBlockEntity) be).getProbeMessage();
		}
		//TODO: better fallback message? this should never happen anyway lol
		return Text.translatable("tip.yttr.inred.multimeter.block");
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return YTickable::tick;
	}
}