package com.unascribed.yttr.mixin.transfungus;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.content.entity.SlippingTransfungusEntity;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YTags;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(PistonHandler.class)
public class MixinPistonHandler {

	@Shadow @Final
	private World world;
	@Shadow @Final
	private BlockPos pistonPos;
	@Shadow @Final
	private List<BlockPos> movedBlocks;
	@Shadow @Final
	private List<BlockPos> brokenBlocks;
	@Shadow @Final
	private Direction pistonFacing;
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/block/piston/PistonHandler.isBlockSticky(Lnet/minecraft/block/BlockState;)Z"), method="tryMove", cancellable=true,
			locals=LocalCapture.CAPTURE_FAILHARD)
	public void tryMoveDirect(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> ci, BlockState state) {
		BlockState movingState = world.getBlockState(pos);
		if (movingState.isOf(YBlocks.TRANSFUNGUS)) {
			var afterPos = pos.offset(dir);
			var after = world.getBlockState(afterPos);
			var beforePos = pos.offset(dir.getOpposite());
			var before = world.getBlockState(beforePos);
			if (after.isFullCube(world, afterPos) && before.isFullCube(world, beforePos)) {
				world.breakBlock(pos, true);
				ci.setReturnValue(true);
				return;
			}
			if (dir != pistonFacing) {
				// pulling
				return;
			}
			if (world.getBlockState(pistonPos).isOf(Blocks.STICKY_PISTON)) return;
			if (after.isAir() && world.getBlockState(afterPos.down()).isIn(YTags.Block.TRANSFUNGUS_SLIPPERY)) {
				var ste = new SlippingTransfungusEntity(
					world,
					pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
					state
				);
				world.setBlockState(pos, state.getFluidState().getBlockState(), 3);
				ste.setVelocity(new Vec3d(dir.getUnitVector()).multiply(0.4));
				world.spawnEntity(ste);
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				ci.setReturnValue(true);
				return;
			}
		}
	}
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/block/BlockState.getPistonBehavior()Lnet/minecraft/block/piston/PistonBehavior;"), method="tryMove", cancellable=true,
			locals=LocalCapture.CAPTURE_FAILHARD)
	public void tryMoveBetween(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> ci, BlockState state, int i, int j, int k, BlockPos moving) {
		BlockState movingState = world.getBlockState(moving);
		if (movingState.isOf(YBlocks.TRANSFUNGUS)) {
			var afterPos = moving.offset(dir);
			var after = world.getBlockState(afterPos);
			var beforePos = moving.offset(dir.getOpposite());
			var before = world.getBlockState(beforePos);
			if (after.isFullCube(world, afterPos) && before.isFullCube(world, beforePos)) {
				brokenBlocks.add(moving);
				ci.setReturnValue(true);
				return;
			}
		}
	}
	
}
