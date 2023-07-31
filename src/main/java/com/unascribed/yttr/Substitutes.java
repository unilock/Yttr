package com.unascribed.yttr;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class Substitutes extends SinglePreparationResourceReloader<BiMap<Item, Item>> implements IdentifiableResourceReloadListener {
	
	public static final IdentifiableResourceReloadListener RELOADER = new Substitutes();
	
	private Substitutes() {}

	private static final Gson gson = new Gson();
	private static final BiMap<Item, Item> MAP = HashBiMap.create();
	
	@Override
	public Identifier getFabricId() {
		return Yttr.id("substitutes");
	}
	
	@Override
	protected BiMap<Item, Item> prepare(ResourceManager mgr, Profiler profiler) {
		BiMap<Item, Item> map = HashBiMap.create();
		for (String ns : mgr.getAllNamespaces()) {
			Identifier id = new Identifier(ns, "yttr_substitutes.json");
			var opt = mgr.getResource(id);
			if (opt.isPresent()) {
				try {
					Resource r = opt.get();
					try (InputStreamReader isr = new InputStreamReader(r.open(), Charsets.UTF_8)) {
						JsonObject obj = gson.fromJson(isr, JsonObject.class);
						for (Map.Entry<String, JsonElement> en : obj.entrySet()) {
							String k = en.getKey();
							boolean optionalK = false;
							if (k.endsWith("?")) {
								k = k.substring(0, k.length()-1);
								optionalK = true;
							}
							Identifier kId = new Identifier(k);
							Optional<Item> kI = Registries.ITEM.getOrEmpty(kId);
							if (kI.isPresent()) {
								String v = en.getValue().getAsString();
								boolean optionalV = false;
								if (v.endsWith("?")) {
									v = v.substring(0, v.length()-1);
									optionalV = true;
								}
								Identifier vId = new Identifier(v);
								Optional<Item> vI = Registries.ITEM.getOrEmpty(vId);
								if (vI.isPresent()) {
									if (map.containsKey(kI.get())) {
										if (!optionalK) YLog.warn("While loading "+id+" substitute "+kId+" to prime "+vId+", a mapping already exists for this substitute to prime "+Registries.ITEM.getId(map.get(kI.get()))+" - ignoring this mapping. Add a ? to make it optional and silence this warning.");
									} else if (map.containsValue(vI.get())) {
										if (!optionalV) YLog.warn("While loading "+id+" substitute "+kId+" to prime "+vId+", a mapping already exists for this prime to substitute "+Registries.ITEM.getId(map.inverse().get(vI.get()))+" - ignoring this mapping. Add a ? to make it optional and silence this warning.");
									} else {
										map.put(kI.get(), vI.get());
									}
								} else if (!optionalV) {
									YLog.warn("While loading "+id+" substitute "+kId+", could not find item with ID "+vId+" for prime (add a ? to make it optional and silence this warning)");
								}
							} else if (!optionalK) {
								YLog.warn("While loading "+id+", could not find item with ID "+kId+" for substitute (add a ? to make it optional and silence this warning)");
							}
						}
					}
				} catch (Throwable e) {
					YLog.error("Failed to load "+id, e);
				}
			}
		}
		YLog.info("Loaded "+map.size()+" substitution"+(map.size() == 1 ? "" : "s"));
		return map;
	}
	
	@Override
	protected void apply(BiMap<Item, Item> prepared, ResourceManager manager, Profiler profiler) {
		MAP.clear();
		MAP.putAll(prepared);
	}
	
	public static Set<Item> allPrimes() {
		return MAP.inverse().keySet();
	}
	
	public static Set<Item> allSubstitutes() {
		return MAP.keySet();
	}
	
	public static @Nullable Item getPrime(Item substitute) {
		return MAP.get(substitute);
	}
	
	public static @Nullable Item getSubstitute(Item prime) {
		return MAP.inverse().get(prime);
	}
	
	public static ItemStack sub(ItemStack stack) {
		return copyWithAltItem(stack, getSubstitute(stack.getItem()));
	}

	public static ItemStack prime(ItemStack stack) {
		return copyWithAltItem(stack, getPrime(stack.getItem()));
	}
	
	private static ItemStack copyWithAltItem(ItemStack stack, Item item) {
		if (item == null) return stack.copy();
		ItemStack copy = new ItemStack(item);
		copy.setCount(stack.getCount());
		copy.setNbt(stack.getNbt());
		return copy;
	}
	
}
