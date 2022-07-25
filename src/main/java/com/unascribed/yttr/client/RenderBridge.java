package com.unascribed.yttr.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL21;

import com.mojang.blaze3d.systems.RenderSystem;

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
	
}
