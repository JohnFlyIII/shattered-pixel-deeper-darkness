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

package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Signal;

public class GLog {

	public static final String TAG = "GAME";

	public static final String POSITIVE = "++ ";
	public static final String NEGATIVE = "-- ";
	public static final String WARNING = "** ";
	public static final String HIGHLIGHT = "@@ ";
	public static final String DEBUG = "DB ";
	public static final String NEW_LINE = "\n";

	public static Signal<String> update = new Signal<>();

	public static void newLine() {
		update.dispatch(NEW_LINE);
	}

	public static void i(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		GameLogger.i("Info", text);
		DeviceCompat.log(TAG, text);
		update.dispatch(text);
	}

	public static void p(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		String prefixedText = POSITIVE + text;

		GameLogger.i("Positive", text);
		DeviceCompat.log(TAG, prefixedText);
		update.dispatch(prefixedText);
	}

	public static void n(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		String prefixedText = NEGATIVE + text;

		GameLogger.i("Negative", text);
		DeviceCompat.log(TAG, prefixedText);
		update.dispatch(prefixedText);
	}

	public static void w(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		String prefixedText = WARNING + text;

		GameLogger.w("Warning", text);
		DeviceCompat.log(TAG, prefixedText);
		update.dispatch(prefixedText);
	}

	public static void h(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		String prefixedText = HIGHLIGHT + text;

		GameLogger.i("Highlight", text);
		DeviceCompat.log(TAG, prefixedText);
		update.dispatch(prefixedText);
	}

	public static void d(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		String prefixedText = DEBUG + text;

		GameLogger.d("Debug", text);
		DeviceCompat.log(TAG, prefixedText);
		update.dispatch(prefixedText);
	}

	/**
	 * Logs an error message
	 */
	public static void e(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		GameLogger.e("Error", text);
		DeviceCompat.log(TAG, "ERROR: " + text);
		update.dispatch("ERROR: " + text);
	}

	/**
	 * Logs a message with formatted arguments but doesn't dispatch to the game UI
	 * Useful for debug logging that shouldn't appear in-game
	 */
	public static void logOnly(String text, Object... args) {
		if (args.length > 0) {
			text = Messages.format(text, args);
		}

		GameLogger.d("LogOnly", text);
		DeviceCompat.log(TAG, "LOG: " + text);
	}
}