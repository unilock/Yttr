package com.unascribed.yttr.init;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.unascribed.yttr.client.screen.handled.AmmoPackScreen;
import com.unascribed.yttr.client.screen.handled.CanFillerScreen;
import com.unascribed.yttr.client.screen.handled.CentrifugeScreen;
import com.unascribed.yttr.client.screen.handled.DSUScreen;
import com.unascribed.yttr.client.screen.handled.InRedOscillatorScreen;
import com.unascribed.yttr.client.screen.handled.MagtankScreen;
import com.unascribed.yttr.client.screen.handled.ProjectTableScreen;
import com.unascribed.yttr.client.screen.handled.RafterScreen;
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
import com.unascribed.yttr.inventory.SuitStationScreenHandler;
import com.unascribed.yttr.inventory.VoidFilterScreenHandler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class YHandledScreens {

	@Screen(CentrifugeScreen.class)
	public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "centrifuge"), new ScreenHandlerType<>(CentrifugeScreenHandler::new));
	@Screen(SuitStationScreen.class)
	public static final ScreenHandlerType<SuitStationScreenHandler> SUIT_STATION = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "suit_station"), new ScreenHandlerType<>(SuitStationScreenHandler::new));
	@Screen(VoidFilterScreen.class)
	public static final ScreenHandlerType<VoidFilterScreenHandler> VOID_FILTER = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "void_filter"), new ScreenHandlerType<>(VoidFilterScreenHandler::new));
	@Screen(DSUScreen.class)
	public static final ScreenHandlerType<DSUScreenHandler> DSU = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "dsu"), new ScreenHandlerType<>(DSUScreenHandler::new));
	@Screen(MagtankScreen.class)
	public static final ScreenHandlerType<MagtankScreenHandler> MAGTANK = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "magtank"), new ScreenHandlerType<>(MagtankScreenHandler::new));
	@Screen(CanFillerScreen.class)
	public static final ScreenHandlerType<CanFillerScreenHandler> CAN_FILLER = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "can_filler"), new ScreenHandlerType<>(CanFillerScreenHandler::new));
	@Screen(AmmoPackScreen.class)
	public static final ScreenHandlerType<AmmoPackScreenHandler> AMMO_PACK = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "ammo_pack"), new ScreenHandlerType<>(AmmoPackScreenHandler::new));
	@Screen(InRedOscillatorScreen.class)
	public static final ScreenHandlerType<InRedOscillatorScreenHandler> INRED_OSCILLATOR = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "inred_oscillator"), new ScreenHandlerType<>(InRedOscillatorScreenHandler::new));
	@Screen(RafterScreen.class)
	public static final ScreenHandlerType<RafterScreenHandler> RAFTING = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "rafter"), new ScreenHandlerType<>(RafterScreenHandler::new));
	@Screen(ProjectTableScreen.class)
	public static final ScreenHandlerType<ProjectTableScreenHandler> PROJECT_TABLE = Registry.register(Registry.SCREEN_HANDLER, new Identifier("yttr", "project_table"), new ScreenHandlerType<>(ProjectTableScreenHandler::new));
	
	public static void init() {}
	
	@Retention(RUNTIME)
	@Target(FIELD)
	public @interface Screen {
		Class<? extends HandledScreen<?>> value();
	}
	
}
