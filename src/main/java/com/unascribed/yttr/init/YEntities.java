package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.entity.RifleDummyEntity;
import com.unascribed.yttr.content.entity.SlippingTransfungusEntity;
import com.unascribed.yttr.content.entity.ThrownGlowingGasEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;

public class YEntities {

	public static final EntityType<RifleDummyEntity> RIFLE_DUMMY = EntityType.Builder.<RifleDummyEntity>create(SpawnGroup.MISC)
			.disableSaving()
			.disableSummon()
			.build("yttr:rifle_dummy");

	public static final EntityType<SlippingTransfungusEntity> SLIPPING_TRANSFUNGUS = EntityType.Builder.<SlippingTransfungusEntity>create(SlippingTransfungusEntity::new, SpawnGroup.MISC)
			.setDimensions(0.98f, 0.98f)
			.maxTrackingRange(10)
			.trackingTickInterval(20)
			.build("yttr:slipping_transfungus");

	public static final EntityType<ThrownGlowingGasEntity> THROWN_GLOWING_GAS = EntityType.Builder.<ThrownGlowingGasEntity>create(ThrownGlowingGasEntity::new, SpawnGroup.MISC)
			.setDimensions(0.25F, 0.25F)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.build("yttr:thrown_glowing_gas");
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.ENTITY_TYPE, YEntities.class, EntityType.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Renderer {
		String value();
	}
	
}
