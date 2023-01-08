package com.unascribed.yttr.content.item;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.world.SoulState;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

@EnvironmentInterface(itf=ItemColorProvider.class, value=EnvType.CLIENT)
public class HaemopalItem extends Item implements ItemColorProvider {

	public HaemopalItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!stack.hasNbt() || !stack.getNbt().containsUuid("Owner")) {
			if (entity instanceof PlayerEntity p) {
				stack.getOrCreateNbt().putUuid("Owner", p.getUuid());
				stack.getOrCreateNbt().putString("OwnerName", p.getGameProfile().getName());
			}
		}
		if (!stack.hasNbt() || !stack.getNbt().contains("ID")) {
			stack.getOrCreateNbt().putLong("ID", ThreadLocalRandom.current().nextLong());
		}
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		var world = context.getWorld();
		var pos = context.getBlockPos();
		var is = context.getStack();
		if (is.hasNbt() && is.getNbt().containsUuid("Owner") && world.getBlockState(pos).isOf(YBlocks.POLISHED_SCORCHED_OBSIDIAN_CAPSTONE)) {
			world.playSound(context.getPlayer(), pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1, 1);
			if (!world.isClient && world instanceof ServerWorld sw) {
				world.setBlockState(pos, YBlocks.POLISHED_SCORCHED_OBSIDIAN_HOLSTER.getDefaultState());
				var state = SoulState.get(sw);
				var id = is.getNbt().getUuid("Owner");
				var frag = state.getFragmentation(id);
				state.addFragmentation(id, -1);
				if (this == YItems.BEETOPAL) {
					state.setImpure(id, 10-frag, true);
				}
				is.setCount(0);
			}
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		if (stack.hasNbt() && stack.getNbt().contains("OwnerName")) {
			var mc = MinecraftClient.getInstance();
			if (this == YItems.BEETOPAL) {
				tooltip.add(Text.translatable("tip.yttr.beetopal").formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
			}
			if (mc.player != null && mc.player.getUuid().equals(stack.getNbt().getUuid("Owner"))) {
				if (this != YItems.BEETOPAL) tooltip.add(Text.translatable("tip.yttr.haemopal.self_own").formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
			} else {
				tooltip.add(Text.translatable("tip.yttr.haemopal.owner", stack.getNbt().getString("OwnerName")).formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
			}
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public int getColor(ItemStack itemStack, int i) {
		return this == YItems.BEETOPAL ? -1 : 0xCFFFFF;
	}

}
