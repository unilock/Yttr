package com.unascribed.yttr.compat.trinkets;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.unascribed.lib39.util.api.SlotReference;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.Yttr.TrinketsAccess;
import com.unascribed.yttr.init.YTags;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class YttrTrinketsCompat {
	
	public static void init() {
		Yttr.trinketsAccess = new TrinketsAccess() {

			@Override
			public Optional<SlotReference> getWorn(PlayerEntity pe, Predicate<Item> predicate) {
				return TrinketsApi.getTrinketComponent(pe)
						.<Optional<SlotReference>>map(tc -> {
							var eq = tc.getEquipped(is -> predicate.test(is.getItem()));
							if (eq.isEmpty()) return Optional.empty();
							var tref = eq.get(0).getLeft();
							return Optional.of(new SlotReference(tref.inventory(), tref.index()));
						})
						.orElse(Optional.empty());
			}

			@Override
			public int count(PlayerEntity pe, ToIntFunction<ItemStack> func) {
				int accum = 0;
				var opt = TrinketsApi.getTrinketComponent(pe);
				if (opt.isPresent()) {
					for (var pair : opt.get().getAllEquipped()) {
						accum += func.applyAsInt(pair.getRight());
					}
				}
				return accum;
			}

			@Override
			public void dropMagneticTrinkets(PlayerEntity pe) {
				var opt = TrinketsApi.getTrinketComponent(pe);
				if (opt.isPresent()) {
					for (var pair : opt.get().getAllEquipped()) {
						if (pair.getRight().isIn(YTags.Item.MAGNETIC)) {
							ItemEntity ie = pe.dropStack(pair.getLeft().inventory().removeStack(pair.getLeft().index()));
							if (ie != null) ie.setVelocity(0, -1, 0);
						}
					}
				}
			}
		};
	}
	
}
