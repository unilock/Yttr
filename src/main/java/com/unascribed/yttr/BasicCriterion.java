package com.unascribed.yttr;

import com.google.gson.JsonObject;

import net.minecraft.class_5258;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BasicCriterion extends AbstractCriterion<BasicCriterion.Conditions> {

	private final Identifier id;

	public BasicCriterion(String id) {
		this.id = new Identifier(id);
	}

	@Override
	public Identifier getId() {
		return id;
	}
	
	@Override
	protected Conditions conditionsFromJson(JsonObject obj, class_5258 playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new BasicCriterion.Conditions(playerPredicate);
	}
	
	public void trigger(ServerPlayerEntity player) {
		trigger(player, cond -> true);
	}

	public class Conditions extends AbstractCriterionConditions {
		public Conditions(class_5258 player) {
			super(id, player);
		}
	}


}
