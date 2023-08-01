package com.unascribed.yttr.crafting;

import com.google.gson.JsonObject;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeSerializers;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class HaemopalRecipe extends ShapelessRecipe {

	public HaemopalRecipe(Identifier id, String group, ItemStack output, DefaultedList<Ingredient> input) {
		super(id, group, CraftingCategory.MISC, output, input);
	}

	public HaemopalRecipe(ShapelessRecipe copy) {
		this(copy.getId(), copy.getGroup(), copy.getResult(null), copy.getIngredients());
	}
	
	@Override
	public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager mgr) {
		var out = getResult(mgr).copy();
		for (var i = 0; i < inv.size(); i++) {
			var is = inv.getStack(i);
			if (is.isOf(YItems.EMPTY_HAEMOPAL) && is.hasNbt()) {
				out.setNbt(is.getNbt().copy());
				break;
			}
		}
		return out;
	}
	
	@Override
	public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inv) {
		var li = super.getRemainder(inv);
		for (var i = 0; i < inv.size(); i++) {
			var is = inv.getStack(i);
			if (is.isOf(Items.BEETROOT_SOUP)) {
				li.set(i, new ItemStack(Items.BOWL));
			}
		}
		return li;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.HAEMOPAL_CRAFTING;
	}
	
	public static class Serializer extends ShapelessRecipe.Serializer {
		
		@Override
		public ShapelessRecipe read(Identifier identifier, JsonObject jsonObject) {
			return new HaemopalRecipe(super.read(identifier, jsonObject));
		}
		
		@Override
		public ShapelessRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			return new HaemopalRecipe(super.read(identifier, packetByteBuf));
		}
		
		@Override
		public void write(PacketByteBuf packetByteBuf, ShapelessRecipe ShapelessRecipe) {
			super.write(packetByteBuf, ShapelessRecipe);
		}
		
	}
}
