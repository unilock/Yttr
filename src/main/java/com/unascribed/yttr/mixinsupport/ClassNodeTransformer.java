package com.unascribed.yttr.mixinsupport;

import org.objectweb.asm.tree.ClassNode;

public interface ClassNodeTransformer {

	void transform(String name, ClassNode node);
	
}
