package com.unascribed.yttr.client;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL21;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.yttr.mixin.accessor.client.AccessorRenderSystem;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class RenderBridge extends GL21 {

	private static final FloatBuffer MATRIX_BUFFER = BufferUtils.createFloatBuffer(4*4);
	
	public static void glMultMatrixf(Matrix4f mat) {
		mat.writeColumnMajor(MATRIX_BUFFER);
		MATRIX_BUFFER.rewind();
		glMultMatrixf(MATRIX_BUFFER);
	}
	
	public static void glDefaultBlendFunc() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void glPushMCMatrix() {
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glMultMatrixf(RenderSystem.getProjectionMatrix());
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glMultMatrixf(RenderSystem.getModelViewMatrix());
	}
	
	public static void glPushMCMatrix(MatrixStack matrices) {
		glPushMCMatrix();
		glMultMatrixf(matrices.peek().getModel());
	}
	
	public static void glPopMCMatrix() {
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
	}
	
	public static void glCopyMCLight() {
		Vec3f[] lights = AccessorRenderSystem.yttr$getShaderLightDirections();
		glPushMatrix();
		glLoadIdentity();
		glEnable(GL_LIGHT0);
		glEnable(GL_LIGHT1);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[] {0.6f, 0.6f, 0.6f, 1.0f});
		glLightfv(GL_LIGHT0, GL_AMBIENT, new float[] {0.0f, 0.0f, 0.0f, 1.0f});
		glLightfv(GL_LIGHT0, GL_SPECULAR, new float[] {0.0f, 0.0f, 0.0f, 1.0f});
		glLightfv(GL_LIGHT0, GL_POSITION, new float[] {lights[0].getX(), lights[0].getY(), lights[0].getZ(), 0});
		glLightfv(GL_LIGHT1, GL_DIFFUSE, new float[] {0.6f, 0.6f, 0.6f, 1.0f});
		glLightfv(GL_LIGHT1, GL_AMBIENT, new float[] {0.0f, 0.0f, 0.0f, 1.0f});
		glLightfv(GL_LIGHT1, GL_SPECULAR, new float[] {0.0f, 0.0f, 0.0f, 1.0f});
		glLightfv(GL_LIGHT1, GL_POSITION, new float[] {lights[1].getX(), lights[1].getY(), lights[1].getZ(), 0});
		glLightModelfv(GL_LIGHT_MODEL_AMBIENT, new float[] {0.4F, 0.4F, 0.4F, 1.0F});
		glPopMatrix();
	}
	
}
