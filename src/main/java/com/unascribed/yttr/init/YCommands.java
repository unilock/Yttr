package com.unascribed.yttr.init;

import com.unascribed.yttr.command.DebugTeleportCommand;
import com.unascribed.yttr.command.VoidUndoCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class YCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(VoidUndoCommand.create(ctx));
			dispatcher.register(DebugTeleportCommand.create(ctx));
		});
	}
	
}
