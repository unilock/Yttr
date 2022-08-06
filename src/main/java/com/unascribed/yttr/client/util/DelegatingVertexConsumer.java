package com.unascribed.yttr.client.util;

import com.mojang.blaze3d.vertex.VertexConsumer;

public class DelegatingVertexConsumer implements VertexConsumer {

	private final VertexConsumer delegate;
	
	public DelegatingVertexConsumer(VertexConsumer delegate) {
		this.delegate = delegate;
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		return delegate.vertex(x, y, z);
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		return delegate.color(red, green, blue, alpha);
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		return delegate.uv(u, v);
	}

	@Override
	public VertexConsumer overlay(int u, int v) {
		return delegate.overlay(u, v);
	}

	@Override
	public VertexConsumer light(int u, int v) {
		return delegate.light(u, v);
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		return delegate.normal(x, y, z);
	}

	@Override
	public void next() {
		delegate.next();
	}

	@Override
	public void fixColor(int var1, int var2, int var3, int var4) {
		delegate.fixColor(var1, var2, var3, var4);
	}

	@Override
	public void unfixColor() {
		delegate.unfixColor();
	}

}
