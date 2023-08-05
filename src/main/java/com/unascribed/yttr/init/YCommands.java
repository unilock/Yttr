package com.unascribed.yttr.init;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.command.VoidUndoCommand;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;

public class YCommands {

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, env) -> {
			dispatcher.register(VoidUndoCommand.create(ctx));
			dispatcher.register(CommandManager.literal("yttr:prefer_survival_inventory")
					.requires(scs -> scs.isPlayer() && scs.getPlayer().isCreative() && Yttr.isEnlightened(scs.getPlayer(), false))
					.executes((cctx) -> {
						var is = cctx.getSource().getPlayer().getEquippedStack(EquipmentSlot.HEAD);
						if (!is.hasNbt()) is.setNbt(new NbtCompound());
						is.getNbt().putBoolean("PreferSurvival", true);
						return 1;
					}));
			dispatcher.register(CommandManager.literal("yttr:prefer_creative_inventory")
					.requires(scs -> scs.isPlayer() && scs.getPlayer().isCreative() && Yttr.isEnlightened(scs.getPlayer(), false))
					.executes((cctx) -> {
						var is = cctx.getSource().getPlayer().getEquippedStack(EquipmentSlot.HEAD);
						if (!is.hasNbt()) is.setNbt(new NbtCompound());
						is.getNbt().putBoolean("PreferSurvival", false);
						return 1;
					}));
		});
	}
	
}
