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

package com.shatteredpixel.shatteredpixeldungeon.android;

import android.content.Context;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.bugfender.sdk.LogLevel;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.utils.GameLogger;
import com.watabou.utils.Signal;

/**
 * Android implementation of the GameLogger that connects to Bugfender
 */
public class AndroidGameLogger {
    
    private static final String TAG = "ShatteredPD";
    private static boolean initialized = false;
    
    /**
     * Initializes Bugfender for Android devices
     */
    public static void initBugfender(Context context) {
        if (initialized) return;
        
        try {
            // Initialize Bugfender - always use debug mode for now
            Bugfender.init(context, "tnPDmLKTLQSskiYKlgZMZ1D1mD6ss17J", true, true);
            Bugfender.enableCrashReporting();
            // UI Event logging requires Application context
            if (context instanceof android.app.Application) {
                Bugfender.enableUIEventLogging((android.app.Application)context);
            }
            
            // Log device and app info
            Bugfender.d(TAG, "Logging initialized");
            
            // Set up listener for GameLogger messages
            GameLogger.onLog.add(new Signal.Listener<GameLogger.LogRecord>() {
                @Override
                public boolean onSignal(GameLogger.LogRecord record) {
                    if (GameLogger.remoteLoggingEnabled()) {
                        switch (record.level) {
                            case 'D':
                                Bugfender.d(record.tag, record.message);
                                break;
                            case 'I':
                                Bugfender.i(record.tag, record.message);
                                break;
                            case 'W':
                                Bugfender.w(record.tag, record.message);
                                break;
                            case 'E':
                                Bugfender.e(record.tag, record.message);
                                break;
                        }
                    }
                    return false;
                }
            });
            
            // Set up a custom uncaught exception handler
            final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    // Log the crash to Bugfender
                    Bugfender.e(TAG, "FATAL CRASH: " + ex.getMessage());
                    Bugfender.e(TAG, Log.getStackTraceString(ex));
                    GameLogger.logGameState("CRASH");
                    
                    // Send logs to Bugfender server before crash
                    Bugfender.forceSendOnce();
                    
                    // Let the default handler deal with the crash
                    if (defaultHandler != null) {
                        defaultHandler.uncaughtException(thread, ex);
                    }
                }
            });
            
            Log.i(TAG, "Bugfender initialized successfully");
            GameLogger.i("Bugfender initialized successfully");
            
            initialized = true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Bugfender", e);
        }
    }
    
    /**
     * Get the Bugfender device URL for support purposes
     */
    public static String getDeviceUrl() {
        if (!initialized) return null;
        
        try {
            return Bugfender.getDeviceUrl().toString();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get Bugfender device URL", e);
            return null;
        }
    }
    
    /**
     * Get the Bugfender device identifier
     */
    public static String getDeviceId() {
        if (!initialized) return null;
        
        try {
            // Device ID may not be available in the current version
            // return a placeholder instead
            return "device-id-unavailable";
        } catch (Exception e) {
            Log.e(TAG, "Failed to get Bugfender device ID", e);
            return null;
        }
    }
}