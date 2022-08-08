package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.entity.RifleDummyEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class YEntities {

	public static final EntityType<RifleDummyEntity> RIFLE_DUMMY = EntityType.Builder.<RifleDummyEntity>create(SpawnGroup.MISC)
			.disableSaving()
			.disableSummon()
			.build("yttr:rifle_dummy");
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registry.ENTITY_TYPE, YEntities.class, EntityType.class);
	}

	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Renderer {
		String value();
	}
	
}
