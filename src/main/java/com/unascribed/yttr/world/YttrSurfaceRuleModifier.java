package com.unascribed.yttr.world;

import java.util.function.BiConsumer;

import com.unascribed.lib39.crowbar.api.SurfaceRuleModifier;
import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.init.YBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

public class YttrSurfaceRuleModifier implements SurfaceRuleModifier {

	@Override
	public void modifyDirtSurfaceRules(BiConsumer<Identifier, BlockState> out) {
		out.accept(Yttr.id("wasteland"), YBlocks.WASTELAND_DIRT.getDefaultState());
	}

	@Override
	public void modifyGrassSurfaceRules(BiConsumer<Identifier, BlockState> out) {
		out.accept(Yttr.id("wasteland"), YBlocks.WASTELAND_DIRT.getDefaultState());
	}

}
