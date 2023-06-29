package com.unascribed.yttr.mechanics;

import com.unascribed.yttr.init.YDamageTypes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public class SolventDamageSource extends DamageSource {

	public final int i;
	
	public SolventDamageSource(World world, int i) {
		super(world.getDamageSources().registry.getHolderOrThrow(YDamageTypes.SOLVENT));
		this.i = i;
	}

}
