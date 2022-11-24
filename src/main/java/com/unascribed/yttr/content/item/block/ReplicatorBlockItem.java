package com.unascribed.yttr.content.item.block;

import java.util.List;

import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixin.accessor.AccessorDispenserBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ReplicatorBlockItem extends BlockItem {

	public ReplicatorBlockItem(Block block, Settings settings) {
		super(block, settings);
		DispenserBlock.registerBehavior(this, (pointer, stack) -> {
			ItemStack inside = ReplicatorBlockItem.getHeldItem(stack);
			Block b = pointer.getBlockState().getBlock();
			if (b instanceof AccessorDispenserBlock) {
				((AccessorDispenserBlock)b).yttr$getBehaviorForItem(inside).dispense(pointer, inside);
			}
			return stack;
		});
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		ItemStack held = getHeldItem(stack);
		if (held.isEmpty()) return;
		List<Text> inner = held.getTooltip(MinecraftClient.getInstance().player, context);
		for (int i = 0; i < inner.size(); i++) {
			tooltip.add(Text.literal("  ").append(inner.get(i)));
		}
		tooltip.add(Text.literal(""));
	}
	
	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}
	
	@Override
	protected SoundEvent getPlaceSound(BlockState state) {
		return YSounds.SILENCE;
	}
	
	@Override
	public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
		if (clickType == ClickType.RIGHT) {
			ItemStack held = getHeldItem(stack);
			if (!held.isEmpty()) {
				if (cursorStackReference.get().isEmpty()) {
					player.playSound(YSounds.REPLICATOR_VEND, 1, 1);
					cursorStackReference.set(held.copy());
					return true;
				}
			} else if (!cursorStackReference.get().isEmpty()) {
				if (isLocked(stack)) {
					player.playSound(YSounds.REPLICATOR_REFUSE, 1, 0.75f);
					player.playSound(YSounds.REPLICATOR_REFUSE, 1, 0.6f);
				} else {
					player.playSound(YSounds.REPLICATOR_UPDATE, 1, 1.25f);
					setHeldItem(stack, otherStack.copy());
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean isLocked(ItemStack stack) {
		NbtCompound entityTag = stack.getSubNbt("BlockEntityTag");
		if (entityTag != null) {
			return entityTag.getBoolean("Locked");
		}
		return false;
	}
	
	public static ItemStack getHeldItem(ItemStack stack) {
		NbtCompound entityTag = stack.getSubNbt("BlockEntityTag");
		if (entityTag != null) {
			return ItemStack.fromNbt(entityTag.getCompound("Item"));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setHeldItem(ItemStack stack, ItemStack held) {
		stack.getOrCreateSubNbt("BlockEntityTag").put("Item", held.writeNbt(new NbtCompound()));
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		ItemStack held = getHeldItem(stack);
		if (held.isFood() && user.canConsume(held.getItem().getFoodComponent().isAlwaysEdible())) {
			user.setCurrentHand(hand);
			return TypedActionResult.consume(stack);
		} else if (held.getItem() instanceof ThrowablePotionItem) {
			user.setStackInHand(hand, held);
			TypedActionResult<ItemStack> res = held.getItem().use(world, user, hand);
			user.setStackInHand(hand, stack);
			return new TypedActionResult<>(res.getResult(), stack);
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		ItemStack stack = context.getStack();
		ItemStack held = getHeldItem(stack);
		if (!context.getPlayer().isSneaking() && (held.isFood() || held.getItem() instanceof ThrowablePotionItem)) {
			return ActionResult.PASS;
		}
		return super.useOnBlock(context);
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack) {
		ItemStack held = getHeldItem(stack);
		return held.getMaxUseTime();
	}
	
	@Override
	public UseAction getUseAction(ItemStack stack) {
		ItemStack held = getHeldItem(stack);
		return held.getUseAction();
	}
	
	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack held = getHeldItem(stack);
		held.copy().finishUsing(world, user);
		return stack;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		ItemStack held = getHeldItem(stack);
		held.copy().onStoppedUsing(world, user, remainingUseTicks);
	}
	
}
