package com.unascribed.yttr;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.unascribed.lib39.core.api.AutoMixin;
import com.unascribed.yttr.mixinsupport.ClassNodeTransformer;
import com.unascribed.yttr.util.YLog;

public class YttrMixin extends AutoMixin {
	
	public @interface Transformer {
		Class<? extends ClassNodeTransformer> value();
	}
	
	private final Map<String, ClassNodeTransformer> transformers = new HashMap<>();
	
	@Override
	protected boolean shouldMixinBeSkipped(String name, ClassNode node) {
		if (name.contains(".Transformer")) {
			return true;
		}
		return super.shouldMixinBeSkipped(name, node);
	}
	
	@Override
	protected boolean shouldAnnotationSkipMixin(String name, AnnotationNode an) {
		if (an.desc.equals("Lcom/unascribed/yttr/YttrMixin$Transformer;")) {
			var params = decodeAnnotationParams(an);
			Type type = (Type)params.get("value");
			try {
				transformers.put(name, (ClassNodeTransformer) Class.forName(type.getClassName()).newInstance());
			} catch (Exception e) {
				YLog.error("Transformer class for mixin {} not found", name, e);
			}
		}
		return super.shouldAnnotationSkipMixin(name, an);
	}
	
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		transformers.getOrDefault(mixinClassName, (s, cn) -> {}).transform(targetClassName, targetClass);
		super.postApply(targetClassName, targetClass, mixinClassName, mixinInfo);
	}
	
}
