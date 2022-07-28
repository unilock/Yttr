package com.unascribed.yttr.mixin.substitute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.unascribed.yttr.Substitutes;

import com.google.common.collect.Iterables;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

@Mixin(Tag.class)
public class MixinTag {

	@SuppressWarnings("unchecked")
	@ModifyVariable(at=@At("HEAD"), method="<init>", argsOnly=true, ordinal=0)
	private static Collection<?> replaceValues(Collection<?> values) {
		if (!values.isEmpty() && Iterables.all(values, i -> i instanceof Item)) {
			var vi = (Collection<? extends Item>)values;
			if (Iterables.any(vi, i -> Substitutes.getSubstitute(i) != null)) {
				List<Item> res = new ArrayList<>();
				for (Item i : vi) {
					res.add(i);
					Item subst = Substitutes.getSubstitute(i);
					if (subst != null) {
						res.add(subst);
					}
				}
				return res;
			}
		}
		return values;
	}
	
}
