package com.unascribed.yttr;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.unmapped.C_ctsfmifk;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PlacedBlockCriterion extends AbstractCriterion<PlacedBlockCriterion.Conditions> {
	static final Identifier ID = new Identifier("placed_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public PlacedBlockCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, C_ctsfmifk extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), name -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + name + ":");
			});
		}

		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new PlacedBlockCriterion.Conditions(extended, block, statePredicate, locationPredicate, itemPredicate);
	}

	@Nullable
	private static Block getBlock(JsonObject obj) {
		if (obj.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
			return Registries.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void trigger(ServerPlayerEntity player, BlockPos blockPos, ItemStack stack) {
		BlockState blockState = player.getWorld().getBlockState(blockPos);
		this.trigger(player, conditions -> conditions.matches(blockState, blockPos, player.getServerWorld(), stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final Block block;
		private final StatePredicate state;
		private final LocationPredicate location;
		private final ItemPredicate item;

		public Conditions(C_ctsfmifk player, @Nullable Block block, StatePredicate state, LocationPredicate location, ItemPredicate item) {
			super(PlacedBlockCriterion.ID, player);
			this.block = block;
			this.state = state;
			this.location = location;
			this.item = item;
		}

		public static PlacedBlockCriterion.Conditions block(Block block) {
			return new PlacedBlockCriterion.Conditions(C_ctsfmifk.field_24388, block, StatePredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(BlockState state, BlockPos pos, ServerWorld world, ItemStack stack) {
			if (this.block != null && !state.isOf(this.block)) {
				return false;
			} else if (!this.state.test(state)) {
				return false;
			} else if (!this.location.test(world, pos.getX(), pos.getY(), pos.getZ())) {
				return false;
			} else {
				return this.item.test(stack);
			}
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			if (this.block != null) {
				jsonObject.addProperty("block", Registries.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.toJson());
			jsonObject.add("location", this.location.toJson());
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
