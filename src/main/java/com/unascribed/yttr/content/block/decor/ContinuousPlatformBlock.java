package com.unascribed.yttr.content.block.decor;

import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YGameRules;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;

import com.google.common.base.Ascii;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterface(itf=BlockColorProvider.class, value=EnvType.CLIENT)
public class ContinuousPlatformBlock extends Block implements BlockColorProvider {
	
	public enum Age implements StringIdentifiable {
		IMMORTAL,
		_0,
		_1,
		_2,
		_3
		;
		private final String name;
		Age() {
			if (name().charAt(0) == '_') {
				name = name().substring(1);
			} else {
				name = Ascii.toLowerCase(name());
			}
		}
		@Override
		public String asString() {
			return name;
		}
	}
	
	public enum PlatformLog implements StringIdentifiable {
		AIR(Fluids.EMPTY),
		WATER(Fluids.WATER),
		LAVA(Fluids.LAVA),
		VOID(YFluids.VOID),
		// HACK FOR BLANKETCON
		LIGHT3(Blocks.LIGHT.getDefaultState().with(LightBlock.LEVEL_15, 3).with(LightBlock.WATERLOGGED, false)),
		;
		private final String name;
		public final BlockState block;
		public final Fluid fluid;
		
		PlatformLog(BlockState block, Fluid fluid) {
			name = Ascii.toLowerCase(name());
			this.block = fluid == null ? block : fluid.getDefaultState().getBlockState();
			this.fluid = fluid;
		}
		
		PlatformLog(Fluid fluid) {
			this(null, fluid);
		}
		PlatformLog(BlockState block) {
			this(block, null);
		}
		@Override
		public String asString() {
			return name;
		}
		
		public static PlatformLog by(BlockState bs) {
			var f = bs.getFluidState().getFluid();
			for (PlatformLog pl : values()) {
				if (pl == AIR) continue;
				if (pl.fluid == null ? pl.block == bs : pl.fluid == f) {
					return pl;
				}
			}
			return AIR;
		}
	}
	
	private static final VoxelShape STEPLADDER = VoxelShapes.combine(
				createCuboidShape(0, 0, 0, 16, 8, 16),
				createCuboidShape(0.3, 8, 0.3, 15.7, 16, 15.7),
			BooleanBiFunction.OR);
	
	public static final EnumProperty<Age> AGE = EnumProperty.of("age", Age.class);
	public static final EnumProperty<PlatformLog> LOGGED = EnumProperty.of("logged", PlatformLog.class);
	public static final BooleanProperty SPEEDY = BooleanProperty.of("speedy");
	
	public ContinuousPlatformBlock(Settings settings) {
		super(FabricBlockSettings.copyOf(settings)
				.dropsNothing()
				.luminance(bs -> Math.max(8, bs.get(LOGGED).block.getLuminance())));
		setDefaultState(getDefaultState().with(AGE, Age._0).with(LOGGED, PlatformLog.AIR).with(SPEEDY, false));
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		if (player.getMainHandStack().isOf(YItems.PROJECTOR)) {
			return state.get(AGE) == Age.IMMORTAL ? 0.2f : 1;
		}
		return 0;
	}
	
	@Override
	public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
		return stateFrom.isOf(this) && stateFrom.get(AGE) == state.get(AGE);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(AGE, LOGGED, SPEEDY);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (state.get(SPEEDY)) return STEPLADDER;
		return VoxelShapes.fullCube();
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
		float h1 = (((pos.getX()+pos.getY()+pos.getZ()))/40f)%1;
		if (h1 < 0) h1 += 1;
		return MathHelper.hsvToRgb(h1, 0.3f, 1f);
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		if (state.get(AGE) == Age.IMMORTAL) return;
		if (state.get(AGE) == Age._3) {
			world.setBlockState(pos, state.get(LOGGED).block);
		} else {
			world.setBlockState(pos, state.cycle(AGE));
		}
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(AGE, Age.IMMORTAL).with(LOGGED, PlatformLog.by(ctx.getWorld().getBlockState(ctx.getBlockPos())));
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (state.get(AGE) != Age.IMMORTAL) {
			world.setBlockState(pos, world.getBlockState(pos).with(AGE, Age._0));
		}
		if (state.get(SPEEDY) && entity instanceof LivingEntity le) {
			le.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 10, 2, false, false));
		}
	}
	
	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world, pos, state, player);
		world.playSound(player, pos, YSounds.PROJECT, SoundCategory.BLOCKS, 0.4f, 1.2f+(world.random.nextFloat()*0.3f));
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
		return 0;
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
		BlockPos down = pos.down();
		if (!world.getBlockState(down).isSolidBlock(world, down)) {
			if (random.nextInt((Math.max(0, state.get(AGE).ordinal()-1)*4)+2) == 0) {
				world.addParticle(ParticleTypes.FIREWORK, pos.getX()+random.nextDouble(), pos.getY(), pos.getZ()+random.nextDouble(), 0, -0.05, 0);
			}
		}
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (state.get(AGE) != Age.IMMORTAL) {
			int ticks = world.getGameRules().getInt(YGameRules.PLATFORM_DECAY_TICKS);
			int slew = world.getGameRules().getInt(YGameRules.PLATFORM_DECAY_SLEW);
			world.scheduleBlockTick(pos, this, ticks+(slew <= 0 ? 0 : world.random.nextInt(slew)));
		}
	}
	
	@Override
	public FluidState getFluidState(BlockState state) {
		var l = state.get(LOGGED);
		return l.fluid == null ? Fluids.EMPTY.getDefaultState() : l.fluid.getDefaultState();
	}
	
}
