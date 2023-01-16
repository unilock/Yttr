package com.unascribed.yttr.mixin.platforms;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.unascribed.yttr.Yttr;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

@Mixin(Entity.class)
public class MixinEntity {

	@ModifyVariable(at=@At("HEAD"), method="adjustSingleAxisMovementForCollisions", ordinal=0)
	private static List<VoxelShape> yttr$addPlatformsCollision(List<VoxelShape> shapes, @Nullable Entity entity) {
		if (entity instanceof PlayerEntity pe && pe.isSneaking() && Yttr.isWearingPlatforms(pe)) {
			shapes = Lists.newArrayList(shapes);
			var box = pe.getBoundingBox();
			var center = box.getCenter();
			double x = center.x;
			double y = box.minY;
			double z = center.z;
			shapes.add(VoxelShapes.cuboid(x-4, y-0.05, z-4, x+4, y, z+4));
			return shapes;
		}
		return shapes;
	}
	
}
