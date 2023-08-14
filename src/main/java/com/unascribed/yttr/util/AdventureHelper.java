package com.unascribed.yttr.util;

import org.spongepowered.include.com.google.common.base.Strings;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class AdventureHelper {

	public static boolean canUse(LivingEntity user, ItemStack stack, World world, Vec3i pos) {
		return _canUse(user, null, stack, world, pos, false);
	}
	
	public static boolean canUse(LivingEntity user, ItemStack stack, World world, Vec3d pos) {
		return _canUse(user, null, stack, world, pos, false);
	}
	
	public static boolean canUse(LivingEntity user, ItemStack stack, World world, double x, double y, double z) {
		return _canUse(user, null, stack, world, x, y, z, false);
	}

	
	public static boolean canUseLoose(LivingEntity user, ItemStack stack, World world, Vec3i pos) {
		return _canUse(user, null, stack, world, pos, true);
	}
	
	public static boolean canUseLoose(LivingEntity user, ItemStack stack, World world, Vec3d pos) {
		return _canUse(user, null, stack, world, pos, true);
	}
	
	public static boolean canUseLoose(LivingEntity user, ItemStack stack, World world, double x, double y, double z) {
		return _canUse(user, null, stack, world, x, y, z, true);
	}
	

	public static boolean canUse(LivingEntity user, String prefix, ItemStack stack, World world, Vec3i pos) {
		return _canUse(user, prefix, stack, world, pos, false);
	}
	
	public static boolean canUse(LivingEntity user, String prefix, ItemStack stack, World world, Vec3d pos) {
		return _canUse(user, prefix, stack, world, pos, false);
	}
	
	public static boolean canUse(LivingEntity user, String prefix, ItemStack stack, World world, double x, double y, double z) {
		return _canUse(user, prefix, stack, world, x, y, z, false);
	}

	
	public static boolean canUseLoose(LivingEntity user, String prefix, ItemStack stack, World world, Vec3i pos) {
		return _canUse(user, prefix, stack, world, pos, true);
	}
	
	public static boolean canUseLoose(LivingEntity user, String prefix, ItemStack stack, World world, Vec3d pos) {
		return _canUse(user, prefix, stack, world, pos, true);
	}
	
	public static boolean canUseLoose(LivingEntity user, String prefix, ItemStack stack, World world, double x, double y, double z) {
		return _canUse(user, prefix, stack, world, x, y, z, true);
	}

	
	private static boolean _canUse(LivingEntity user, String prefix, ItemStack stack, World world, Vec3i pos, boolean loose) {
		return _canUse(user, prefix, stack, world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, loose);
	}

	private static boolean _canUse(LivingEntity user, String prefix, ItemStack stack, World world, Vec3d pos, boolean loose) {
		return _canUse(user, prefix, stack, world, pos.x, pos.y, pos.z, loose);
	}
	
	private static boolean _canUse(LivingEntity user, String prefix, ItemStack stack, World world, double x, double y, double z, boolean loose) {
		prefix = Strings.nullToEmpty(prefix);
		if (user instanceof PlayerEntity player) {
			boolean anyChecks = loose || player.canModifyBlocks();
			boolean anyObjections = false;
			if (stack.hasNbt() && stack.getNbt().contains("CanUse"+prefix+"InDim", NbtElement.STRING_TYPE)) {
				anyChecks = true;
				anyObjections |= !world.getRegistryKey().getValue().toString().equals(stack.getNbt().getString("CanUse"+prefix+"InDim"));
			}
			if (stack.hasNbt() && stack.getNbt().contains("CanUse"+prefix+"InBox", NbtElement.INT_ARRAY_TYPE)) {
				anyChecks = true;
				int[] arr = stack.getNbt().getIntArray("CanUse"+prefix+"InBox");
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
