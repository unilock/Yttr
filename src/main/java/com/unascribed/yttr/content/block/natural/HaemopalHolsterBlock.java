package com.unascribed.yttr.content.block.natural;

import java.util.ArrayList;
import java.util.List;

import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.world.SoulState;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class HaemopalHolsterBlock extends Block {

	public HaemopalHolsterBlock(Settings settings) {
		super(settings);
	}

	@Override
	public float getHardness() {
		return -1;
	}
	
	@Override
	public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		if (player.getMaxHealth() <= 4) {
			player.sendMessage(Text.translatable("tip.yttr.too_weak.soul").formatted(Formatting.RED, Formatting.ITALIC), true);
			return 0;
		}
		if (player.getHealth() < player.getMaxHealth()) {
			player.sendMessage(Text.translatable("tip.yttr.too_weak.body").formatted(Formatting.YELLOW, Formatting.ITALIC), true);
			return 0;
		}
		float modifier = player.canHarvest(state) ? 30 : 100;
		if (player.age % 20 == 0) {
			player.playSound(SoundEvents.ENTITY_PLAYER_HURT, 0.3f, 0.6f);
			player.limbDistance = 1.5F;
			player.timeUntilRegen = 20;
			player.maxHurtTime = 10;
			player.hurtTime = 6;
			player.knockbackVelocity = -90;
		}
		float hardness = 60;
		hardness += (20-player.getMaxHealth())*5;
		return player.getBlockBreakingSpeed(state) / hardness / modifier;
	}
	
	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		var breaker = builder.getNullable(LootContextParameters.THIS_ENTITY);
		var li = new ArrayList<ItemStack>();
		li.add(new ItemStack(YItems.POLISHED_SCORCHED_OBSIDIAN_CAPSTONE));
		if (breaker instanceof PlayerEntity player && player.getMaxHealth() > 4) {
			var is = new ItemStack(YItems.HAEMOPAL);
			var nbt = is.getOrCreateNbt();
			nbt.putUuid("Owner", player.getUuid());
			nbt.putString("OwnerName", player.getGameProfile().getName());
			nbt.putLong("ID", ThreadLocalRandom.current().nextLong());
			SoulState.get(builder.getWorld()).addFragmentation(player.getUuid());
			li.add(is);
			if (player.getHealth() > 4) player.setHealth(4);
			player.getHungerManager().setSaturationLevel(0);
		}
		return li;
	}
	
}