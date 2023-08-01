package com.unascribed.yttr.mixin.curse;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.mechanics.ShatteringLogic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(Block.class)
public class MixinBlock {
	
	@Inject(at=@At("HEAD"), method="dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
			cancellable=true)
	private static void dropStacksHead(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		if (EnchantmentHelper.getLevel(YEnchantments.ANNIHILATION_CURSE, stack) > 0) {
			ci.cancel();
			return;
		}
		ShatteringLogic.shatteringDepth = 0;
		ShatteringLogic.isShattering = EnchantmentHelper.getLevel(YEnchantments.SHATTERING_CURSE, stack) > 0;
	}

	@Inject(at=@At("RETURN"), method="dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
	private static void dropStacksTail(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
		ShatteringLogic.isShattering = false;
	}
	
	@Inject(at=@At(value="INVOKE", target="net/minecraft/entity/ItemEntity.setToDefaultPickupDelay()V", shift=Shift.AFTER),
			method="dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V", cancellable=true,
			locals=LocalCapture.CAPTURE_FAILHARD)
	private static void dropStack(World world, Supplier<ItemEntity> sup, ItemStack stack, CallbackInfo ci, ItemEntity entity) {
		if (ShatteringLogic.isShattering) {
			if (ShatteringLogic.shatteringDepth > 0) {
				entity.setVelocity(entity.getPos().subtract(Vec3d.ofCenter(entity.getBlockPos())).normalize().multiply(0.2));
				if (ThreadLocalRandom.current().nextInt(10*ShatteringLogic.shatteringDepth) != 0) {
					return;
				}
			}
			for (int j = 0; j < stack.getCount(); j++) {
				ItemStack copy1 = stack.copy();
				copy1.setCount(1);
				ShatteringLogic.inv.setStack(0, copy1);
				Optional<? extends Recipe<RecipeInputInventory>> recipe = world.getRecipeManager().getFirstMatch(YRecipeTypes.SHATTERING, ShatteringLogic.inv, world);
				List<ItemStack> remainder = null;
				if (!recipe.isPresent()) {
					recipe = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, ShatteringLogic.inv, world);
					remainder = world.getRecipeManager().getRemainingStacks(RecipeType.CRAFTING, ShatteringLogic.inv, world);
				}
				if (!recipe.isPresent()) {
					for (StonecuttingRecipe sr : world.getRecipeManager().listAllOfType(RecipeType.STONECUTTING)) {
						if (sr.getResult(world.getRegistryManager()).getCount() == 1 && ItemStack.areEqual(sr.getResult(world.getRegistryManager()), copy1)) {
							ItemStack input = new ItemStack(Item.byRawId(sr.getIngredients().get(0).getMatchingItemIds().getInt(0)));
							recipe = Optional.of(new ShapelessRecipe(sr.getId(), null,
									CraftingCategory.MISC, input, DefaultedList.ofSize(1, Ingredient.ofStacks(sr.getResult(world.getRegistryManager())))));
							remainder = null;
							break;
						}
					}
				}
				if (!recipe.isPresent()) return;
				ItemStack result = recipe.get().craft(ShatteringLogic.inv, world.getRegistryManager());
				try {
					ShatteringLogic.shatteringDepth++;
					for (int i = 0; i < result.getCount(); i++) {
						ItemStack copy = result.copy();
						copy.setCount(1);
						dropStack(world, entity.getBlockPos(), copy);
					}
					if (remainder != null) {
						for (ItemStack is : remainder) {
							for (int i = 0; i < is.getCount(); i++) {
								ItemStack copy = is.copy();
								copy.setCount(1);
								dropStack(world, entity.getBlockPos(), copy);
							}
						}
					}
					ci.cancel();
				} finally {
					ShatteringLogic.shatteringDepth--;
				}
			}
		}
	}
	
	@Shadow
	public static void dropStack(World world, BlockPos pos, ItemStack stack) {}
	
}
