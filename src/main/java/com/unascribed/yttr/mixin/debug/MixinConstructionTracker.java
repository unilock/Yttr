package com.unascribed.yttr.mixin.debug;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.yttr.mixinsupport.Blameable;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.nbt.LootNbtProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.score.LootScoreProviderType;
import net.minecraft.particle.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.poi.PointOfInterestType;

@Mixin({
	SoundEvent.class,
	Fluid.class,
	StatusEffect.class,
	Block.class,
	Enchantment.class,
	EntityType.class,
	Item.class,
	Potion.class,
	ParticleType.class,
	BlockEntityType.class,
	PaintingMotive.class,
	Identifier.class,
	ChunkStatus.class,
	ScreenHandlerType.class,
	EntityAttribute.class,
	GameEvent.class,
	StatType.class,
	VillagerType.class,
	VillagerProfession.class,
	PointOfInterestType.class,
	MemoryModuleType.class,
	SensorType.class,
	Schedule.class,
	Activity.class,
	LootPoolEntryType.class,
	LootFunctionType.class,
	LootConditionType.class,
	LootNumberProviderType.class,
	LootNbtProviderType.class,
	LootScoreProviderType.class
})
public class MixinConstructionTracker implements Blameable {

	private static final boolean yttr$debugRegistration = Boolean.getBoolean("yttr.debugRegistration");
	
	// Xaero's Minimap likes to serialize things it shouldn't
	private transient Throwable yttr$blame;
	
	@Inject(at=@At("RETURN"), method="<init>", allow=900)
	public void onConstruct(CallbackInfo ci) {
		if (yttr$debugRegistration) {
			yttr$blame = new Throwable("Object was constructed here");
		}
	}

	@Override
	public Throwable yttr$getConstructionBlame() {
		return yttr$blame;
	}
	
}
