package com.unascribed.yttr.client;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Month;
import java.time.MonthDay;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.lib39.core.api.util.LatchReference;
import com.unascribed.lib39.deferral.api.RenderBridge;
import com.unascribed.lib39.recoil.api.RecoilEvents;
import com.unascribed.lib39.ripple.api.SplashTextRegistry;
import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.cache.CleavedBlockMeshes;
import com.unascribed.yttr.client.render.CleaverUI;
import com.unascribed.yttr.client.render.ControlHints;
import com.unascribed.yttr.client.render.EffectorRenderer;
import com.unascribed.yttr.client.render.PlatformsRenderer;
import com.unascribed.yttr.client.render.ReplicatorRenderer;
import com.unascribed.yttr.client.render.RifleHUDRenderer;
import com.unascribed.yttr.client.render.ShifterUI;
import com.unascribed.yttr.client.render.SuitHUDRenderer;
import com.unascribed.yttr.client.render.VelresinUI;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.compat.EarsCompat;
import com.unascribed.yttr.compat.trinkets.YttrTrinketsCompatClient;
import com.unascribed.yttr.content.block.decor.BloqueBlock;
import com.unascribed.yttr.content.block.decor.CleavedBlock;
import com.unascribed.yttr.content.block.mechanism.ReplicatorBlock;
import com.unascribed.yttr.content.block.mechanism.VelresinBlock;
import com.unascribed.yttr.content.block.void_.DivingPlateBlock;
import com.unascribed.yttr.content.block.void_.DormantVoidGeyserBlock;
import com.unascribed.yttr.content.item.RifleItem;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YEntities;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YHandledScreens;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.mixin.accessor.client.AccessorClientPlayerInteractionManager;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityTrackingSoundInstance;
import com.unascribed.yttr.mixinsupport.Clippy;
import com.unascribed.yttr.network.MessageC2SCreativeBlink;
import com.unascribed.yttr.network.MessageC2SCreativeNoClip;
import com.unascribed.yttr.util.YLog;
import com.unascribed.yttr.util.annotate.ConstantColor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.LightBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class YttrClient extends IHasAClient implements ClientModInitializer {
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	public static final Map<Entity, SoundInstance> dropCastSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();

	public static final VertexFormat POSITION_NORMAL = new VertexFormat(ImmutableMap.of(
			"Position", VertexFormats.POSITION_ELEMENT,
			"Normal", VertexFormats.NORMAL_ELEMENT));
	
	public static ShaderProgram positionNormalShader;
	
	private boolean hasCheckedRegistry = false;
	private boolean firstWorldTick = true;
	
	public static boolean renderingGui = false;
	public static boolean forceIbxmMono;
	
	public static int soulImpurity;
	
	private HitResult rifleHitResult;
	private long lastRifleHitUpdate;
	
	private boolean noclipping;

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			for (var f : VelresinBlock.Facing.values()) {
				out.accept(new ModelIdentifier("yttr", "spread_"+f.asString(), "inventory"));
			}
		});
		if (RenderBridge.canUseCompatFunctions()) {
			SplashTextRegistry.replace("Now on OpenGL 3.2 core profile!",
					"Now on OpenGL 3.2 §mcore§r §ocompatibility§r profile!");
			SplashTextRegistry.registerStatic("Core profile? More like snore profile!");
		}
		if (YConfig.General.shenanigans) {
			SplashTextRegistry.remove("The true meaning of covfefe");
			SplashTextRegistry.replace("Don't bother with the clones!",
					"Try the clones!");
			SplashTextRegistry.replace("Closed source!",
					"Effectively visible source!");
			SplashTextRegistry.replace("Lennart lennart = new Lennart()",
					"§7Lennart §flennart §6= §9new §7Lennart§6();");
			SplashTextRegistry.registerStatic(
					"Also try Minetest!",
					"Also try Terasology!",
					"Also try Vintage Story!",
					"Also try ZZT!",
					"Also try MegaZeux!",
					"Also try Xonotic!",
					"Now with everybody's favorite Bloque® Brand Plastic Construction Building Bricks!",
					"Vertical!",
					"§9var §flen §6= §9new §7Lennart§6(); §2// DRY",
					"", // the scariest splash is no splash at all
					"Now with Void!",
					"Ingestible food!",
					"§4§lFILL YOUR SOUL WITH SOUP",
					"Beetroot?",
					"Beeproot?",
					"§cwhy have you done this",
					"Team effort!"
				);
			
			SplashTextRegistry.registerTemporal("Happy birthday, Kat!", MonthDay.of(Month.JANUARY, 28));
			SplashTextRegistry.registerTemporal("Happy birthday, Una!", MonthDay.of(Month.OCTOBER, 29));
		}
		doReflectionMagic();
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if ((sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE.getId()) || sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE_FAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					SoundInstance existing = rifleChargeSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
					if (existing != null) {
						mc.getSoundManager().stop(existing);
					}
				} else if ((sound.getSound().getIdentifier().equals(YSounds.DROP_CAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					SoundInstance existing = dropCastSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
					if (existing != null) {
						mc.getSoundManager().stop(existing);
					}
				}
			});
			ReloadableResourceManager rm = (ReloadableResourceManager)mc.getResourceManager();
			rm.registerReloader(reloader("yttr:clear_caches", (manager) -> {
				TextureColorThief.clearCache();
				CleavedBlockMeshes.clearCache();
			}));
			rm.registerReloader(reloader("yttr:detect", (manager) -> {
				Yttr.lessCreepyAwareHopper = manager.getResource(Yttr.id("lcah-marker")).isPresent();
				Yttr.vectorSuit = manager.getResource(Yttr.id("vector-marker")).isPresent();
			}));
			Yttr.lessCreepyAwareHopper = rm.getResource(Yttr.id("lcah-marker")).isPresent();
			Yttr.vectorSuit = rm.getResource(Yttr.id("vector-marker")).isPresent();
		});
		
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
				out.accept(new ModelIdentifier("yttr", "ammo_pack_model", "inventory"));
				out.accept(new ModelIdentifier("yttr", "ammo_pack_seg_model", "inventory"));
				out.accept(new ModelIdentifier("yttr", "platforms_model", "inventory"));
			});
		}
		
		ModelPredicateProviderRegistry.register(YItems.SNARE, Yttr.id("filled"), (stack, world, entity, seed) -> {
			return stack.hasNbt() && stack.getNbt().contains("Contents") ? 1 : 0;
		});
		ModelPredicateProviderRegistry.register(Yttr.id("durability_bonus"), (stack, world, entity, seed) -> {
			return stack.hasNbt() ? stack.getNbt().getInt("yttr:DurabilityBonus") : 0;
		});
		ModelPredicateProviderRegistry.register(Yttr.id("gui"), (stack, world, entity, seed) -> {
			return renderingGui ? 1 : 0;
		});
		ModelPredicateProviderRegistry.register(Yttr.id("has_block_entity"), (stack, world, entity, seed) -> {
			return stack.getSubNbt("BlockEntityTag") != null ? 1 : 0;
		});
		ModelPredicateProviderRegistry.register(YItems.TINT, new Identifier("level"), (stack, world, entity, seed) -> {
			var nbt = stack.getSubNbt("BlockStateTag");
			try {
				if (nbt != null) {
					NbtElement ele = nbt.get(LightBlock.LEVEL_15.getName());
					if (ele != null) {
						return Integer.parseInt(ele.asString()) / 16f;
					}
				}
			} catch (NumberFormatException e) {}
			return 1;
		});
		

		if (FabricLoader.getInstance().isModLoaded("ears")) {
			try {
				EarsCompat.init();
			} catch (Throwable t) {
				YLog.warn("Failed to load Ears compat", t);
			}
		}
		if (FabricLoader.getInstance().isModLoaded("trinkets")) {
			try {
				YttrTrinketsCompatClient.init();
			} catch (Throwable t) {
				YLog.warn("Failed to load Trinkets compat", t);
			}
		}
		
		ClientTickEvents.START_CLIENT_TICK.register((mc) -> {
			Profiler prof = mc.getProfiler();
			prof.swap("yttr");
			if (mc.world != null) {
				if (firstWorldTick) {
					firstWorldTick = false;
					Calendar c = Calendar.getInstance();
					c.setTime(new Date());
					int month = c.get(Calendar.MONTH)+1;
					int day = c.get(Calendar.DAY_OF_MONTH);
					if (YConfig.General.shenanigans) {
						if (month == 1 && (day >= 9 && day <= 15)) {
							mc.player.sendMessage(Text.translatable("chat.type.text", Text.literal(">:]").formatted(Formatting.AQUA),
									Text.translatable("msg.yttr.well_wishes")), false);
						}
					}
				}
			} else {
				firstWorldTick = true;
			}
			if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
				if (mc.world != null && mc.isIntegratedServerRunning() && !hasCheckedRegistry) {
					hasCheckedRegistry = true;
					for (Map.Entry<RegistryKey<Block>, Block> en : Registries.BLOCK.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
							if (en.getValue() instanceof ReplicatorBlock) continue;
							if (en.getValue() instanceof DivingPlateBlock) continue;
							if (en.getValue() instanceof DormantVoidGeyserBlock) continue;
							if (en.getValue() instanceof CleavedBlock) continue;
							if (en.getValue() instanceof BloqueBlock) continue;
							if (en.getValue().getDefaultState().isAir()) continue;
							if (en.getValue().getLootTableId().equals(LootTables.EMPTY)) continue;
							if (!mc.getServer().getLootManager().getIds(LootDataType.LOOT_TABLES).contains(en.getValue().getLootTableId())) {
								if (en.getValue().getDroppedStacks(en.getValue().getDefaultState(), new LootContextParameterSet.Builder(mc.getServer().getOverworld())
										.add(LootContextParameters.TOOL, new ItemStack(Items.APPLE))
										.add(LootContextParameters.ORIGIN, Vec3d.ZERO)).isEmpty()) {
									YLog.error("Block "+en.getKey().getValue()+" is missing a loot table and doesn't seem to have custom drops");
								}
							}
						}
					}
					for (Map.Entry<RegistryKey<Item>, Item> en : Registries.ITEM.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
					for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> en : Registries.ENTITY_TYPE.getEntries()) {
						if (en.getKey().getValue().getNamespace().equals("yttr")) {
							checkTranslation(en.getKey().getValue(), en.getValue().getTranslationKey());
						}
					}
				}
			}
			if (mc.isPaused()) return;
			prof.push("effector");
			EffectorRenderer.tick();
			prof.swap("suit");
			SuitHUDRenderer.tick();
			prof.swap("replicator");
			ReplicatorRenderer.tick();
			prof.swap("rifle");
			RifleHUDRenderer.tick();
			prof.swap("shifter");
			ShifterUI.tick();
			if (mc.player != null && mc.player.isCreative() && mc.player.getStackInHand(Hand.MAIN_HAND).getItem() == YItems.SHIFTER) {
				((AccessorClientPlayerInteractionManager)mc.interactionManager).yttr$setBlockBreakingCooldown(0);
			}
			prof.pop();
			if (Yttr.isEnlightened(mc.player, true)) {
				if (mc.options.dropKey.wasPressed()) {
					var pos = mc.player.raycast(256, 0, false).getPos();
					new MessageC2SCreativeBlink(pos.x, mc.player.isSneaking() ? mc.player.getY() : pos.y, pos.z).sendToServer();
				}
				if (mc.options.swapHandsKey.isPressed() != noclipping) {
					noclipping = !noclipping;
					((Clippy)mc.player).yttr$setNoClip(noclipping);
					new MessageC2SCreativeNoClip(noclipping).sendToServer();
				}
			} else if (mc.player != null && noclipping) {
				noclipping = false;
				mc.player.noClip = false;
			}
		});
		
		HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
			var prof = mc.getProfiler();
			prof.push("yttr");
			prof.push("suit-hud");
			SuitHUDRenderer.render(ctx, tickDelta);
			prof.swap("rifle-hud");
			RifleHUDRenderer.render(ctx, tickDelta);
			prof.swap("shifter-ui");
			ShifterUI.render(ctx, tickDelta);
			prof.swap("control-hints");
			ControlHints.render(ctx, tickDelta);
			prof.pop();
			prof.pop();
		});
		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
		WorldRenderEvents.BLOCK_OUTLINE.register(ReplicatorRenderer::renderOutline);
		WorldRenderEvents.BLOCK_OUTLINE.register(ShifterUI::renderOutline);
		WorldRenderEvents.BLOCK_OUTLINE.register(VelresinUI::render);
		WorldRenderEvents.LAST.register(EffectorRenderer::render);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(ReplicatorRenderer::render);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(PlatformsRenderer::renderWorld);
		DynamicBlockModelProvider.init();
		
		CoreShaderRegistrationCallback.EVENT.register(ctx -> {
			ctx.register(Yttr.id("position_normal"), POSITION_NORMAL, it -> positionNormalShader = it);
		});

		EntityRendererRegistry.register(YEntities.SLIPPING_TRANSFUNGUS, FallingBlockEntityRenderer::new);
		EntityRendererRegistry.register(YEntities.THROWN_GLOWING_GAS, FlyingItemEntityRenderer::new);
		
		RecoilEvents.UPDATE_FOV.register((fov, tickDelta) -> {
			fov.set(MathHelper.lerp(RifleHUDRenderer.scopeA, fov.get(), 10));
		});
		RecoilEvents.UPDATE_ENTITY_RENDER_DISTANCE.register((erd) -> {
			if (RifleHUDRenderer.scopeA > 0) {
				erd.scale(1+(RifleHUDRenderer.scopeA*5));
			}
		});
		RecoilEvents.RENDER_CROSSHAIRS.register(ctx -> {
			var matrices = ctx.getMatrices();
			var stack = mc.player.getMainHandStack();
			if (mc.player != null && stack.getItem() instanceof RifleItem ri) {
				RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
				RenderSystem.setShaderColor(1, 1, 1, 1);
				int windowWidth = mc.getWindow().getScaledWidth();
				int windowHeight = mc.getWindow().getScaledHeight();
				int w = 15;
				int h = 15;
				long now = System.currentTimeMillis();
				if (rifleHitResult == null || now-lastRifleHitUpdate > 100) {
					rifleHitResult = RifleItem.raycast(mc.world, mc.player);
					lastRifleHitUpdate = now;
				}
				int u = switch (rifleHitResult.getType()) {
					case MISS -> 0;
					case BLOCK -> 15;
					case ENTITY -> 30;
					default -> 0;
				};
				var tex = Yttr.id("textures/gui/rifle_crosshairs.png");
				ctx.drawTexture(tex, (windowWidth-w)/2, (windowHeight-h)/2, u, 0, w, h, 45, 30);
				if (mc.player.isUsingItem() && mc.player.getActiveHand() == Hand.MAIN_HAND) {
					int useTicks = ri.calcAdjustedUseTime(stack, mc.player.getItemUseTimeLeft());
					float power = ri.calculatePower(useTicks);
					IntConsumer draw = (u2) -> ctx.drawTexture(tex, (windowWidth-w)/2, (windowHeight-h)/2, u2, 15, w, h, 45, 30);
					if (power >= 1.2f) {
						draw.accept(15);
						draw.accept(30);
					} else if (power >= 1) {
						draw.accept(15);
					} else if (power >= 0.7f) {
						draw.accept(0);
					}
				}
				RenderSystem.defaultBlendFunc();
				return true;
			} else {
				rifleHitResult = null;
				lastRifleHitUpdate = 0;
			}
			return false;
		});
		
		// TODO
//		ResourcePackProvider prov = new ResourcePackProvider() {
//			@Override
//			public void register(Consumer<ResourcePackProfile> consumer) {
//				ResourcePackFactory f = str -> new EmbeddedResourcePack("lcah");
//				consumer.accept(ResourcePackProfile.of("yttr:lcah", Text.literal("Less Creepy Aware Hopper"), false, f,
//						ResourceType.CLIENT_RESOURCES, InsertionPosition.TOP, nameAndSource("Yttr built-in")));
//				f = str -> new EmbeddedResourcePack("vector");
//				consumer.accept(ResourcePackProfile.of("yttr:vector", Text.literal("Vector Suit"), false, f,
//						ResourceType.CLIENT_RESOURCES, InsertionPosition.TOP, nameAndSource("Yttr built-in")));
//			}
//			private static ResourcePackSource nameAndSource(String source) {
//				Text text = Text.translatable(source);
//				return ResourcePackSource.of(name -> Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY), false);
//			}
//		};
//
//		AccessorResourcePackManager arpm = ((AccessorResourcePackManager)MinecraftClient.getInstance().getResourcePackManager());
//		Set<ResourcePackProvider> providers = arpm.yttr$getProviders();
//		try {
//			providers.add(prov);
//		} catch (UnsupportedOperationException e) {
//			providers = Sets.newHashSet(providers);
//			providers.add(prov);
//			arpm.yttr$setProviders(providers);
//		}
	}

	private SimpleSynchronousResourceReloadListener reloader(String idStr, Consumer<ResourceManager> cb) {
		Identifier id = new Identifier(idStr);
		return new SimpleSynchronousResourceReloadListener() {
			@Override
			public void reload(ResourceManager manager) {
				cb.accept(manager);
			}
			
			@Override
			public Identifier getFabricId() {
				return id;
			}
		};
	}

	private void checkTranslation(Identifier id, String key) {
		if (!I18n.hasTranslation(key)) {
			YLog.error("Translation "+key+" is missing for "+id);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doReflectionMagic() {
		Map<String, RenderLayer> renderLayers = Maps.newHashMap();
		renderLayers.put("cutout", RenderLayer.getCutout());
		renderLayers.put("cutout_mipped", RenderLayer.getCutoutMipped());
		renderLayers.put("translucent", RenderLayer.getTranslucent());
		renderLayers.put("tripwire", RenderLayer.getTripwire());
		Yttr.autoreg.eachRegisterableField(YBlocks.class, Block.class, com.unascribed.yttr.util.annotate.RenderLayer.class, (f, b, ann) -> {
			if (b instanceof BlockColorProvider) ColorProviderRegistry.BLOCK.register((BlockColorProvider)b, b);
			if (ann != null) {
				if (!renderLayers.containsKey(ann.value())) throw new RuntimeException("YBlocks."+f.getName()+" has an unknown @RenderLayer: "+ann.value());
				BlockRenderLayerMap.INSTANCE.putBlocks(renderLayers.get(ann.value()), b);
			}
		});
		Yttr.autoreg.eachRegisterableField(YItems.class, Item.class, null, this::handleItemAutoreg);
		Yttr.autoreg.eachRegisterableField(YItems.class, LatchReference.class, null, (f, l, s) -> {
			if (l.isPresent() && l.get() instanceof Item i) {
				handleItemAutoreg(f, i, s);
			}
		});
		Yttr.autoreg.eachRegisterableField(YBlockEntities.class, BlockEntityType.class, YBlockEntities.Renderer.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(Class.forName("com.unascribed.yttr.client.render.block_entity."+ann.value()), MethodType.methodType(void.class));
					BlockEntityRendererFactories.register(type, berd -> {
						try {
							return (BlockEntityRenderer<?>)handle.invoke();
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		Yttr.autoreg.eachRegisterableField(YEntities.class, EntityType.class, YEntities.Renderer.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(Class.forName("com.unascribed.yttr.client.render."+ann.value()), MethodType.methodType(void.class, EntityRenderDispatcher.class));
					EntityRendererRegistry.register(type, (ctx) -> {
						try {
							return (EntityRenderer<?>)handle.invoke(ctx.getRenderDispatcher());
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		Map<Fluid, Class<?>> fluids = Maps.newHashMap();
		Map<Class<?>, Identifier[]> fluidSprites = Maps.newHashMap();
		Map<Class<?>, int[]> fluidColors = Maps.newHashMap();
		Yttr.autoreg.eachRegisterableField(YFluids.class, Fluid.class, null, (f, fl, ann) -> {
			fluids.put(fl, f.getType());
			com.unascribed.yttr.util.annotate.RenderLayer rlAnn = f.getAnnotation(com.unascribed.yttr.util.annotate.RenderLayer.class);
			if (rlAnn != null) {
				if (!renderLayers.containsKey(rlAnn.value())) throw new RuntimeException("YFluids."+f.getName()+" has an unknown @RenderLayer: "+rlAnn.value());
				BlockRenderLayerMap.INSTANCE.putFluids(renderLayers.get(rlAnn.value()), fl);
			}
			ConstantColor colAnn = f.getAnnotation(ConstantColor.class);
			if (colAnn != null) {
				if (!fluidColors.containsKey(f.getType().getSuperclass())) {
					fluidColors.put(f.getType().getSuperclass(), new int[] { -1, -1 });
				}
				fluidColors.get(f.getType().getSuperclass())[fl.isSource(fl.getDefaultState()) ? 0 : 1] = colAnn.value();
			}
			YFluids.Sprite spriteAnn = f.getAnnotation(YFluids.Sprite.class);
			if (spriteAnn != null) {
				if (!fluidSprites.containsKey(f.getType().getSuperclass())) {
					fluidSprites.put(f.getType().getSuperclass(), new Identifier[] { new Identifier("missingno"), new Identifier("missingno") });
				}
				fluidSprites.get(f.getType().getSuperclass())[fl.isSource(fl.getDefaultState()) ? 0 : 1] = new Identifier(spriteAnn.value());
			}
		});
		int[] white = new int[] { -1, -1 };
		Identifier[] missingno = { new Identifier("missingno"), new Identifier("missingno") };
		for (Map.Entry<Fluid, Class<?>> en : fluids.entrySet()) {
			final int[] colors = fluidColors.getOrDefault(en.getValue().getSuperclass(), white);
			final Identifier[] spriteIds = fluidSprites.getOrDefault(en.getValue().getSuperclass(), missingno);
			FluidRenderHandler frh = new FluidRenderHandler() {
				@Override
				public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
					return new Sprite[] {
						mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(spriteIds[0]),
						mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(spriteIds[1])
					};
				}
				@Override
				public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
					return state.isSource() ? colors[0] : colors[1];
				}
			};
			FluidRenderHandlerRegistry.INSTANCE.register(en.getKey(), frh);
		}
		Yttr.autoreg.eachRegisterableField(YHandledScreens.class, ScreenHandlerType.class, YHandledScreens.Screen.class, (f, type, ann) -> {
			if (ann != null) {
				try {
					Constructor<?> actualConstructor = null;
					for (Constructor<?> cons : Class.forName("com.unascribed.yttr.client.screen.handled."+ann.value()).getConstructors()) {
						if (cons.getParameterCount() == 3 && ScreenHandler.class.isAssignableFrom(cons.getParameterTypes()[0])) {
							actualConstructor = cons;
						}
					}
					if (actualConstructor == null) throw new RuntimeException(ann.value()+" does not have a normal constructor");
					MethodHandle handle = MethodHandles.publicLookup().unreflectConstructor(actualConstructor);
					// must be an anonymous class due to type unsafety; we need the rawtype
					HandledScreens.register(type, new HandledScreens.Provider() {
		
						@Override
						public Screen create(ScreenHandler handler, PlayerInventory inventory, Text title) {
							try {
								return (HandledScreen)handle.invoke(handler, inventory, title);
							} catch (RuntimeException | Error e) {
								throw e;
							} catch (Throwable e) {
								throw new RuntimeException(e);
							}
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	public void handleItemAutoreg(Field f, Item i, Annotation ann) {
		if (i instanceof ItemColorProvider) ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
		ConstantColor colAnn = f.getAnnotation(ConstantColor.class);
		if (colAnn != null) ColorProviderRegistry.ITEM.register((stack, tintIndex) -> colAnn.value(), i);
		YItems.ColorProvider colProvAnn = f.getAnnotation(YItems.ColorProvider.class);
		if (colProvAnn != null) {
			try {
				ColorProviderRegistry.ITEM.register((ItemColorProvider)Class.forName("com.unascribed.yttr.client."+colProvAnn.value()).getConstructor().newInstance(), i);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
		YItems.BuiltinRenderer birAnn = f.getAnnotation(YItems.BuiltinRenderer.class);
		if (birAnn != null) {
			try {
				Class<?> rend = Class.forName("com.unascribed.yttr.client.render."+birAnn.value());
				MethodHandle renderHandle = MethodHandles.publicLookup().findStatic(rend, "render", MethodType.methodType(void.class, ItemStack.class, ModelTransformationMode.class, MatrixStack.class, VertexConsumerProvider.class, int.class, int.class));
				BuiltinItemRendererRegistry.INSTANCE.register(i, (is, mode, matrices, vcp, light, overlay) -> {
					try {
						renderHandle.invoke(is, mode, matrices, vcp, light, overlay);
					} catch (RuntimeException | Error e) {
						throw e;
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				});
				try {
					MethodHandle registerModelsHandle = MethodHandles.publicLookup().findStatic(rend, "registerModels", MethodType.methodType(void.class, Consumer.class));
					ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
						try {
							registerModelsHandle.invoke(out);
						} catch (RuntimeException | Error e) {
							throw e;
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}
					});
				} catch (NoSuchMethodException e) {
					// ignore
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void addLine(MatrixStack matrices, VertexConsumer vc,
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			float r1, float g1, float b1, float a1,
			float r2, float g2, float b2, float a2) {
		addLine(matrices, vc,
				(float)x1, (float)y1, (float)z1,
				(float)x2, (float)y2, (float)z2,
				r1, g1, b1, a1,
				r2, g2, b2, a2);
	}

	public static void addLine(MatrixStack matrices, VertexConsumer vc,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float r1, float g1, float b1, float a1,
			float r2, float g2, float b2, float a2) {
		float dX = x2 - x1;
		float dY = y2 - y1;
		float dZ = z2 - z2;
		float dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
		dX /= dist;
		dY /= dist;
		dZ /= dist;
		Matrix4f model = matrices.peek().getModel();
		Matrix3f normal = matrices.peek().getNormal();
		vc.vertex(model, x1, y1, z1).color(r1, g1, b1, a1).normal(normal, dX, dY, dZ).next();
		vc.vertex(model, x2, y2, z2).color(r2, g2, b2, a2).normal(normal, dX, dY, dZ).next();
	}

	public static Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, String suffix, Identifier id) {
		if ((stack.isOf(Items.DIAMOND_HELMET) ||
					stack.isOf(Items.DIAMOND_CHESTPLATE) ||
					stack.isOf(Items.DIAMOND_LEGGINGS) ||
					stack.isOf(Items.DIAMOND_BOOTS)
				) && stack.hasNbt() && stack.getNbt().getInt("yttr:DurabilityBonus") > 0) {
			return new Identifier("textures/models/armor/yttr_ultrapure_diamond_layer_" + (secondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png");
		}
		return id;
	}
	
}
