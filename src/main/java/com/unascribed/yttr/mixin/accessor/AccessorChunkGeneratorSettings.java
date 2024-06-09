package com.unascribed.yttr.mixin.accessor;

import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.surfacebuilder.SurfaceRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkGeneratorSettings.class)
public interface AccessorChunkGeneratorSettings {
    @Accessor("surfaceRule")
    @Final
    @Mutable
    void yttr$setSurfaceRule(SurfaceRules.MaterialRule value);
}
