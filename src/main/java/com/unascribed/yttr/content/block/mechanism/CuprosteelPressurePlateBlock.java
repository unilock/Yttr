package com.unascribed.yttr.content.block.mechanism;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.init.YTags;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CuprosteelPressurePlateBlock extends AbstractPressurePlateBlock {
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final EnumProperty<Axis> AXIS = Properties.HORIZONTAL_AXIS;

	public CuprosteelPressurePlateBlock(Settings settings) {
		super(FabricBlockSettings.copyOf(settings)
				.noCollision());
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED, AXIS);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(AXIS, ctx.getPlayerFacing().getAxis());
	}

	@Override
	protected int getRedstoneOutput(BlockState state) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
		return state.with(POWERED, rsOut > 0);
	}

	@Override
	protected void playPressSound(WorldAccess world, BlockPos pos) {
		if (world instanceof ServerWorld) {
			BlockState bs = world.getBlockState(pos);
			if (bs.isOf(this)) {
				if (bs.get(AXIS) == Axis.X) {
					((ServerWorld)world).spawnParticles(ParticleTypes.CRIT,
							pos.getX()+0.5, pos.getY()+0.1, pos.getZ()+0.25,
							1,
							0.25, 0, 0.1,
							0);
					((ServerWorld)world).spawnParticles(ParticleTypes.CRIT,
							pos.getX()+0.5, pos.getY()+0.1, pos.getZ()+0.75,
							1,
							0.25, 0, 0.1,
							0);
				} else {
					((ServerWorld)world).spawnParticles(ParticleTypes.CRIT,
							pos.getX()+0.25, pos.getY()+0.1, pos.getZ()+0.5,
							1,
							0.1, 0, 0.25,
							0);
					((ServerWorld)world).spawnParticles(ParticleTypes.CRIT,
							pos.getX()+0.75, pos.getY()+0.1, pos.getZ()+0.5,
							1,
							0.1, 0, 0.25,
							0);
				}
			}
		}
		world.playSound(null, pos, YSounds.SMALL_ZAP, SoundCategory.BLOCKS, 0.5f, (world.getRandom().nextFloat()/2)+0.7f);
	}

	@Override
	protected void playDepressSound(WorldAccess world, BlockPos pos) {
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		List<ServerPlayerEntity> players = world.getEntitiesByClass(ServerPlayerEntity.class, BOX.offset(pos), e -> e.isAlive() && !e.isSpectator() && isConductive(e));
		for (ServerPlayerEntity p : players) {
			YCriteria.ACTIVATE_CUPROSTEEL_PLATE.trigger(p);
		}
		return players.isEmpty() ? 0 : 15;
	}

	private boolean isConductive(PlayerEntity e) {
		return e.getEquippedStack(EquipmentSlot.FEET).isIn(YTags.Item.CONDUCTIVE_BOOTS) || Yttr.getWornCoil(e).isPresent();
	}

}
