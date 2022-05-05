package com.unascribed.yttr.crafting.ingredient;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class BlockIngredient implements Predicate<Block> {

	private final Set<Block> exacts = Sets.newHashSet();
	private final Set<TagKey<Block>> tags = Sets.newHashSet();
	
	private BlockIngredient() {}
	
	@Override
	public boolean test(Block b) {
		if (exacts.contains(b)) return true;
		for (TagKey<Block> tag : tags) {
			if (b.getRegistryEntry().isIn(tag)) return true;
		}
		return false;
	}
	
	public List<Block> getMatchingBlocks() {
		List<Block> li = Lists.newArrayList();
		li.addAll(exacts);
		for (TagKey<Block> tag : tags) {
			for (RegistryEntry<Block> block : Registry.BLOCK.iterateEntries(tag)) {
				li.add(block.value());
			}
		}
		return li;
	}
	
	public void write(PacketByteBuf out) {
		List<Block> all = getMatchingBlocks();
		out.writeVarInt(all.size());
		for (Block b : all) {
			out.writeVarInt(Registry.BLOCK.getRawId(b));
		}
	}
	
	public static BlockIngredient read(PacketByteBuf in) {
		int amt = in.readVarInt();
		BlockIngredient out = new BlockIngredient();
		for (int i = 0; i < amt; i++) {
			out.exacts.add(Registry.BLOCK.get(in.readVarInt()));
		}
		return out;
	}
	
	public static BlockIngredient fromJson(JsonElement ele) {
		BlockIngredient out = new BlockIngredient();
		if (ele.isJsonArray()) {
			for (JsonElement child : ele.getAsJsonArray()) {
				readInto(out, child);
			}
		} else {
			readInto(out, ele);
		}
		return out;
	}

	private static void readInto(BlockIngredient out, JsonElement ele) {
		if (!ele.isJsonObject()) throw new IllegalArgumentException("Expected object, got "+ele);
		JsonObject obj = ele.getAsJsonObject();
		if (obj.has("block")) {
			out.exacts.add(Registry.BLOCK.get(Identifier.tryParse(obj.get("block").getAsString())));
		} else if (obj.has("tag")) {
			out.tags.add(TagKey.of(Registry.BLOCK_KEY, Identifier.tryParse(obj.get("tag").getAsString())));
		} else {
			throw new IllegalArgumentException("Don't know how to parse "+ele+" without a block or tag value");
		}
	}
	
}
