package com.unascribed.yttr.crafting;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.content.block.decor.LampBlock;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeSerializers;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.mixin.accessor.AccessorShapedRecipe;
import com.unascribed.yttr.util.Resolvable;

import com.google.common.collect.Lists;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingCategory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class LampRecipe extends ShapedRecipe {

	private final List<String> stripTags = Lists.newArrayList();
	private boolean misc, important;
	
	public LampRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
		super(id, group, CraftingCategory.REDSTONE, width, height, ingredients, output);
	}
	
	public LampRecipe(ShapedRecipe copy) {
		this(copy.getId(), ((AccessorShapedRecipe)copy).yttr$getGroup(), copy.getWidth(), copy.getHeight(), copy.getIngredients(), copy.getResult(null));
	}

	@Override
	public boolean matches(RecipeInputInventory inv, World world) {
		return super.matches(inv, world) && !craft(inv, world.getRegistryManager()).isEmpty();
	}
	
	@Override
	public boolean isIgnoredInRecipeBook() {
		return !important;
	}

	public int getPriority() {
		if (important) {
			return misc ? 2 : 3;
		}
		return misc ? 0 : 1;
	}
	
	@Override
	public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager mgr) {
		ItemStack stack = getResult(mgr).copy();
		boolean containsTorch = false;
		Boolean inputLampInverted = null;
		LampColor inputLampColor = null;
		LampColor color = null;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack in = inv.getStack(i);
			if (in.getItem() == Items.REDSTONE_TORCH) {
				containsTorch = true;
			} else if (in.getItem() instanceof BlockItem && ((BlockItem)in.getItem()).getBlock() instanceof LampBlock) {
				boolean thisInverted = LampBlockItem.isInverted(in);
				LampColor thisColor = LampBlockItem.getColor(in);
				if (inputLampInverted != null && inputLampInverted != thisInverted) return ItemStack.EMPTY;
				if (inputLampColor != null && inputLampColor != thisColor) return ItemStack.EMPTY;
				inputLampColor = thisColor;
				inputLampInverted = thisInverted;
			} else {
				Item item = in.getItem();
				LampColor thisColor = null;
				if (item instanceof DyeItem) {
					thisColor = LampColor.BY_DYE.get(((DyeItem)item).getColor());
				} else {
					thisColor = LampColor.BY_ITEM.get(Resolvable.mapKey(item, Registries.ITEM));
				}
				if (color != null && color != thisColor) return ItemStack.EMPTY;
				if (thisColor != null) color = thisColor;
			}
		}
		if (stack.getItem() != YItems.LAZOR_EMITTER) {
			LampBlockItem.setInverted(stack, inputLampInverted == null ? containsTorch : inputLampInverted ^ containsTorch);
		}
		LampBlockItem.setColor(stack, color == null ? inputLampColor == null ? LampColor.COLORLESS : inputLampColor : color);
		for (String s : stripTags) {
			stack.getNbt().remove(s);
		}
		return stack;
	}

	public void addStripTag(String tag) {
		stripTags.add(tag);
	}
	
	public List<String> getStripTags() {
		return stripTags;
	}
	
	public boolean isMisc() {
		return misc;
	}
	
	public void setMisc(boolean misc) {
		this.misc = misc;
	}
	
	public boolean isImportant() {
		return important;
	}
	
	public void setImportant(boolean important) {
		this.important = important;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return YRecipeSerializers.LAMP_CRAFTING;
	}
	
	public static class Serializer extends ShapedRecipe.Serializer {
		
		@Override
		public ShapedRecipe read(Identifier identifier, JsonObject jsonObject) {
			LampRecipe lr = new LampRecipe(super.read(identifier, jsonObject));
			if (jsonObject.has("yttr:strip_tags")) {
				for (JsonElement je : jsonObject.get("yttr:strip_tags").getAsJsonArray()) {
					lr.addStripTag(je.getAsString());
				}
			}
			if (jsonObject.has("yttr:misc")) {
				lr.setMisc(jsonObject.get("yttr:misc").getAsBoolean());
			}
			if (jsonObject.has("yttr:important")) {
				lr.setImportant(jsonObject.get("yttr:important").getAsBoolean());
			}
			return lr;
		}
		
		@Override
		public ShapedRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
			LampRecipe lr = new LampRecipe(super.read(identifier, packetByteBuf));
			if (packetByteBuf.isReadable()) {
				int count = packetByteBuf.readVarInt();
				for (int i = 0; i < count; i++) {
					lr.addStripTag(packetByteBuf.readString(32767));
				}
				lr.setMisc(packetByteBuf.readBoolean());
				lr.setImportant(packetByteBuf.readBoolean());
			}
			return lr;
		}
		
		@Override
		public void write(PacketByteBuf packetByteBuf, ShapedRecipe shapedRecipe) {
			super.write(packetByteBuf, shapedRecipe);
			if (shapedRecipe instanceof LampRecipe) {
				LampRecipe lr = (LampRecipe)shapedRecipe;
				packetByteBuf.writeVarInt(lr.getStripTags().size());
				for (String tag : lr.getStripTags()) {
					packetByteBuf.writeString(tag);
				}
				packetByteBuf.writeBoolean(lr.isMisc());
				packetByteBuf.writeBoolean(lr.isImportant());
			}
		}
		
	}

}
