package com.unascribed.yttr.client;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.annotate.ConstantColor;
import com.unascribed.yttr.annotate.Renderer;
import com.unascribed.yttr.block.LampBlock;
import com.unascribed.yttr.client.particle.VoidBallParticle;
import com.unascribed.yttr.client.render.LampBlockEntityRenderer;
import com.unascribed.yttr.client.util.DelegatingVertexConsumer;
import com.unascribed.yttr.client.util.TextureColorThief;
import com.unascribed.yttr.client.util.UVObserver;
import com.unascribed.yttr.init.YBlockEntities;
import com.unascribed.yttr.init.YBlocks;
import com.unascribed.yttr.init.YFluids;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.init.YSounds;
import com.unascribed.yttr.item.CleaverItem;
import com.unascribed.yttr.item.RifleItem;
import com.unascribed.yttr.item.block.LampBlockItem;
import com.unascribed.yttr.mixin.accessor.client.AccessorEntityTrackingSoundInstance;
import com.unascribed.yttr.mixin.accessor.client.AccessorRenderPhase;
import com.google.common.collect.MapMaker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.Perspective;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;

public class YttrClient implements ClientModInitializer {
	
	private static final Identifier CHAMBER_TEXTURE = new Identifier("yttr", "textures/item/rifle_chamber.png");
	
	private static final Identifier VOID_FLOW = new Identifier("yttr", "block/void_flow");
	private static final Identifier VOID_STILL = new Identifier("yttr", "block/void_still");
	
	private static final ModelIdentifier BASE_MODEL = new ModelIdentifier("yttr:rifle_base#inventory");
	private static final ModelIdentifier CHAMBER_MODEL = new ModelIdentifier("yttr:rifle_chamber#inventory");
	private static final ModelIdentifier CHAMBER_GLASS_MODEL = new ModelIdentifier("yttr:rifle_chamber_glass#inventory");
	
	public static final Map<Entity, SoundInstance> rifleChargeSounds = new MapMaker().concurrencyLevel(1).weakKeys().weakValues().makeMap();
	
	private final UVObserver uvo = new UVObserver();
	
	private final MinecraftClient mc = MinecraftClient.getInstance();
	
	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_REINFORCED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YItems.RIFLE_OVERCLOCKED, this::renderRifle);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.LAMP, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.FIXTURE, this::renderLamp);
		BuiltinItemRendererRegistry.INSTANCE.register(YBlocks.CAGE_LAMP, this::renderLamp);
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(VOID_FLOW);
			registry.register(VOID_STILL);
		});
		eachRegisterableField(YBlocks.class, Block.class, (f, b) -> {
			if (b instanceof BlockColorProvider) {
				ColorProviderRegistry.BLOCK.register((BlockColorProvider)b, b);
			}
			com.unascribed.yttr.annotate.RenderLayer ann = f.getAnnotation(com.unascribed.yttr.annotate.RenderLayer.class);
			if (ann != null) {
				boolean foundIt = false;
				for (RenderLayer layer : RenderLayer.getBlockLayers()) {
					if (((AccessorRenderPhase)layer).yttr$getName().equals(ann.value())) {
						BlockRenderLayerMap.INSTANCE.putBlocks(layer, b);
						foundIt = true;
						break;
					}
				}
				if (!foundIt) throw new RuntimeException("YBlocks."+f.getName()+" has an unknown @RenderLayer: "+ann.value());
			}
		});
		eachRegisterableField(YItems.class, Item.class, (f, i) -> {
			if (i instanceof ItemColorProvider) {
				ColorProviderRegistry.ITEM.register((ItemColorProvider)i, i);
			}
			ConstantColor ann = f.getAnnotation(ConstantColor.class);
			if (ann != null) {
				ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ann.value(), i);
			}
		});
		eachRegisterableField(YBlockEntities.class, BlockEntityType.class, (f, type) -> {
			Renderer ann = f.getAnnotation(Renderer.class);
			if (ann != null) {
				try {
					MethodHandle handle = MethodHandles.publicLookup().findConstructor(ann.value(), MethodType.methodType(void.class, BlockEntityRenderDispatcher.class));
					BlockEntityRendererRegistry.INSTANCE.register(type, berd -> {
						try {
							return (BlockEntityRenderer<?>)handle.invoke(berd);
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
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(BASE_MODEL);
			out.accept(CHAMBER_MODEL);
			out.accept(CHAMBER_GLASS_MODEL);
		});
		registerFluidRenderers();
		mc.send(() -> {
			mc.getSoundManager().registerListener((sound, soundSet) -> {
				if ((sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE.getId()) || sound.getSound().getIdentifier().equals(YSounds.RIFLE_CHARGE_FAST.getId()))
						&& sound instanceof EntityTrackingSoundInstance) {
					rifleChargeSounds.put(((AccessorEntityTrackingSoundInstance)sound).yttr$getEntity(), sound);
				}
			});
			((ReloadableResourceManager)mc.getResourceManager()).registerListener(new SimpleSynchronousResourceReloadListener() {

				@Override
				public Identifier getFabricId() {
					return new Identifier("yttr", "clear_thief_cache");
				}

				@Override
				public void apply(ResourceManager manager) {
					TextureColorThief.clearCache();
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "beam"), this::handleBeamPacket);
		ClientPlayNetworking.registerGlobalReceiver(new Identifier("yttr", "void_ball"), (client, handler, buf, responseSender) -> {
			float x = buf.readFloat();
			float y = buf.readFloat();
			float z = buf.readFloat();
			float r = buf.readFloat();
			mc.send(() -> {
				mc.particleManager.addParticle(new VoidBallParticle(mc.world, x, y, z, r));
			});
		});
		FabricModelPredicateProviderRegistry.register(YItems.SNARE, new Identifier("yttr", "filled"), (stack, world, entity) -> {
			return stack.hasTag() && stack.getTag().contains("Contents") ? 1 : 0;
		});
		FabricModelPredicateProviderRegistry.register(Blocks.AIR.asItem(), new Identifier("yttr", "halo"), (stack, world, entity) -> {
			return retrievingHalo ? 1 : 0;
		});
		
		WorldRenderEvents.BLOCK_OUTLINE.register(CleaverUI::render);
	}
	
	private void registerFluidRenderers() {
		FluidRenderHandler voidRenderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return new Sprite[] {
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(VOID_STILL),
					mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(VOID_FLOW)
				};
			}
			@Override
			public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return 0xFFAAAAAA;
			}
		};
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.VOID, voidRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(YFluids.FLOWING_VOID, voidRenderHandler);
	}

	private <T> void eachRegisterableField(Class<?> holder, Class<T> type, BiConsumer<Field, T> cb) {
		for (Field f : holder.getDeclaredFields()) {
			if (type.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				try {
					cb.accept(f, (T)f.get(null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void handleBeamPacket(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int entityId = buf.readInt();
		int color = buf.readInt();
		// NativeImage assumes little-endian, but our colors are big-endian, so swap red/blue
		float a = NativeImage.getAlpha(color)/255f;
		float r = NativeImage.getBlue(color)/255f;
		float g = NativeImage.getGreen(color)/255f;
		float b = NativeImage.getRed(color)/255f;
		float eX = buf.readFloat();
		float eY = buf.readFloat();
		float eZ = buf.readFloat();
		mc.send(() -> {
			Entity ent = mc.world.getEntityById(entityId);
			if (ent == null) return;
			boolean fp = ent == mc.player && mc.options.getPerspective() == Perspective.FIRST_PERSON;
			Vec3d start = RifleItem.getMuzzlePos(ent, fp);
			double len = MathHelper.sqrt(start.squaredDistanceTo(eX, eY, eZ));
			double diffX = eX-start.x;
			double diffY = eY-start.y;
			double diffZ = eZ-start.z;
			int count = (int)(len*14);
			DustParticleEffect eff = new DustParticleEffect(r, g, b, 0.2f);
			SpriteProvider sprites = ((ParticleManagerAccessor)mc.particleManager).getSpriteAwareFactories().get(Registry.PARTICLE_TYPE.getKey(ParticleTypes.DUST).get().getValue());
			for (int i = 0; i < count; i++) {
				double t = (i/(double)count);
				double x = start.x+(diffX*t);
				double y = start.y+(diffY*t);
				double z = start.z+(diffZ*t);
				final int fi = i;
				mc.particleManager.addParticle(new RedDustParticle(mc.world, x, y, z, 0, 0, 0, eff, sprites) {
					{
						if (fp && fi < 3) {
							scale /= 2;
						}
						setMaxAge((int)(Math.log10((fi*4)+5))+10);
						setColor(r, g, b);
						setColorAlpha(a);
						velocityX = 0;
						velocityY = 0;
						velocityZ = 0;
					}
					
					@Override
					protected int getColorMultiplier(float tint) {
						return LightmapTextureManager.pack(15, 15);
					}

					@Override
					public ParticleTextureSheet getType() {
						return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
					}
					
				});
			}
		});
	}
	
	public static boolean retrievingHalo = false;
	
	public void renderRifle(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		float tickDelta = mc.getTickDelta();
		matrices.pop();
		boolean fp = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.FIRST_PERSON_RIGHT_HAND;
		boolean inUse = fp && mc.player != null && mc.player.isUsingItem();
		float useTime = inUse ? ((RifleItem)stack.getItem()).calcAdjustedUseTime(stack, mc.player.getItemUseTimeLeft()-tickDelta) : 0;
		if (useTime > 80) {
			float a = (useTime-80)/40f;
			a = a*a;
			if (stack.getItem() == YItems.RIFLE_REINFORCED) {
				a /= 3;
			}
			float f = 50;
			ThreadLocalRandom tlr = ThreadLocalRandom.current();
			matrices.translate((tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a, (tlr.nextGaussian()/f)*a);
		}
		BakedModel base = mc.getBakedModelManager().getModel(BASE_MODEL);
		BakedModel chamber = mc.getBakedModelManager().getModel(CHAMBER_MODEL);
		BakedModel chamberGlass = mc.getBakedModelManager().getModel(CHAMBER_GLASS_MODEL);
		boolean leftHanded = mode == Mode.FIRST_PERSON_LEFT_HAND || mode == Mode.THIRD_PERSON_LEFT_HAND;
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, base);
		if (fp) {
			if (inUse) {
				RenderLayer layer = RenderLayer.getEntityCutoutNoCull(CHAMBER_TEXTURE);
				uvo.reset();
				for (BakedQuad quad : chamber.getQuads(null, null, ThreadLocalRandom.current())) {
					uvo.quad(matrices.peek(), quad, 1, 1, 1, 1, 1);
				}
				float minU = uvo.getMinU();
				float minV = uvo.getMinV();
				float maxU = uvo.getMaxU();
				float maxV = uvo.getMaxV();
				int frame = useTime < 70 ? (int)((useTime/70f)*36) : 34+(int)(mc.world.getTime()%2);
				mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, junk -> new DelegatingVertexConsumer(vertexConsumers.getBuffer(layer)) {
					@Override
					public VertexConsumer texture(float u, float v) {
						if (u >= minU && u <= maxU) {
							u = ((u-minU)/(maxU-minU));
						} else {
							System.out.println("U?? "+u+"; "+minU+"/"+maxU);
							u = 0;
						}
						if (v >= minV && v <= maxV) {
							v = ((frame*3)/108f)+(((v-minV)/(maxV-minV))*(3/108f));
						} else {
							System.out.println("V?? "+v+"; "+minV+"/"+maxV);
							v = 0;
						}
						return super.texture(u, v);
					}
					
					@Override
					public VertexConsumer color(int red, int green, int blue, int alpha) {
						int c = ((RifleItem)stack.getItem()).getMode(stack).color;
						return super.color(NativeImage.getBlue(c), NativeImage.getGreen(c), NativeImage.getRed(c), 255);
					}
				}, light, overlay, chamber);
				if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw(layer);
			}
		}
		RenderSystem.disableCull();
		mc.getItemRenderer().renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, chamberGlass);
		if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		RenderSystem.enableCull();
		matrices.push();
	}
	
	private final LampBlockEntityRenderer lampItemGlow = new LampBlockEntityRenderer(null);
	
	public void renderLamp(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BlockState state = ((BlockItem)stack.getItem()).getBlock().getDefaultState()
				.with(LampBlock.LIT, LampBlockItem.isInverted(stack))
				.with(LampBlock.COLOR, LampBlockItem.getColor(stack));
		matrices.translate(0.5, 0.5, 0.5);
		matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90));
		matrices.translate(-0.5, -0.5, -0.5);
		BakedModel model = mc.getBlockRenderManager().getModel(state);
        int i = mc.getBlockColors().getColor(state, null, null, 0);
        float r = (i >> 16 & 255) / 255.0F;
        float g = (i >> 8 & 255) / 255.0F;
        float b = (i & 255) / 255.0F;
        mc.getBlockRenderManager().getModelRenderer().render(matrices.peek(),
        		vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()), state, model, r, g, b, light, overlay);
        if (vertexConsumers instanceof Immediate) ((Immediate)vertexConsumers).draw();
		lampItemGlow.render(mc.world, null, state, matrices, vertexConsumers, light, overlay);
	}
	
}