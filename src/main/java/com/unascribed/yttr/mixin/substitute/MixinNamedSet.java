package com.unascribed.yttr.mixin.substitute;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.registry.Holder;
import net.minecraft.registry.HolderSet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.Substitutes;

import com.google.common.collect.Iterables;

import net.minecraft.item.Item;

@Mixin(HolderSet.NamedSet.class)
public class MixinNamedSet {

	@SuppressWarnings("unchecked")
	@ModifyVariable(at=@At("HEAD"), method="setContents", argsOnly=true, ordinal=0)
	private List<? extends Holder<?>> replaceValues(List<? extends Holder<?>> values) {
		if (!values.isEmpty() && Iterables.all(values, h -> h.getKey().map(k -> k.getRegistry().equals(RegistryKeys.ITEM)).orElse(false))) {
			var vi = (List<Holder<Item>>)values;
			if (Iterables.any(vi, i -> Substitutes.getSubstitute(i.value()) != null)) {
				List<Holder<Item>> res = new ArrayList<>();
				for (Holder<Item> i : vi) {
					res.add(i);
					Item subst = Substitutes.getSubstitute(i.value());
					if (subst != null) {

						res.add(Holder.Reference.create(Registries.ITEM.asHolderOwner(), Registries.ITEM.getKey(subst).get()));
					}
				}
				return res;
			}
		}
		return values;
	}
	
}
