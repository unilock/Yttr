package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.command.VoidUndoCommand;
import com.unascribed.yttr.mixinsupport.DiverPlayer;
import com.unascribed.yttr.world.GeysersState;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class YCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(VoidUndoCommand.create(ctx));
			dispatcher.register(CommandManager.literal("yttr:discover_geyser")
				.requires(scs -> scs.hasPermissionLevel(2))
				.then(CommandManager.argument("players", EntityArgumentType.players())
					.then(CommandManager.argument("geyser", UuidArgumentType.uuid())
						.suggests((cctx, bldr) -> {
							for (var g : GeysersState.get(cctx.getSource().getWorld()).getGeysers()) {
								if (g.id.toString().startsWith(bldr.getRemaining())) {
									bldr.suggest(g.id.toString());
								}
							}
							return bldr.buildFuture();
						})
						.executes((cctx) -> {
							int count = 0;
							for (var player : EntityArgumentType.getPlayers(cctx, "players")) {
								if (Yttr.discoverGeyser(UuidArgumentType.getUuid(cctx, "geyser"), player,
										player instanceof DiverPlayer dp ? dp.yttr$isDiving() : false)) {
									count++;
								}
							}
							if (count == 0) {
								cctx.getSource().sendError(Text.literal("All the players already know that geyser or it was not valid"));
							} else {
								var t = Text.literal("Discovered geyser for "+count+" player"+(count == 1 ? "" : "s"));
								cctx.getSource().sendFeedback(() -> t, true);
							}
							return count;
						})
					)
				)
			);
			dispatcher.register(CommandManager.literal("yttr:prefer_survival_inventory")
				.requires(scs -> scs.isPlayer() && scs.getPlayer().isCreative() && Yttr.isEnlightened(scs.getPlayer(), false))
				.executes((cctx) -> {
					var is = cctx.getSource().getPlayer().getEquippedStack(EquipmentSlot.HEAD);
					if (!is.hasNbt()) is.setNbt(new NbtCompound());
					is.getNbt().putBoolean("PreferSurvival", true);
					return 1;
				})
			);
			dispatcher.register(CommandManager.literal("yttr:prefer_creative_inventory")
				.requires(scs -> scs.isPlayer() && scs.getPlayer().isCreative() && Yttr.isEnlightened(scs.getPlayer(), false))
				.executes((cctx) -> {
					var is = cctx.getSource().getPlayer().getEquippedStack(EquipmentSlot.HEAD);
					if (!is.hasNbt()) is.setNbt(new NbtCompound());
					is.getNbt().putBoolean("PreferSurvival", false);
					return 1;
				})
			);
		});
	}
	
}
