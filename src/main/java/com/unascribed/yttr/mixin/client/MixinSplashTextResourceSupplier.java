package com.unascribed.yttr.mixin.client;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.yttr.YConfig;
import com.unascribed.yttr.client.RenderBridge;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
		if (RenderBridge.canUseCompatFunctions()) {
			if (Collections.replaceAll(splashTexts, "Now on OpenGL 3.2 core profile!",
					"Now on OpenGL 3.2 §mcore§r §ocompatibility§r profile!")) {
				splashTexts.add("Core profile? More like snore profile!");
			}
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
