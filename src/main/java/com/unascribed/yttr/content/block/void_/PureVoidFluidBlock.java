package com.unascribed.yttr.content.block.void_;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mechanics.SolventDamageSource;

import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PureVoidFluidBlock extends VoidFluidBlock {
	
	public PureVoidFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.isAlive()) return;
		int i = 2;
		if (entity instanceof LivingEntity) {
			LivingEntity le = (LivingEntity)entity;
			DamageSource recent = le.getRecentDamageSource();
			if (recent instanceof SolventDamageSource) {
				i = ((SolventDamageSource) recent).i+2;
				if (le.timeUntilRegen > 10) return;
			}
		}
		if (entity.damage(new SolventDamageSource(world, i), 2*i)) {
			if (entity instanceof LivingEntity) {
				LivingEntity le = (LivingEntity)entity;
				for (EquipmentSlot es : EquipmentSlot.values()) {
					if (es.getType() == Type.ARMOR) {
						le.getEquippedStack(es).damage(3*i, le, (e) -> {
							le.sendEquipmentBreakStatus(es);
						});
					}
				}
			}
			world.playSound(null, entity.getPos().x, entity.getPos().y, entity.getPos().z, YSounds.DISSOLVE, SoundCategory.BLOCKS, 1, 0.8f+(i*0.1f));
		}
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !getFluidState(state).isSource();
	}
	
	@Override
	public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
	
}
