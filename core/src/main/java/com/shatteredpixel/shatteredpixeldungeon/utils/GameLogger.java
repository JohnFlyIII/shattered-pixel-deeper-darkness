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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Signal;

/**
 * GameLogger provides a centralized logging interface for the game.
 * It supports local logging (console/logcat) and remote logging (Bugfender),
 * with user-configurable settings to enable/disable each type.
 * On platforms that don't support remote logging (like desktop), only local
 * logging will be used regardless of settings.
 */
public class GameLogger {
    
    private static final String TAG = "ShatteredPD";
    
    // Log level indicator chars
    private static final char LEVEL_DEBUG = 'D';
    private static final char LEVEL_INFO = 'I';
    private static final char LEVEL_WARN = 'W';
    private static final char LEVEL_ERROR = 'E';
    
    // Signal that other classes can listen to for log events
    public static final Signal<LogRecord> onLog = new Signal<>();
    
    // Local flags to track initialization
    private static boolean initialized = false;
    private static boolean hasBugfender = false;
    
    // Log record class for handling log events
    public static class LogRecord {
        public final char level;
        public final String tag;
        public final String message;
        
        public LogRecord(char level, String tag, String message) {
            this.level = level;
            this.tag = tag;
            this.message = message;
        }
    }
    
    /**
     * Initializes the logging system based on current settings.
     * This should be called during app startup.
     */
    public static void init() {
        // Prevent multiple initializations
        if (initialized) return;
        
        // Attempt to detect Bugfender availability
        try {
            Class.forName("com.bugfender.sdk.Bugfender");
            hasBugfender = true;
        } catch (ClassNotFoundException e) {
            hasBugfender = false;
        }
        
        initialized = true;
    }
    
    /**
     * Logs a debug message
     */
    public static void d(String message) {
        d(TAG, message);
    }
    
    /**
     * Logs a debug message with custom tag
     */
    public static void d(String tag, String message) {
        log(LEVEL_DEBUG, tag, message);
    }
    
    /**
     * Logs an info message
     */
    public static void i(String message) {
        i(TAG, message);
    }
    
    /**
     * Logs an info message with custom tag
     */
    public static void i(String tag, String message) {
        log(LEVEL_INFO, tag, message);
    }
    
    /**
     * Logs a warning message
     */
    public static void w(String message) {
        w(TAG, message);
    }
    
    /**
     * Logs a warning message with custom tag
     */
    public static void w(String tag, String message) {
        log(LEVEL_WARN, tag, message);
    }
    
    /**
     * Logs an error message
     */
    public static void e(String message) {
        e(TAG, message);
    }
    
    /**
     * Logs an error message with custom tag
     */
    public static void e(String tag, String message) {
        log(LEVEL_ERROR, tag, message);
    }
    
    /**
     * Logs an exception with stack trace
     */
    public static void e(Throwable throwable) {
        e(TAG, throwable);
    }
    
    /**
     * Logs an exception with stack trace and custom tag
     */
    public static void e(String tag, Throwable throwable) {
        if (throwable == null) {
            e(tag, "null exception");
            return;
        }
        
        e(tag, throwable.getMessage());
        for (StackTraceElement element : throwable.getStackTrace()) {
            e(tag, "    at " + element.toString());
        }
        
        if (throwable.getCause() != null) {
            e(tag, "Caused by: " + throwable.getCause().getMessage());
            for (StackTraceElement element : throwable.getCause().getStackTrace()) {
                e(tag, "    at " + element.toString());
            }
        }
    }
    
    /**
     * Logs game state information, useful for debugging
     */
    public static void logGameState(String reason) {
        i("Game State (" + reason + "):");
        if (Dungeon.hero != null) {
            i("  Hero: " + Dungeon.hero.heroClass + " L" + Dungeon.hero.lvl + 
                    " HP:" + Dungeon.hero.HP + "/" + Dungeon.hero.HT);
            i("  Depth: " + Dungeon.depth);
            if (Dungeon.level != null) {
                i("  Level Type: " + Dungeon.level.getClass().getSimpleName());
            }
        } else {
            i("  No active game");
        }
    }
    
    /**
     * Internal logging implementation
     */
    private static void log(char level, String tag, String message) {
        if (!initialized) init();
        
        LogRecord record = new LogRecord(level, tag, message);
        
        // Send to console if local logging is enabled
        if (localLoggingEnabled()) {
            switch (level) {
                case LEVEL_DEBUG:
                    DeviceCompat.log(tag, "DEBUG: " + message);
                    break;
                case LEVEL_INFO:
                    DeviceCompat.log(tag, "INFO: " + message);
                    break;
                case LEVEL_WARN:
                    DeviceCompat.log(tag, "WARNING: " + message);
                    break;
                case LEVEL_ERROR:
                    DeviceCompat.log(tag, "ERROR: " + message);
                    break;
            }
        }
        
        // Emit the signal for any listeners
        onLog.dispatch(record);
        
        // Remote logging is handled in the Android module
    }
    
    /**
     * Whether local logging is enabled
     */
    public static boolean localLoggingEnabled() {
        return SPDSettings.localLogging();
    }
    
    /**
     * Whether remote logging is enabled
     */
    public static boolean remoteLoggingEnabled() {
        return hasBugfender && SPDSettings.remoteLogging();
    }
}