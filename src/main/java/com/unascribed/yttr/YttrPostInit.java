package com.unascribed.yttr;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.unascribed.lib39.core.api.ModPostInitializer;
import com.unascribed.yttr.init.YLatches;
import com.unascribed.yttr.util.YLog;

import com.google.common.collect.Sets;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

public class YttrPostInit implements ModPostInitializer {

	@Override
	public void onPostInitialize() {
		if (YConfig.General.fixupDebugWorld) {
			List<BlockState> states = DebugChunkGenerator.BLOCK_STATES;
			Set<BlockState> known = Sets.newHashSet(states);
			List<BlockState> newStates = Registry.BLOCK.stream()
				.flatMap(b -> b.getStateManager().getStates().stream())
				.filter(bs -> !known.contains(bs))
				.collect(Collectors.toList());
			if (newStates.isEmpty()) {
				YLog.info("Looks like someone else already fixed the debug world.", newStates.size());
			} else {
				YLog.info("Adding {} missing blockstates to the debug world.", newStates.size());
				states.addAll(newStates);
				int oldX = DebugChunkGenerator.X_SIDE_LENGTH;
				int oldZ = DebugChunkGenerator.Z_SIDE_LENGTH;
				DebugChunkGenerator.X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(states.size()));
				DebugChunkGenerator.Z_SIDE_LENGTH = MathHelper.ceil(states.size() / (float)DebugChunkGenerator.X_SIDE_LENGTH);
				YLog.info("Ok. Your debug world is now {}x{} instead of {}x{}.", DebugChunkGenerator.X_SIDE_LENGTH, DebugChunkGenerator.Z_SIDE_LENGTH, oldX, oldZ);
			}
		}
		
		YLatches.latchAll();
	}
	
}
