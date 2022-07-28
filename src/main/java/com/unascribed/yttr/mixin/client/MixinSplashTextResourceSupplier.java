package com.unascribed.yttr.mixin.client;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.system.Platform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.YConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
@Mixin(value=SplashTextResourceSupplier.class, priority=80000)
public class MixinSplashTextResourceSupplier {
	
	@Shadow @Final
	private List<String> splashTexts;

	@Inject(at=@At("RETURN"), method="apply")
	protected void apply(List<String> li, ResourceManager mgr, Profiler prof, CallbackInfo ci) {
		if (YConfig.Client.openglCompatibility.resolve(Platform.get() != Platform.MACOSX)) {
			if (Collections.replaceAll(splashTexts, "Now on OpenGL 3.2 core profile!",
					"Now on OpenGL 3.2 §mcore§r §ocompatibility§r profile!")) {
				splashTexts.add("Core profile? More like snore profile!");
			}
		}
		Set<String> necessarySplashes = Sets.newHashSet(
			"4876b3073f7da15ac4a688e9a9f9fcb5f1e29f1e701376f32faa49510115f1a8",
			"3ebbe99b5d2e5b00e44698ff6d37c91ece29e830f1005dda050c78d73a18f61e",
			"eb508b317d829b020f5a7b8f56b3e8bb48c04f4f1cd15fbefbb6fb949ba1d5e9",
			"c85138d38af4434552bc8cad3c48d279b0b0c35874beb4c5dcfbecabe6f74d8b",
			"2bfddfdb398a3fda965c7ba3b0b75f7e6d608366fdaad043712ef2730457ddee",
			"afd468a6303057d4e3d6d3c7d0f3c2301f77b60fc3a3dcfa0706380ad7c33df5",
			"36358601e4faea4b7dc1e634126f186fb7472701641a5120148fd4a7a6ca8b95",
			"65834796f8efd80d4b2ad433f693c67f4c9b2d2ae5703651952aa0838090dd77"
		);
		for (String s : splashTexts) {
			necessarySplashes.remove(Hashing.sha256().hashString(s+"2ab4850e297d889e", Charsets.UTF_8).toString());
		}
		if (!necessarySplashes.isEmpty()) {
			MinecraftClient.getInstance().particleManager = null;
		}
		if (YConfig.General.shenanigans) {
			splashTexts.remove("The true meaning of covfefe");
			Collections.replaceAll(splashTexts, "Don't bother with the clones!",
					"Try the clones!");
			Collections.replaceAll(splashTexts, "Closed source!",
					"Effectively visible source!");
			Collections.replaceAll(splashTexts, "Lennart lennart = new Lennart()",
					"§7Lennart §flennart §6= §1new §7Lennart§6();");
			splashTexts.addAll(List.of(
					"Also try Minetest!",
					"Also try Terasology!",
					"Also try Vintage Story!",
					"Also try ZZT!",
					"Also try MegaZeux!",
					"Also try Xonotic!",
					
					"Now with everybody's favorite Bloque® Brand Plastic Construction Building Bricks!",
					
					"Vertical!",
					
					"§9var §flen §6= §9new §7Lennart§6(); §2// DRY",
					"", // the scariest splash is no splash at all
					"Now with Void!"
				));
		}
	}
	
	@Inject(at=@At("HEAD"), method="get", cancellable=true)
	public void get(CallbackInfoReturnable<String> ci) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		if (month == 6 && ThreadLocalRandom.current().nextInt(8) == 0) {
			ci.setReturnValue("§8B§ce §6g§ea§2y §9d§5o §bc§dr§fim§de§bs§r!");
			return;
		}
		if (!YConfig.General.shenanigans) return;
		if (month == 1 && day == 28) {
			ci.setReturnValue("Happy birthday, Kat!");
		} else if (month == 10 && day == 29) {
			ci.setReturnValue("Happy birthday, Una!");
		}
	}
	
}
