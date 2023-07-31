package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Consumer;

import com.unascribed.yttr.Yttr;
import com.unascribed.yttr.client.screen.handled.AmmoPackScreen;
import com.unascribed.yttr.client.screen.handled.CanFillerScreen;
import com.unascribed.yttr.client.screen.handled.CentrifugeScreen;
import com.unascribed.yttr.client.screen.handled.DSUScreen;
import com.unascribed.yttr.client.screen.handled.InRedOscillatorScreen;
import com.unascribed.yttr.client.screen.handled.MagtankScreen;
import com.unascribed.yttr.client.screen.handled.ProjectTableScreen;
import com.unascribed.yttr.client.screen.handled.RafterScreen;
import com.unascribed.yttr.client.screen.handled.SSDScreen;
import com.unascribed.yttr.client.screen.handled.SuitStationScreen;
import com.unascribed.yttr.client.screen.handled.VoidFilterScreen;
import com.unascribed.yttr.inventory.AmmoPackScreenHandler;
import com.unascribed.yttr.inventory.CanFillerScreenHandler;
import com.unascribed.yttr.inventory.CentrifugeScreenHandler;
import com.unascribed.yttr.inventory.DSUScreenHandler;
import com.unascribed.yttr.inventory.InRedOscillatorScreenHandler;
import com.unascribed.yttr.inventory.MagtankScreenHandler;
import com.unascribed.yttr.inventory.ProjectTableScreenHandler;
import com.unascribed.yttr.inventory.RafterScreenHandler;
import com.unascribed.yttr.inventory.SSDScreenHandler;
import com.unascribed.yttr.inventory.SuitStationScreenHandler;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class YHandledScreens {

	@Screen(CentrifugeScreen.class)
	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = new ScreenHandlerType<>(CentrifugeScreenHandler::new);
	@Screen(SuitStationScreen.class)
	public static final ScreenHandlerType<SuitStationScreenHandler> SUIT_STATION = new ScreenHandlerType<>(SuitStationScreenHandler::new);
	@Screen(VoidFilterScreen.class)
	public static final ScreenHandlerType<VoidFilterScreenHandler> VOID_FILTER = new ScreenHandlerType<>(VoidFilterScreenHandler::new);
	@Screen(DSUScreen.class)
	public static final ScreenHandlerType<DSUScreenHandler> DSU = new ScreenHandlerType<>(DSUScreenHandler::new);
	@Screen(MagtankScreen.class)
	public static final ScreenHandlerType<MagtankScreenHandler> MAGTANK = new ScreenHandlerType<>(MagtankScreenHandler::new);
	@Screen(CanFillerScreen.class)
	public static final ScreenHandlerType<CanFillerScreenHandler> CAN_FILLER = new ScreenHandlerType<>(CanFillerScreenHandler::new);
	@Screen(AmmoPackScreen.class)
	public static final ScreenHandlerType<AmmoPackScreenHandler> AMMO_PACK = new ScreenHandlerType<>(AmmoPackScreenHandler::new);
	@Screen(InRedOscillatorScreen.class)
	public static final ScreenHandlerType<InRedOscillatorScreenHandler> INRED_OSCILLATOR = new ScreenHandlerType<>(InRedOscillatorScreenHandler::new);
	@Screen(RafterScreen.class)
	public static final ScreenHandlerType<RafterScreenHandler> RAFTING = new ScreenHandlerType<>(RafterScreenHandler::new);
	@Screen(ProjectTableScreen.class)
	public static final ScreenHandlerType<ProjectTableScreenHandler> PROJECT_TABLE = new ScreenHandlerType<>(ProjectTableScreenHandler::new);
	@Screen(SSDScreen.class)
	public static final ScreenHandlerType<SSDScreenHandler> SSD = new ScreenHandlerType<>(SSDScreenHandler::new);
	
	public static void init() {
		Yttr.autoreg.autoRegister(Registries.SCREEN_HANDLER_TYPE, YHandledScreens.class, ScreenHandlerType.class);
	}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Screen {
		Class<? extends HandledScreen<?>> value();
	}

	public static void addPlayerSlots(Consumer<Slot> h, PlayerInventory playerInv, int oX, int oY) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				h.accept(new Slot(playerInv, x + y * 9 + 9, oX + (x * 18), oY + (y * 18)));
			}
		}

		for (int i = 0; i < 9; i++) {
			h.accept(new Slot(playerInv, i, oX + (i * 18), oY + 58));
		}
	}
	
}
