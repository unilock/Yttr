package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.util.FakeEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;

public class YAttributes {
    public static final EntityAttribute JUMP_HEIGHT = new FakeEntityAttribute("attribute.name.yttr.jump_height");

    public static void init() {
        Yttr.autoreg.autoRegister(Registries.ENTITY_ATTRIBUTE, YAttributes.class, EntityAttribute.class);
    }
}
