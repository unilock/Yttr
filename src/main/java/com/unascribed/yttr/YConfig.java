package com.unascribed.yttr;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.unascribed.yttr.mechanics.LampColor;
import com.unascribed.yttr.util.QDCSS;
import com.unascribed.yttr.util.QDCSS.SyntaxErrorException;
import com.unascribed.yttr.util.YLog;

import com.google.common.base.Ascii;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;


public class YConfig {

	public enum Trilean {
		AUTO,
		ON,
		OFF,
		;
		public boolean resolve(boolean def) {
			if (this == AUTO) return def;
			return this == ON;
		}
	}

	public enum TrileanSoft {
		ON,
		SOFT,
		OFF,
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Key {
		String value();
	}

	public static final QDCSS defaults;
	public static QDCSS data;
	
	private static final List<Class<?>> sections = List.of(
			General.class, Client.class, Rifle.class, Enchantments.class, WorldGen.class, Debug.class
		);
	private static final Map<String, Class<?>> keyTypes = new HashMap<>();
	
	static {
		URL defaultsUrl = YConfig.class.getResource("/yttr-default.css");
		try {
			defaults = QDCSS.load(defaultsUrl);
		} catch (IOException e) {
			throw new Error("Could not load config defaults", e);
		}
		load();
		save();
	}
	
	public static Class<?> getKeyType(String key) {
		return keyTypes.getOrDefault(key, void.class);
	}
	
	public static void copyFieldsToData() {
		for (Class<?> section : sections) {
			for (Field f : section.getFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					Key k = f.getAnnotation(Key.class);
					if (k != null) {
						try {
							if (f.getType() == boolean.class) {
								data.put(k.value(), f.getBoolean(null) ? "on" : "off");
							} else if (f.getType().isEnum()) {
								data.put(k.value(), Ascii.toLowerCase(((Enum<?>)f.get(null)).name()));
							}
						} catch (Exception e) {
							YLog.error("Could not serialize config", e);
						}
					}
				}
			}
		}
	}
	
	public static void copyDataToFields() {
		for (Class<?> section : sections) {
			for (Field f : section.getFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					Key k = f.getAnnotation(Key.class);
					if (k != null) {
						try {
							keyTypes.put(k.value(), f.getType());
							if (f.getType() == boolean.class) {
								f.set(null, data.getBoolean(k.value()).get());
							} else if (f.getType().isEnum()) {
								f.set(null, data.getEnum(k.value(), (Class)f.getType()).get());
							}
						} catch (Exception e) {
							YLog.error("Could not memoize config", e);
						}
					}
				}
			}
		}
	}
	
	public static void load() {
		File cfg = new File("config/yttr.css");
		if (!cfg.exists()) {
			data = defaults.copy();
			save();
		} else {
			QDCSS dataTmp;
			try {
				dataTmp = QDCSS.load(cfg);
			} catch (IOException e) {
				YLog.error("IO error when reading configuration. Using defaults", e);
				dataTmp = defaults;
			} catch (SyntaxErrorException e) {
				YLog.error("Syntax error in configuration: {}. Using defaults", e.getMessage());
				dataTmp = defaults;
			}
			data = defaults.merge(dataTmp);
		}
		copyDataToFields();
	}
	
	public static void save() {
		copyFieldsToData();
		URL templateUrl = YConfig.class.getResource("/yttr-template.css");
		File cfg = new File("config/yttr.css");
		try {
			Files.createParentDirs(cfg);
			String s = Resources.asCharSource(templateUrl, Charsets.UTF_8).read();
			for (Map.Entry<String, String> en : data.flatten().entrySet()) {
				s = s.replace("var("+en.getKey()+")", en.getValue());
			}
			Files.asCharSink(cfg, Charsets.UTF_8).write(s);
		} catch (IOException e) {
			YLog.error("IO error when copying default configuration", e);
		}
	}
	
	static void touch() {}
	
	public static final class General {
		@Key("general.trust-players")
		public static boolean trustPlayers     = false;
		@Key("general.break-bedrock-anywhere")
		public static boolean breakBedrockAnywhere = false;
		@Key("general.shenanigans")
		public static boolean shenanigans      = true;
		@Key("general.convert-void-holes")
		public static boolean convertVoidHoles = true;
		
		static { touch(); }
		
		private General() {}
	}
	
	public static final class Client {
		@Key("client.force-opengl-core")
		public static Trilean forceOpenGLCore = Trilean.AUTO;
		@Key("client.config-color")
		public static LampColor configColor = LampColor.TEAL;
		@Key("client.control-hints")
		public static boolean controlHints = true;
		
		static { touch(); }
		
		private Client() {}
	}
	
	public static final class Rifle {
		@Key("rifle.allow-void")
		public static boolean allowVoid    = true;
		@Key("rifle.allow-explode")
		public static TrileanSoft allowExplode = TrileanSoft.ON;
		@Key("rifle.allow-fire")
		public static boolean allowFire    = true;
		@Key("rifle.timing-assist")
		public static boolean timingAssist = true;
		
		static { touch(); }
		
		private Rifle() {}
	}
	
	public static final class Enchantments {
		@Key("enchantments.vorpal")
		public static boolean vorpal = true;
		@Key("enchantments.disjunction")
		public static boolean disjunction = true;
		@Key("enchantments.annihilation")
		public static boolean annihilation = true;
		@Key("enchantments.shattering")
		public static boolean shattering = true;
		@Key("enchantments.springing")
		public static boolean springing = true;
		@Key("enchantments.stabilization")
		public static boolean stabilization = true;
		@Key("enchantments.curses-in-table")
		public static boolean cursesInTable = true;
		
		static { touch(); }
		
		private Enchantments() {}
	}
	
	public static final class WorldGen {
		@Key("worldgen.gadolinite")
		public static boolean gadolinite = true;
		@Key("worldgen.brookite")
		public static boolean brookite   = true;
		
		@Key("worldgen.squeeze-trees")
		public static boolean squeezeTrees = true;
		@Key("worldgen.wasteland")
		public static boolean wasteland    = true;
		
		@Key("worldgen.core-lava")
		public static boolean coreLava = true;
		@Key("worldgen.scorched")
		public static boolean scorched  = true;
		
		@Key("worldgen.continuity")
		public static boolean continuity = true;
		
		@Key("worldgen.scorched-retrogen")
		public static boolean scorchedRetrogen = true;
		
		static { touch(); }
		
		private WorldGen() {}
	}
	
	public static final class Debug {
		
		static { touch(); }
		
		private Debug() {}
	}
	
}
