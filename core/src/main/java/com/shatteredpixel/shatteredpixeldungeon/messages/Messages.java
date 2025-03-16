/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/*
	Simple wrapper class for libGDX I18NBundles.

	The core idea here is that each string resource's key is a combination of the class definition and a local value.
	An object or static method would usually call this with an object/class reference (usually its own) and a local key.
	This means that an object can just ask for "name" rather than, say, "items.weapon.enchantments.death.name"
 */
public class Messages {

	private static final String PACKAGE_PREFIX = "com.shatteredpixel.shatteredpixeldungeon.";
	public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";

	private static List<I18NBundle> bundles;
	private static Languages lang;
	private static Locale locale;

	private static final Map<String, DecimalFormat> formatters = new HashMap<>();

	//Words which should not be capitalized in title case, mostly prepositions which appear ingame
	//This list is not comprehensive!
	private static final Set<String> noCaps = new HashSet<>(
			Arrays.asList("a", "an", "and", "of", "by", "to", "the", "x", "for")
	);

	private static final String[] BUNDLE_FILES = new String[]{
			Assets.Messages.ACTORS,
			Assets.Messages.ITEMS,
			Assets.Messages.JOURNAL,
			Assets.Messages.LEVELS,
			Assets.Messages.MISC,
			Assets.Messages.PLANTS,
			Assets.Messages.SCENES,
			Assets.Messages.UI,
			Assets.Messages.WINDOWS
	};

	static {
		setup(SPDSettings.language());
	}

	public static Languages lang() {
		return lang;
	}

	public static Locale locale() {
		return locale;
	}

	/**
	 * Setup Methods
	 */
	public static void setup(Languages language) {
		//seeing as missing keys are part of our process, this is faster than throwing an exception
		I18NBundle.setExceptionOnMissingKey(false);

		//store language and locale info for various string logic
		Messages.lang = language;
		Locale bundleLocal;
		if (language == Languages.ENGLISH) {
			locale = Locale.ENGLISH;
			bundleLocal = Locale.ROOT; //english is source, uses root locale for fetching bundle
		} else {
			locale = new Locale(language.code());
			bundleLocal = locale;
		}
		formatters.clear();

		//strictly match the language code when fetching bundles however
		bundles = new ArrayList<>();
		for (String file : BUNDLE_FILES) {
			bundles.add(I18NBundle.createBundle(Gdx.files.internal(file), bundleLocal));
		}
	}

	/**
	 * Resource grabbing methods
	 */
	public static String get(String key, Object... args) {
		return get(null, key, args);
	}

	public static String get(Object o, String k, Object... args) {
		return o == null ? get((Class)null, k, args) : get(o.getClass(), k, args);
	}

	public static String get(Class c, String k, Object... args) {
		return getWithRecursion(c, k, args, new ArrayList<>());
	}

	/**
	 * Helper method that implements the recursive lookup with cycle detection
	 */
	private static String getWithRecursion(Class c, String k, Object[] args, List<Class> visitedClasses) {
		// Build the key
		String fullKey = buildKey(c, k);

		// Try to get the value from bundles
		String value = getFromBundle(fullKey.toLowerCase(Locale.ENGLISH));

		if (value != null) {
			// Found a value, format if necessary and return
			return args.length > 0 ? format(value, args) : value;
		} else if (c != null && c.getSuperclass() != null && !visitedClasses.contains(c.getSuperclass())) {
			// If no value found and there's a parent class, try with it
			// Add current class to visited set to prevent cycles
			visitedClasses.add(c);
			return getWithRecursion(c.getSuperclass(), k, args, visitedClasses);
		} else {
			// No value found and no more parent classes to try, or we've already visited this class
			logMissingText(c, k);
			return NO_TEXT_FOUND;
		}
	}

	/**
	 * Build a key from a class and local key
	 */
	private static String buildKey(Class c, String k) {
		if (c != null) {
			return c.getName().replace(PACKAGE_PREFIX, "") + "." + k;
		} else {
			return k;
		}
	}

	/**
	 * Log detailed information about missing text
	 */
	private static void logMissingText(Class c, String k) {
		// Build a more informative log message
		if (c != null) {
			GLog.w("** Text not found for key: %s.%s (class: %s)",
					c.getName().replace(PACKAGE_PREFIX, ""),
					k,
					c.getSimpleName());
		} else {
			GLog.w("** Text not found for key: %s (no class provided)", k);
		}

		// Add stack trace info to see where the call came from
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length >= 4) {
			// We need to skip Messages class methods to find the actual caller
			StackTraceElement caller = stackTrace[3];
			GLog.w("** Called from: %s.%s (line %d)",
					caller.getClassName(),
					caller.getMethodName(),
					caller.getLineNumber());
		}
	}

	private static String getFromBundle(String key) {
		String result;
		for (I18NBundle b : bundles) {
			result = b.get(key);
			//if it isn't the return string for no key found, return it
			if (result.length() != key.length() + 6 || !result.contains(key)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * String Utility Methods
	 */
	public static String format(String format, Object... args) {
		try {
			return String.format(locale(), format, args);
		} catch (IllegalFormatException e) {
			ShatteredPixelDungeon.reportException(new Exception("formatting error for the string: " + format, e));
			return format;
		}
	}

	public static String decimalFormat(String format, double number) {
		if (!formatters.containsKey(format)) {
			formatters.put(format, new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale())));
		}
		return formatters.get(format).format(number);
	}

	public static String capitalize(String str) {
		if (str.length() == 0) return str;
		else return str.substring(0, 1).toUpperCase(locale) + str.substring(1);
	}

	public static String titleCase(String str) {
		if (str.length() == 0) return str;

		//English capitalizes every word except for a few exceptions
		if (lang == Languages.ENGLISH) {
			StringBuilder result = new StringBuilder();
			//split by any unicode space character
			for (String word : str.split("(?<=\\p{Zs})")) {
				if (shouldNotCapitalize(word.trim())) {
					result.append(word);
				} else {
					result.append(capitalize(word));
				}
			}
			//first character is always capitalized.
			return capitalize(result.toString());
		}

		//Otherwise, use sentence case
		return capitalize(str);
	}

	/**
	 * Helper method to determine if a word should not be capitalized in title case
	 */
	private static boolean shouldNotCapitalize(String word) {
		return noCaps.contains(word.toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", ""));
	}

	public static String upperCase(String str) {
		return str.toUpperCase(locale);
	}

	public static String lowerCase(String str) {
		return str.toLowerCase(locale);
	}
}