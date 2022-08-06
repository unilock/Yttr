// Made with Blockbench 4.2.5
// Exported for Minecraft version 1.17+ for Yarn

package com.unascribed.yttr.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.yttr.Yttr;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ZendermieModel extends EntityModel<Entity> {
	public final ModelPart head;
	private final ModelPart jaw;
	private final ModelPart inside;
	public final ModelPart arms;
	private final ModelPart arm1;
	private final ModelPart arm2;
	
	public ZendermieModel(ModelPart root) {
		this.head = root.getChild("head");
		this.arms = root.getChild("arms");
		
		this.jaw = this.head.getChild("jaw");
		this.inside = this.head.getChild("inside");
		
		this.arm1 = this.arms.getChild("arm1");
		this.arm2 = this.arms.getChild("arm2");
	}
	
	public static TexturedModelData buildModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData head = modelPartData.addChild("head", ModelPartBuilder.create()
				.uv(0, 0).cuboid(-4.0155F, -5.844F, -4.0301F, 8.0F, 8.0F, 8.0F, Dilation.NONE),
				ModelTransform.pivot(0.0155F, 13.844F, 0.0301F));

		head.addChild("jaw", ModelPartBuilder.create()
				.uv(0, 16).cuboid(-7.9536F, -4.4681F, -7.4098F, 8.0F, 8.0F, 8.0F, Dilation.NONE),
				ModelTransform.of(3.9845F, 0.656F, 3.4699F, 0.1745F, 0.0F, -0.0873F));

		head.addChild("inside", ModelPartBuilder.create()
				.uv(32, 16).cuboid(-4.0F, -0.5F, -4.0F, 8.0F, 3.0F, 8.0F, new Dilation(-0.01F)),
				ModelTransform.of(-0.0155F, 0.656F, -0.0301F, 3.1416F, 0.0F, 0.0F));

		ModelPartData arms = modelPartData.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(5.0F, 27.0F, 7.0F));

		arms.addChild("arm1", ModelPartBuilder.create()
				.uv(48, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, Dilation.NONE),
				ModelTransform.of(-7.0F, -1.0F, -14.0F, 1.2217F, -0.2618F, 0.2618F));

		arms.addChild("arm2", ModelPartBuilder.create()
				.uv(48, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, Dilation.NONE),
				ModelTransform.of(0.0F, 0.0F, 0.0F, -1.309F, 0.0873F, 0.2618F));
		
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		float t = (ageInTicks/20)%((float)Math.PI*2);
		if (Yttr.lessCreepyAwareHopper) {
			jaw.roll = 0;
		} else {
			jaw.roll = -0.0873f+MathHelper.sin(t)*0.05f;
		}
		jaw.pitch = 0.1745f+MathHelper.cos(t)*0.045f;
		head.yaw = (float)Math.toRadians(netHeadYaw);
		head.pitch = (float)Math.toRadians(headPitch);
		
		boolean crafting = limbSwing > 0;
		float vigor = crafting ? 0.2f : 0.05f;
		float fervor = crafting ? 20 : 1;
		
		arm1.yaw = MathHelper.cos(t*fervor)*vigor;
		arm1.pitch = 1.3f-(MathHelper.sin(t*fervor)*vigor);
		
		arm2.yaw = MathHelper.sin(t*fervor)*vigor;
		arm2.pitch = -1.3f-(MathHelper.cos(t*fervor)*vigor);
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		arms.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}

}