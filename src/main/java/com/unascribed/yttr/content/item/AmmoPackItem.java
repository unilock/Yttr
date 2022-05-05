package com.unascribed.yttr.content.item;

import java.util.List;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YItems;
import com.unascribed.yttr.inventory.AmmoPackScreenHandler;
import com.unascribed.yttr.util.InventoryProviderItem;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

public class AmmoPackItem extends TrinketItem implements InventoryProviderItem {

	public AmmoPackItem(Settings settings) {
		super(settings);
	}
	
	public int getSize(ItemStack pack) {
		return 6;
	}
	
	public void setStack(ItemStack pack, int i, ItemStack stack) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasNbt()) pack.setNbt(new NbtCompound());
		NbtList inv = pack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		inv.set(i, stack.isEmpty() ? new NbtCompound() : stack.writeNbt(new NbtCompound()));
		pack.getNbt().put("Contents", inv);
	}

	public ItemStack getStack(ItemStack pack, int i) {
		int size = getSize(pack);
		if (i < 0 || i >= size) throw new IndexOutOfBoundsException(""+i);
		if (!pack.hasNbt()) return ItemStack.EMPTY;
		NbtList inv = pack.getNbt().getList("Contents", NbtType.COMPOUND);
		ensureSize(inv, size);
		NbtCompound comp = inv.getCompound(i);
		if (comp.getSize() == 0) return ItemStack.EMPTY;
		return ItemStack.fromNbt(comp);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		for (int i = 0; i < getSize(stack); i++) {
			ItemStack is = getStack(stack, i);
			if (!is.isEmpty()) {
				tooltip.add(is.getName());
				is.getItem().appendTooltip(is, world, tooltip, context);
			}
		}
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isSneaking()) {
			ItemStack stack = player.getStackInHand(hand);
			player.openHandledScreen(new NamedScreenHandlerFactory() {
				
				@Override
				public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
					return new AmmoPackScreenHandler(asInventory(stack), syncId, inv);
				}
				
				@Override
				public Text getDisplayName() {
					return stack.getName();
				}
			});
			return TypedActionResult.consume(stack);
		}
		return super.use(world, player, hand);
	}
	
	public void clear(ItemStack pack) {
		if (pack.hasNbt()) pack.getNbt().remove("Contents");
	}
	
	@Override
	public Inventory asInventory(ItemStack pack) {
		return new Inventory() {
			
			@Override
			public void clear() {
				AmmoPackItem.this.clear(pack);
			}
			
			@Override
			public int size() {
				return AmmoPackItem.this.getSize(pack);
			}
			
			@Override
			public void setStack(int slot, ItemStack stack) {
				AmmoPackItem.this.setStack(pack, slot, stack);
			}
			
			@Override
			public ItemStack getStack(int slot) {
				return AmmoPackItem.this.getStack(pack, slot);
			}
			
			@Override
			public ItemStack removeStack(int slot, int amount) {
				ItemStack content = getStack(slot);
				ItemStack res = content.split(amount);
				setStack(slot, content);
				return res;
			}
			
			@Override
			public ItemStack removeStack(int slot) {
				ItemStack content = getStack(slot);
				setStack(slot, ItemStack.EMPTY);
				return content;
			}
			
			@Override
			public void markDirty() {
			}
			
			@Override
			public boolean isEmpty() {
				for (int i = 0; i < size() ; i++) {
					if (!getStack(i).isEmpty()) return false;
				}
				return true;
			}
			
			@Override
			public boolean canPlayerUse(PlayerEntity player) {
				return true;
			}
		};
	}
	
	protected static void ensureSize(NbtList li, int size) {
		if (li.size() < size) {
			for (int j = 0; j < size-li.size(); j++) {
				li.add(new NbtCompound());
			}
		}
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.BACKPACK);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void render(String trinketsSlot, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light,
			PlayerEntityModel<AbstractClientPlayerEntity> model, AbstractClientPlayerEntity player, float headYaw, float headPitch) {
		if (player.getEquippedStack(EquipmentSlot.CHEST).getItem() == YItems.SUIT_CHESTPLATE) return;
		ItemStack is = TrinketsApi.getTrinketComponent(player).getStack(trinketsSlot);
		BakedModel bm = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr:ammo_pack_model#inventory"));
		matrices.push();
			model.body.rotate(matrices);
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
			matrices.translate(-8/16f, -12/16f, 2/16f);
			VertexConsumer vc = vertexConsumer.getBuffer(RenderLayer.getEntityCutoutNoCull(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
			for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
				vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
			}
			for (Direction d : Direction.values()) {
				int i = d.ordinal();
				ItemStack slot = getStack(is, i);
				if (!slot.isEmpty()) {
					int color = slot.getItem() instanceof AmmoCanItem ? ((AmmoCanItem)slot.getItem()).getColor(slot, 1) : 0xFF284946;
					float r = ((color>>16)&0xFF)/255f;
					float g = ((color>> 8)&0xFF)/255f;
					float b = ((color>> 0)&0xFF)/255f;
					for (BakedQuad bq : bm.getQuads(Blocks.DIRT.getDefaultState(), d, RANDOM)) {
						vc.quad(matrices.peek(), bq, bq.hasColor() ? r : 1, bq.hasColor() ? g : 1, bq.hasColor() ? b : 1, light, OverlayTexture.DEFAULT_UV);
					}
				}
			}
			float chest = 0;
			try {
				chest = Yttr.earsAccess.getChestSize(player);
			} catch (Throwable t) {}
			BakedModel seg = MinecraftClient.getInstance().getBakedModelManager().getModel(new ModelIdentifier("yttr:ammo_pack_seg_model#inventory"));
			matrices.translate(3.5f/16f, 11f/16f, -4.5f/16f);
			if (chest > 0) {
				float ang = chest*45;
				
				matrices.push();
					matrices.push();
						for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
							vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
						}
					matrices.pop();
					
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(ang));
					matrices.translate(0, -5.5f/16f, 0);
					matrices.push();
						matrices.scale(1, 5.5f, 1);
						for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
							vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
						}
					matrices.pop();
					
					float scale = 5.5f;
					float angr = (float)Math.toRadians(ang);
					float len = (float)(scale * Math.tan(angr));
					float h = scale/MathHelper.cos(angr);
					matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
					matrices.translate(0, -len/16f, 0);
					matrices.push();
						matrices.scale(1, len, 1);
						for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
							vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
						}
					matrices.pop();
				matrices.pop();
				
				matrices.translate(0, -7.5f/16f, 0);
				matrices.push();
					matrices.scale(1, (12-h)-4.5f, 1);
					for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
						vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
					}
				matrices.pop();
			} else {
				for (int y = 0; y < 10; y++) {
					for (BakedQuad bq : seg.getQuads(Blocks.DIRT.getDefaultState(), null, RANDOM)) {
						vc.quad(matrices.peek(), bq, 1, 1, 1, light, OverlayTexture.DEFAULT_UV);
					}
					matrices.translate(0, -1/16f, 0);
				}
			}
		matrices.pop();
	}

}
