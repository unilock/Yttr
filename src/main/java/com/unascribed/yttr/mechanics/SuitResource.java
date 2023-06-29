package com.unascribed.yttr.mechanics;

import com.google.common.collect.ImmutableList;

import com.unascribed.yttr.init.YDamageTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public enum SuitResource {
	FUEL {
		@Override
		public int getConsumptionPerBlock(int pressure) {
			return 5;
		}
		@Override
		public int getConsumptionPerTick(int pressure) {
			return 0;
		}
		@Override
		public int getMaximum() {
			return 40000;
		}
		@Override
		public int getSpeedDivider(boolean depleted) {
			return depleted ? 4 : 1;
		}
	},
	OXYGEN {
		@Override
		public int getConsumptionPerBlock(int pressure) {
			return 2*((pressure+699)/700);
		}
		@Override
		public int getConsumptionPerTick(int pressure) {
			return 5*((pressure+699)/700);
		}
		@Override
		public int getMaximum() {
			return 80000;
		}
		@Override
		public void applyDepletedEffect(ServerPlayerEntity player) {
			if (player.age % 10 == 0) {
				player.damage(player.getDamageSources().create(YDamageTypes.SUIT_SUFFOCATION), 1);
			}
		}
	},
	INTEGRITY {
		@Override
		public int getConsumptionPerBlock(int pressure) {
			return 0;
		}
		@Override
		public int getConsumptionPerTick(int pressure) {
			if (pressure < 200) return 0;
			return pressure/70;
		}
		@Override
		public int getMaximum() {
			return 60000;
		}
		@Override
		public int getDefaultAmount() {
			return getMaximum();
		}
		@Override
		public void applyDepletedEffect(ServerPlayerEntity player) {
			player.damage(player.getDamageSources().create(YDamageTypes.SUIT_INTEGRITY_FAILURE), player.getHealth()*6);
		}
	},
	;
	
	public static final ImmutableList<SuitResource> VALUES = ImmutableList.copyOf(values());
	
	public abstract int getConsumptionPerTick(int pressure);
	public abstract int getConsumptionPerBlock(int pressure);
	public abstract int getMaximum();
	public int getDefaultAmount() { return 0; }
	
	public void applyDepletedEffect(ServerPlayerEntity player) {}
	public int getSpeedDivider(boolean depleted) { return 1; }
}
