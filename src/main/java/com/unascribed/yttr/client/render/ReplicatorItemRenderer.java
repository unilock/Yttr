package com.unascribed.yttr.client.render;

import com.unascribed.yttr.client.IHasAClient;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ReplicatorItemRenderer extends IHasAClient {

	public static void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		NbtCompound entityTag = stack.getSubNbt("BlockEntityTag");
		ItemStack item = ItemStack.EMPTY;
		int seed = 100;
		if (entityTag != null) {
			item = ItemStack.fromNbt(entityTag.getCompound("Item"));
			seed = entityTag.getInt("Seed");
		}
		int t = 0;
		float delta = 0;
		if (mc.world != null) {
			t = (int)mc.world.getTime();
			delta = mc.getTickDelta();
		}
		ReplicatorRenderer.render(matrices, delta, seed, item, BlockPos.ORIGIN, t, null, mode == ModelTransformationMode.GUI ? -1 : 0, 0.5f);
		ReplicatorRenderer.render(matrices, delta, seed, item, BlockPos.ORIGIN, t, null, 1, 0.5f);
	}
	
}
