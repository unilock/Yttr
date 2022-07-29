package com.unascribed.yttr.compat.emi;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.SecretShapedRecipe;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.mixinsupport.ItemGroupParent;
import com.unascribed.yttr.mixinsupport.SubTabLocation;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class YttrEmiPlugin implements EmiPlugin {

	@Override
	public void register(EmiRegistry registry) {
		registry.addRecipe(EmiWorldInteractionRecipe.builder()
			.id(Yttr.id("stripping/squeeze_log"))
			.leftInput(EmiStack.of(YBlocks.SQUEEZE_LOG))
			.rightInput(EmiStack.of(Items.IRON_AXE), true)
			.output(EmiStack.of(YBlocks.STRIPPED_SQUEEZE_LOG))
			.build());
		registry.removeRecipes(er -> er.getCategory() == VanillaEmiRecipeCategories.CRAFTING && registry.getRecipeManager().get(er.getId())
				.map(it -> it instanceof SecretShapedRecipe || it instanceof LampRecipe
						|| (it instanceof CraftingRecipe cr && (cr.getIngredients().size() > 9
								|| (cr instanceof ShapedRecipe sr && (sr.getWidth() > 3 || sr.getHeight() > 3))))).orElse(false));
		
		registry.removeEmiStacks(EmiStack.of(YBlocks.DUST));
		registry.removeEmiStacks(EmiStack.of(YBlocks.RAFTER));
		registry.removeEmiStacks(EmiStack.of(YBlocks.GIANT_COBBLESTONE));
		registry.removeEmiStacks(EmiStack.of(YItems.LOOTBOX_OF_CONTINUITY));
		registry.removeEmiStacks(EmiStack.of(YItems.SPATULA));
		registry.removeEmiStacks(EmiStack.of(YItems.LOGO));
		registry.removeEmiStacks(EmiStack::isEmpty);
		
		Function<Comparison, Comparison> compareNbt = c -> c.copy().nbt(true).build();
		registry.setDefaultComparison(YItems.MERCURIAL_POTION, compareNbt);
		registry.setDefaultComparison(YItems.MERCURIAL_SPLASH_POTION, compareNbt);
		registry.setDefaultComparison(YItems.LAZOR_EMITTER, compareNbt);
		registry.setDefaultComparison(YItems.LAMP, compareNbt);
		registry.setDefaultComparison(YItems.FIXTURE, compareNbt);
		registry.setDefaultComparison(YItems.CAGE_LAMP, compareNbt);
		registry.setDefaultComparison(YItems.PANEL, compareNbt);
		
		registry.addExclusionArea(CreativeInventoryScreen.class, (screen, out) -> {
			ItemGroup selected = ItemGroup.GROUPS[screen.getSelectedTab()];
			ItemGroupParent parent = (ItemGroupParent)selected;
			if (screen instanceof SubTabLocation stl && parent.yttr$getChildren() != null && !parent.yttr$getChildren().isEmpty()) {
				out.accept(new Bounds(stl.yttr$getX(), stl.yttr$getY(), stl.yttr$getW(), stl.yttr$getH()));
			}
		});
		
		Hash.Strategy<ItemStack> itemStackStrategy = new Hash.Strategy<ItemStack>() {

			@Override
			public int hashCode(ItemStack o) {
				return Objects.hash(o.getItem(), o.getCount(), o.getNbt());
			}

			@Override
			public boolean equals(ItemStack a, ItemStack b) {
				if (a == b) return true;
				if (a == null) return false;
				if (b == null) return false;
				return ItemStack.canCombine(a, b) && a.getCount() == b.getCount();
			}
			
		};
		
		Multimap<ItemStack, List<ItemStack>> resultsToInputs = Multimaps.newMultimap(new Object2ObjectLinkedOpenCustomHashMap<>(itemStackStrategy), Lists::newArrayList);
		Multimap<ItemStack, List<ItemStack>> resultsToColorlessInputs = Multimaps.newMultimap(new Object2ObjectLinkedOpenCustomHashMap<>(itemStackStrategy), Lists::newArrayList);
		
		List<LampRecipe> recipes = registry.getRecipeManager().values().stream()
				.map(r -> r instanceof LampRecipe lr ? lr : null)
				.filter(lr -> lr != null)
				.sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
				.toList();
		
		for (LampRecipe r : recipes) {
			resultsToInputs.clear();
			resultsToColorlessInputs.clear();
			LampRecipe lr = r;
			DefaultedList<Ingredient> ingredients = lr.getIngredients();
			boolean interesting = false;
			int w = 0;
			int h = 0;
			sizeCheck: for (int i = 1; i <= 3; i++) {
				for (int j = 1; j <= 3; j++) {
					if (lr.fits(i, j)) {
						w = i;
						h = j;
						break sizeCheck;
					}
				}
			}
			CraftingInventory inv = new CraftingInventory(new ScreenHandler(null, 0) {
				@Override
				public boolean canUse(PlayerEntity player) {
					return false;
				}
			}, w, h);
			for (int i = 0; i < ingredients.size(); i++) {
				ItemStack[] matching = ingredients.get(i).getMatchingStacks();
				if (matching.length == 0) {
					inv.setStack(i, ItemStack.EMPTY);
				} else {
					inv.setStack(i, matching[0]);
				}
			}
			if (interesting) {
				System.out.println(r.getId()+" is "+w+"x"+h);
				System.out.println("initial inventory setup: "+Yttr.asList(inv));
			}
			boolean shapeless = true;
			final boolean finteresting = interesting;
			// go through every possible permutation of the inputs and figure out what causes different outputs
			for (int i = 0; i < ingredients.size(); i++) {
				final int fi = i;
				for (ItemStack is : ingredients.get(i).getMatchingStacks()) {
					if (is.isOf(YItems.YTTRIUM_INGOT)) {
						shapeless = false;
					}
					permute(is, (isp, colorless) -> {
						inv.setStack(fi, isp);
						for (int j = 0; j < ingredients.size(); j++) {
							if (ingredients.size() > 1 && j == fi) continue;
							final int fj = j;
							for (ItemStack is2 : ingredients.get(j).getMatchingStacks()) {
								permute(is2, (isp2, colorless2) -> {
									inv.setStack(fj, isp2);
									ItemStack result = lr.craft(inv);
									if (finteresting) {
										System.out.println(Yttr.asList(inv)+" => "+result);
									}
									if (!result.isEmpty()) {
										boolean isColorless = colorless || colorless2;
										if (ingredients.size() == 1) isColorless = colorless2;
										if (result.getItem() instanceof LampBlockItem && LampBlockItem.getColor(result) == LampColor.COLORLESS) {
											isColorless = false;
										}
										(isColorless ? resultsToColorlessInputs : resultsToInputs)
											.put(result, Lists.newArrayList(Yttr.asList(inv)));
									}
								});
							}
						}
					});
				}
			}
			// now construct recipe displays for every unique output
			String[] names = {
				"/colorless.", "."
			};
			int k = 0;
			for (var mm : List.of(resultsToColorlessInputs, resultsToInputs)) {
				int permutation = 0;
				for (var en : mm.asMap().entrySet()) {
					List<Set<ItemStack>> fin = Lists.newArrayList();
					for (List<ItemStack> inputs : en.getValue()) {
						for (int i = 0; i < inputs.size(); i++) {
							int x = i%w;
							int y = i/w;
							int j = (y*3)+x;
							while (fin.size() <= j) {
								fin.add(new ObjectLinkedOpenCustomHashSet<>(itemStackStrategy));
							}
							fin.get(j).add(inputs.get(i));
						}
					}
					ItemStack result = en.getKey();
					List<Text> tip = Lists.newArrayList();
					if (result.getItem() == YItems.SUIT_HELMET) {
						LampColor color = LampBlockItem.getColor(result);
						tip.add(new TranslatableText("emi.yttr.suit_helmet_hud", new TranslatableText("color.yttr."+color.asString())
								.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.baseLitColor)))));
					}
					EmiCraftingRecipe ecr = new EmiCraftingRecipe(
							fin.stream()
								.map(s -> EmiIngredient.of(s.stream()
										.map(EmiStack::of)
										.toList()))
								.toList(),
							EmiStack.of(result),
							Yttr.id(r.getId().getPath()+names[k]+permutation),
							shapeless
						);
					registry.addRecipe(ecr);
					permutation++;
				}
				k++;
			}
		}
	}
	
	private void permute(ItemStack is, BiConsumer<ItemStack, Boolean> cb) {
		if (is.getItem() instanceof LampBlockItem) {
			for (LampColor lc : LampColor.VALUES) {
				ItemStack is2 = is.copy();
				LampBlockItem.setColor(is2, lc);
				LampBlockItem.setInverted(is2, false);
				cb.accept(is2, false);
				if (lc == LampColor.COLORLESS) cb.accept(is2, true);
				is2 = is2.copy();
				LampBlockItem.setInverted(is2, true);
				cb.accept(is2, false);
				if (lc == LampColor.COLORLESS) cb.accept(is2, true);
			}
		} else {
			cb.accept(is, false);
		}
	}


}
