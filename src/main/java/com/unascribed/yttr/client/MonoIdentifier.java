package com.unascribed.yttr.client;

import net.minecraft.util.Identifier;

public class MonoIdentifier extends Identifier {

	public MonoIdentifier(Identifier id) {
		super(id.getNamespace(), id.getPath().replace("--mono-", ""));
	}

	public MonoIdentifier(String namespace, String path) {
		super(namespace, path);
	}
	

}
