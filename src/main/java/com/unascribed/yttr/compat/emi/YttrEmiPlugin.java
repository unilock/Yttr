package com.unascribed.yttr.compat.emi;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.RuinedRecipeResourceMetadata;
import com.unascribed.yttr.compat.emi.handler.ProjectTableRecipeHandler;
import com.unascribed.yttr.compat.emi.handler.RaftingRecipeHandler;
import com.unascribed.yttr.compat.emi.recipe.EmiCentrifugingRecipe;
import com.unascribed.yttr.compat.emi.recipe.EmiForgottenRecipe;
import com.unascribed.yttr.compat.emi.recipe.EmiGiftRecipe;
import com.unascribed.yttr.compat.emi.recipe.EmiShatteringRecipe;
import com.unascribed.yttr.compat.emi.recipe.EmiVoidFilteringRecipe;
import com.unascribed.yttr.compat.emi.stack.SuitHelmetEmiStack;
import com.unascribed.yttr.content.item.DropOfContinuityItem;
import com.unascribed.yttr.content.item.block.LampBlockItem;
import com.unascribed.yttr.crafting.CentrifugingRecipe;
import com.unascribed.yttr.crafting.LampRecipe;
import com.unascribed.yttr.crafting.SecretShapedRecipe;
import com.unascribed.yttr.crafting.ShatteringRecipe;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YEnchantments;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YRecipeTypes;
import com.unascribed.yttr.mechanics.LampColor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class YttrEmiPlugin implements EmiPlugin {

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Simple {
		Class<? extends Recipe<?>> from();
		Class<? extends EmiRecipe> to();
	}
	
	// actual crafting methods
	@Simple(from=CentrifugingRecipe.class, to=EmiCentrifugingRecipe.class)
	public static final EmiRecipeCategory CENTRIFUGING = category(EmiStack.of(YItems.CENTRIFUGE));
	public static final EmiRecipeCategory VOID_FILTERING = category(EmiStack.of(YItems.VOID_FILTER));
	
	// miscellaneous
	@Simple(from=ShatteringRecipe.class, to=EmiShatteringRecipe.class)
	public static final EmiRecipeCategory SHATTERING = category(createShatteringPickaxe(Items.DIAMOND_PICKAXE));
	public static final EmiRecipeCategory CONTINUITY_GIFTS = category(EmiStack.of(YItems.DROP_OF_CONTINUITY));
	public static final EmiRecipeCategory FORGOTTEN_CRAFTING = category(EmiStack.of(YItems.WASTELAND_DIRT));
	
	public static class Texture {
		public static final EmiTexture SHATTERING = new EmiTexture(Yttr.id("textures/gui/shattering.png"), 0, 0, 24, 17, 24, 17, 24, 33);
	}
	
	@Override
	public void register(EmiRegistry registry) {
		Yttr.autoreg.autoRegister((id, c) -> {
			c.id = id;
			Identifier iconId = Yttr.id("textures/gui/emi_simple/"+id.getPath()+".png");
			if (MinecraftClient.getInstance().getResourceManager().getResource(iconId).isPresent()) {
				c.simplified = new EmiTexture(iconId, 0, 0, 16, 16, 16, 16, 16, 16);
			}
			registry.addCategory(c);
		}, YttrEmiPlugin.class, EmiRecipeCategory.class);
		Yttr.autoreg.eachRegisterableField(YttrEmiPlugin.class, EmiRecipeCategory.class, Simple.class, (f, t, a) -> {
			if (a == null) return;
			try {
				RecipeType rt = Registries.RECIPE_TYPE.get(t.id);
				MethodHandle cons = MethodHandles.lookup().findConstructor(a.to(), MethodType.methodType(void.class, a.from()));
				for (Recipe r : (Iterable<Recipe>)registry.getRecipeManager().listAllOfType(rt)) {
					registry.addRecipe((EmiRecipe)cons.invoke(r));
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		});
		
		registry.addWorkstation(VOID_FILTERING, EmiStack.of(YItems.VOID_FILTER));
		registry.addWorkstation(CENTRIFUGING, EmiStack.of(YItems.CENTRIFUGE));
		registry.addWorkstation(CONTINUITY_GIFTS, EmiStack.of(YItems.DROP_OF_CONTINUITY));

		registry.addRecipeHandler(YHandledScreens.RAFTING, new RaftingRecipeHandler());
		registry.addRecipeHandler(YHandledScreens.PROJECT_TABLE, new ProjectTableRecipeHandler());
		
		List<EmiStack> pickaxes = new ArrayList<>();
		for (var en : Registries.ITEM.getEntries()) {
			if (YEnchantments.SHATTERING_CURSE.isAcceptableItem(new ItemStack(en.getValue()))) {
				pickaxes.add(createShatteringPickaxe(en.getValue()));
			}
		}
		registry.addWorkstation(SHATTERING, new ListEmiIngredient(pickaxes, 1) {
			@Override
			public List<TooltipComponent> getTooltip() {
				List<TooltipComponent> tooltip = Lists.newArrayList();
				tooltip.add(TooltipComponent.of(Text.translatable("emi.tooltip.yttr.any_tool").asOrderedText()));
				tooltip.add(TooltipComponent.of(YEnchantments.SHATTERING_CURSE.getName(1).asOrderedText()));
				tooltip.add(new IngredientTooltipComponent(pickaxes));
				return tooltip;
			}
		});
		
		registry.addRecipe(new EmiGiftRecipe(DropOfContinuityItem.getPossibilities().stream()
				.map(EmiStack::of)
				.toList()));
		
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
		
		registry.removeEmiStacks(EmiStack.of(YBlocks.RAFTER));
		registry.removeEmiStacks(EmiStack.of(YBlocks.GIANT_COBBLESTONE));
		registry.removeEmiStacks(EmiStack.of(YItems.LOOTBOX_OF_CONTINUITY));
		registry.removeEmiStacks(EmiStack.of(YItems.SPATULA));
		registry.removeEmiStacks(EmiStack.of(YItems.LOGO));
		registry.removeEmiStacks(EmiStack.of(YItems.CREASE));
		registry.removeEmiStacks(EmiStack::isEmpty);
		
		var compareNbt = Comparison.compareNbt();
		registry.setDefaultComparison(YItems.MERCURIAL_POTION, compareNbt);
		registry.setDefaultComparison(YItems.MERCURIAL_SPLASH_POTION, compareNbt);
		registry.setDefaultComparison(YItems.LAZOR_EMITTER, compareNbt);
		registry.setDefaultComparison(YItems.LAMP, compareNbt);
		registry.setDefaultComparison(YItems.FIXTURE, compareNbt);
		registry.setDefaultComparison(YItems.CAGE_LAMP, compareNbt);
		registry.setDefaultComparison(YItems.PANEL, compareNbt);

		registry.setDefaultComparison(YItems.SNARE, Comparison.of(
				(a, b) -> {
					EntityType<?> ae = YItems.SNARE.getEntityType(a.getItemStack());
					EntityType<?> be = YItems.SNARE.getEntityType(b.getItemStack());

					if (ae == null || be == null) {
						return ae == be;
					} else {
						return ae.equals(be);
					}
				}
		));

		registry.getRecipeManager().listAllOfType(YRecipeTypes.VOID_FILTERING).stream()
			.filter(r -> !r.isHidden())
			.sorted((a, b) -> Float.compare(b.getChance(), a.getChance()))
			.map(EmiVoidFilteringRecipe::new)
			.forEach(registry::addRecipe);
		
		registry.getRecipeManager().listAllOfType(RecipeType.STONECUTTING).stream()
			.filter(r -> r.getResult(MinecraftClient.getInstance().world.getRegistryManager()).getCount() == 1 && !r.getIngredients().isEmpty())
			.map(EmiShatteringRecipe::new)
			.forEach(registry::addRecipe);
		registry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream()
			.filter(r -> r.fits(1, 1) && !r.getIngredients().isEmpty())
			.map(EmiShatteringRecipe::new)
			.forEach(registry::addRecipe);
		
		ResourceManager rm = MinecraftClient.getInstance().getResourceManager();
		for (var en : rm.findResources("textures/gui/ruined_recipe", id -> id.getPath().endsWith(".png")).entrySet()) {
			Identifier id = en.getKey();
			String name = id.getPath();
			name = name.substring(27, name.length()-4);
			if (id.getNamespace().equals("yttr") && (name.equals("border") || name.equals("overlay"))) continue;
			Identifier itemId = new Identifier(id.getNamespace(), name);
			Item result = Registries.ITEM.getOrEmpty(itemId).orElse(null);
			if (result != null) {
				RuinedRecipeResourceMetadata meta = null;
				try {
					meta = en.getValue().getMetadata().readMetadata(RuinedRecipeResourceMetadata.READER).orElse(null);
				} catch (IOException e) {
				}
				Set<Integer> emptySlots = Collections.emptySet();
				if (meta != null) {
					emptySlots = meta.getEmptySlots();
				}
				registry.addRecipe(new EmiForgottenRecipe(itemId, emptySlots, EmiStack.of(result)));
			}
		}
		
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

				@Override
				public ItemStack quickTransfer(PlayerEntity player, int index) {
					return null;
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
									ItemStack result = lr.craft(inv, MinecraftClient.getInstance().world.getRegistryManager());
									if (finteresting) {
										System.out.println(Yttr.asList(inv)+" => "+result);
									}
									if (!result.isEmpty()) {
										boolean isColorless = colorless || colorless2;
										if (ingredients.size() == 1) isColorless = colorless2;
										if (result.getItem() instanceof LampBlockItem && LampBlockItem.getColor(result) == LampColor.COLORLESS
												|| result.getItem() == YItems.SUIT_HELMET) {
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
						tip.add(Text.translatable("emi.yttr.suit_helmet_hud", Text.translatable("color.yttr."+color.asString())
								.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.baseLitColor)))));
					}
					EmiCraftingRecipe ecr = new EmiCraftingRecipe(
							fin.stream() // make EMI autodetect which tag this ingredient matches
								.map(Set::stream)
								.map(Ingredient::ofStacks)
								.map(EmiIngredient::of)
								.toList(),
							result.isOf(YItems.SUIT_HELMET) ? new SuitHelmetEmiStack(result) : EmiStack.of(result),
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
	
	private static EmiRecipeCategory category(EmiRenderable icon) {
		return new EmiRecipeCategory(null, icon);
	}
	
	private static EmiStack createShatteringPickaxe(Item item) {
		ItemStack is = new ItemStack(item);
		EnchantmentHelper.set(ImmutableMap.of(YEnchantments.SHATTERING_CURSE, 1), is);
		return EmiStack.of(is);
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
