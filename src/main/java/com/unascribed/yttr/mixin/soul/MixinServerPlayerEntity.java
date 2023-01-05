package com.unascribed.yttr.mixin.soul;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.world.SoulState;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

	private int yttr$lastImpurity = 0;
	
	@Inject(at=@At("TAIL"), method="tick")
	public void yttr$tick(CallbackInfo ci) {
		var self = (ServerPlayerEntity)(Object)this;
		int impurity = SoulState.get(self.getWorld()).getFragmentation(self.getUuid());
		if (impurity != yttr$lastImpurity) {
			yttr$lastImpurity = impurity;
			var inst = self.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
			inst.removeModifier(SoulState.MAX_HEALTH_MODIFIER);
			inst.addTemporaryModifier(new EntityAttributeModifier(SoulState.MAX_HEALTH_MODIFIER,
					"Yttr soul impurity debuff", -(impurity*2), Operation.ADDITION));
			if (self.getHealth() > self.getMaxHealth()) {
				self.setHealth(self.getMaxHealth());
			}
		}
	}
	
}
