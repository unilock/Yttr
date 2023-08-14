package com.unascribed.yttr.mixin.soul;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.network.MessageS2CSoulImpurity;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.world.SoulState;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

	private int yttr$lastFragmentation = -1;
	private int yttr$lastImpurity = -1;
	
	@Inject(at=@At("TAIL"), method="tick")
	public void yttr$tick(CallbackInfo ci) {
		var self = (ServerPlayerEntity)(Object)this;
		boolean changed = false;
		
		int fragmentation = SoulState.get(self.getServerWorld()).getFragmentation(self.getUuid());
		if (fragmentation != yttr$lastFragmentation) {
			yttr$lastFragmentation = fragmentation;
			var inst = self.getAttributes().createIfAbsent(EntityAttributes.GENERIC_MAX_HEALTH);
			inst.removeModifier(SoulState.FRAGMENTATION_MODIFIER);
			inst.addTemporaryModifier(new EntityAttributeModifier(SoulState.FRAGMENTATION_MODIFIER,
					"Yttr soul fragmentation debuff", -(fragmentation*2), Operation.ADDITION));
			changed = true;
		}
		
		int impurity = SoulState.get(self.getServerWorld()).getImpurityMask(self.getUuid());
		if (impurity != yttr$lastImpurity) {
			yttr$lastImpurity = impurity;
			var inst = self.getAttributes().createIfAbsent(EntityAttributes.GENERIC_MAX_HEALTH);
			inst.removeModifier(SoulState.IMPURITY_MODIFIER);
			inst.addTemporaryModifier(new EntityAttributeModifier(SoulState.IMPURITY_MODIFIER,
					"Yttr soul impurity debuff", -Integer.bitCount(impurity), Operation.ADDITION));
			changed = true;
			try {
				new MessageS2CSoulImpurity(impurity).sendTo(self);
			} catch (Throwable t) {
				YLog.warn("Error while trying to sync soul status", t);
			}
		}
		
		if (changed && self.getHealth() > self.getMaxHealth()) {
			self.setHealth(self.getMaxHealth());
		}
	}
	
}
