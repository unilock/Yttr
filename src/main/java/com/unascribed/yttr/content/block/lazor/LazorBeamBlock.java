package com.unascribed.yttr.content.block.lazor;

import com.unascribed.lib39.waypoint.api.AbstractHaloBlockEntity;

import com.unascribed.yttr.init.YDamageTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LazorBeamBlock extends AbstractColoredLazorBlock implements BlockEntityProvider, Waterloggable {

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	
	public LazorBeamBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	protected boolean isEmitter() {
		return false;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? AbstractHaloBlockEntity::tick : null;
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(state, world, pos, entity);
		if (world.isClient) return;
		entity.damage(entity.getDamageSources().create(YDamageTypes.LAZOR), 2);
		if (entity instanceof LivingEntity && ((int)entity.getEyeY()) == pos.getY()) {
			((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 80));
		}
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		Direction facing = state.get(FACING);
		BlockPos opos = pos.offset(facing);
		if (world.getBlockState(opos).isSideSolid(world, opos, facing.getOpposite(), SideShapeType.CENTER)) {
			Vec3d p = Vec3d.ofCenter(pos).add(new Vec3d(facing.getUnitVector()).multiply(0.5));
			int color = state.get(COLOR).baseLitColor;
			float r = ((color>>16)&0xFF)/255f;
			float g = ((color>>8)&0xFF)/255f;
			float b = (color&0xFF)/255f;
			for (int i = 0; i < 2; i++) {
				Particle prt = MinecraftClient.getInstance().particleManager.addParticle(ParticleTypes.FIREWORK,
						p.x+random.nextGaussian()*0.1, p.y+random.nextGaussian()*0.1, p.z+random.nextGaussian()*0.1,
						(random.nextGaussian()*0.025)+(facing.getOffsetX()*-0.05), (random.nextGaussian()*0.025)+0.05+(facing.getOffsetY()*-0.05), (random.nextGaussian()*0.025)+(facing.getOffsetZ()*-0.05));
				if (prt != null) prt.setColor(r, g, b);
			}
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : Fluids.EMPTY.getDefaultState();
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new LazorBeamBlockEntity(pos, state);
	}
}
