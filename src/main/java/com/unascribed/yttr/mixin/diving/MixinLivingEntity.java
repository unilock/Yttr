package com.unascribed.yttr.mixin.diving;

import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.SuitArmorItem;
import com.unascribed.yttr.init.YCriteria;
import com.unascribed.yttr.mixinsupport.SuitPiecesForJump;
import com.unascribed.yttr.util.EquipmentSlots;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity implements SuitPiecesForJump {

	private int yttr$suitPiecesForJump = 0;
	
	@Inject(at=@At("HEAD"), method="jump")
	public void jumpHead(CallbackInfo ci) {
		int i = 0;
		LivingEntity self = (LivingEntity)(Object)this;
		for (EquipmentSlot slot : EquipmentSlots.ARMOR) {
			ItemStack is = self.getEquippedStack(slot);
			if (is.getItem() instanceof SuitArmorItem) {
				i++;
			}
		}
		yttr$suitPiecesForJump = i;
	}
	
	@Inject(at=@At("TAIL"), method="jump")
	public void jumpTail(CallbackInfo ci) {
		yttr$suitPiecesForJump = 0;
	}
	
	@Override
	public int yttr$getSuitPiecesForJump() {
		return yttr$suitPiecesForJump;
	}
	
	@ModifyVariable(at=@At("HEAD"), method="damage", argsOnly=true, ordinal=0)
	public float modifyDamage(float dmg, DamageSource src) {
		LivingEntity self = (LivingEntity)(Object)this;
		if (Yttr.isWearingFullSuit(self)) {
			if (!src.isTypeIn(DamageTypeTags.BYPASSES_ARMOR)) {
				dmg /= 2;
			}
			if (src.getAttacker() != null && self instanceof ServerPlayerEntity) {
				YCriteria.HIT_WITH_FULL_SUIT.trigger((ServerPlayerEntity)self);
			}
		}
		return dmg;
	}
	
}
