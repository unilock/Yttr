package com.unascribed.yttr.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class AdventureHelper {

	public static boolean canUse(LivingEntity user, ItemStack stack, World world, Vec3i pos) {
		return canUse(user, stack, world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5);
	}
	
	public static boolean canUse(LivingEntity user, ItemStack stack, World world, Vec3d pos) {
		return canUse(user, stack, world, pos.x, pos.y, pos.z);
	}
	
	public static boolean canUse(LivingEntity user, ItemStack stack, World world, double x, double y, double z) {
		if (user instanceof PlayerEntity player) {
			if (player.canModifyBlocks()) return true;
			boolean anyChecks = false;
			boolean anyObjections = false;
			if (stack.hasNbt() && stack.getNbt().contains("CanUseInDim", NbtElement.STRING_TYPE)) {
				anyChecks = true;
				anyObjections |= !world.getRegistryKey().getValue().toString().equals(stack.getNbt().getString("CanUseInDim"));
			}
			if (stack.hasNbt() && stack.getNbt().contains("CanUseInBox", NbtElement.INT_ARRAY_TYPE)) {
				anyChecks = true;
				int[] arr = stack.getNbt().getIntArray("CanUseInBox");
				if (arr.length != 6) return false;
				int minX = Math.min(arr[0], arr[3]);
				int minY = Math.min(arr[1], arr[4]);
				int minZ = Math.min(arr[2], arr[5]);
				int maxX = Math.max(arr[0], arr[3]);
				int maxY = Math.max(arr[1], arr[4]);
				int maxZ = Math.max(arr[2], arr[5]);
				anyObjections |= !(x >= minX && x < maxX && y >= minY && y < maxY && z >= minZ && z < maxZ);
			}
			return anyChecks && !anyObjections;
		} else {
			return true; // I guess???
		}
	}
	
}
