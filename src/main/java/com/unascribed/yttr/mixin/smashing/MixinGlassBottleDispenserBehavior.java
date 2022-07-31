package com.unascribed.yttr.mixin.smashing;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.unascribed.yttr.crafting.PistonSmashingRecipe;
import com.unascribed.yttr.mechanics.SmashCloudLogic;

import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;

@Mixin(targets={
	"net/minecraft/block/dispenser/DispenserBehavior$C_tfeacyls",
	"net/minecraft/block/dispenser/DispenserBehavior$17"
})
public abstract class MixinGlassBottleDispenserBehavior extends FallibleItemDispenserBehavior {

	// Mixin has an absolute *fit* about this class due to weirdness with QMoL and anonymous inner classes.
	// Let's just use a method handle and spell out every possible obf signature.
	@Unique
	private MethodHandle tryPutFilledBottle;
	
	@Inject(at=@At("HEAD"), method={
			"(Lgk;Lbuw;)Lbuw;", // obf (just in case)
			"method_10135(Lnet/minecraft/class_2342;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1799;", // intermediary
			"m_clhynwvb(Lnet/minecraft/C_wzdnszcs;Lnet/minecraft/C_sddaxwyk;)Lnet/minecraft/C_sddaxwyk;", // hashed
			"dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", // quilt mappings
	}, cancellable=true, remap=false)
	public void dispenseSilently(BlockPointer ptr, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) {
		PistonSmashingRecipe r = SmashCloudLogic.consumeGasCloud(ptr.getWorld(), new Box(ptr.getPos()).expand(0.5));
		if (r != null) {
			setSuccess(true);
			if (tryPutFilledBottle == null) {
				Method tpfb = null;
				var sig = List.of(BlockPointer.class, ItemStack.class, ItemStack.class);
				for (Method m : getClass().getDeclaredMethods()) {
					if (Arrays.asList(m.getParameterTypes()).equals(sig)) {
						tpfb = m;
						break;
					}
				}
				if (tpfb == null) throw new AssertionError("Can't find tryPutFilledBottle");
				try {
					tryPutFilledBottle = MethodHandles.lookup().unreflect(tpfb);
				} catch (IllegalAccessException e) {
					throw new AssertionError(e);
				}
			}
			try {
				ci.setReturnValue((ItemStack)tryPutFilledBottle.invoke(this, ptr, stack, r.getCloudOutput().copy()));
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
