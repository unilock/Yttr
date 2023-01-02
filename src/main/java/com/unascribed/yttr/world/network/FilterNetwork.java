package com.unascribed.yttr.world.network;

import java.util.UUID;

import com.unascribed.lib39.mesh.api.BlockNetwork;
import com.unascribed.lib39.mesh.api.BlockNetworkManager;
import com.unascribed.lib39.mesh.api.BlockNetworkNode;
import com.unascribed.lib39.mesh.api.BlockNetworkType;
import com.unascribed.yttr.content.block.device.VoidFilterBlock;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class FilterNetwork extends BlockNetwork<BlockNetworkNode, FilterNodeTypes> {

	public static final BlockNetworkType<BlockNetworkNode, FilterNodeTypes> TYPE = new BlockNetworkType<BlockNetworkNode, FilterNodeTypes>() {

		@Override
		public BlockNetwork<BlockNetworkNode, FilterNodeTypes> construct(BlockNetworkManager owner, UUID id) {
			return new FilterNetwork(owner, this, id);
		}

		@Override
		public BlockNetworkNode deserializeNode(BlockPos pos, FilterNodeTypes type, NbtCompound nbt) {
			return new BlockNetworkNode(pos, type);
		}

		@Override
		public BlockNetworkNode createNode(BlockPos pos, FilterNodeTypes type) {
			return new BlockNetworkNode(pos, type);
		}

		@Override
		public FilterNodeTypes[] getNodeTypes() {
			return FilterNodeTypes.values();
		}
	};
	
	private int totalFluidCapacity;
	private int fluidProductionPerTick;
	
	private int fluidContent;
	
	protected FilterNetwork(BlockNetworkManager owner, BlockNetworkType<BlockNetworkNode, FilterNodeTypes> type, UUID id) {
		super(owner, type, id);
	}
	
	@Override
	public void update() {
		boolean complete = isComplete();
		for (BlockNetworkNode n : getMembersByType().get(FilterNodeTypes.FILTER)) {
			BlockState bs = getWorld().getBlockState(n.getPos());
			if (bs.isOf(YBlocks.VOID_FILTER)) {
				getWorld().setBlockState(n.getPos(), bs.with(VoidFilterBlock.INDEPENDENT, !complete));
			}
		}
		totalFluidCapacity = (getMembersByType().get(FilterNodeTypes.PIPE).size()*100) + (getMembersByType().get(FilterNodeTypes.TANK).size()*64000);
		fluidProductionPerTick = getMembersByType().get(FilterNodeTypes.FILTER).size()*2;
//		System.out.println("total capacity: "+totalFluidCapacity);
//		System.out.println("production per tick: "+fluidProductionPerTick);
//		if (fluidProductionPerTick > 0) {
//			System.out.println("time to destruction: "+((totalFluidCapacity/fluidProductionPerTick)/1200)+"m");
//		} else {
//			System.out.println("time to destruction: ∞");
//		}
	}
	
	public boolean isComplete() {
		return getMembersByType().containsKey(FilterNodeTypes.TANK)
				&& getMembersByType().containsKey(FilterNodeTypes.DSU)
				&& getMembersByType().containsKey(FilterNodeTypes.FILTER);
	}

	@Override
	public void readNbt(NbtCompound compound) {
		super.readNbt(compound);
		compound.putInt("FluidContent", fluidContent);
	}
	
	@Override
	public void writeNbt(NbtCompound compound) {
		super.writeNbt(compound);
		fluidContent = compound.getInt("FluidContent");
	}

	public int getPressure() {
		return 0;
	}
	
}
