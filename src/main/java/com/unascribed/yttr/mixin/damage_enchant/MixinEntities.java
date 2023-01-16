package com.unascribed.yttr.mixin.damage_enchant;

import org.spongepowered.asm.mixin.Mixin;

import com.unascribed.yttr.YttrMixin.Transformer;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin({PlayerEntity.class, MobEntity.class})
@Transformer(TransformerEntities.class)
public class MixinEntities {}
