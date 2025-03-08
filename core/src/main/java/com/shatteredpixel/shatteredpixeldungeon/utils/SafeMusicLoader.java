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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.audio.Music;

import java.util.HashSet;
import java.util.Set;

/**
 * SafeMusicLoader provides a safer way to play music tracks with default fallbacks.
 * If a music track fails to load, a fallback is used and an error is logged.
 */
public class SafeMusicLoader {
    
    private static final String DEFAULT_MUSIC_PATH = Assets.Defaults.MUSIC;
    private static final String TAG = "SafeMusicLoader";
    
    private static Set<String> reportedMissing = new HashSet<>();
    
    /**
     * Plays a music track safely with fallback to a default track if the requested track is missing
     * 
     * @param trackPath Path to the music track to play
     * @param looping Whether the track should loop
     * @return true if the track was successfully played, false otherwise
     */
    public static boolean play(String trackPath, boolean looping) {
        long startTime = System.currentTimeMillis();
        
        // Check if the music file exists
        FileHandle file = Gdx.files.internal(trackPath);
        if (!file.exists()) {
            // Only report each missing asset once to avoid log spam
            if (!reportedMissing.contains(trackPath)) {
                logMissingAsset("music", trackPath, startTime);
                reportedMissing.add(trackPath);
            }
            
            // Play default music track instead
            if (!trackPath.equals(DEFAULT_MUSIC_PATH)) {
                return playDefaultMusic(looping);
            }
            return false;
        }
        
        try {
            // Attempt to play the music normally
            Music.INSTANCE.play(trackPath, looping);
            return true;
        } catch (Exception e) {
            logFailedAsset("music", trackPath, e, startTime);
            
            // Play default music track instead
            if (!trackPath.equals(DEFAULT_MUSIC_PATH)) {
                return playDefaultMusic(looping);
            }
            return false;
        }
    }
    
    /**
     * Plays the default music track
     * 
     * @param looping Whether the track should loop
     * @return true if the default track was successfully played, false otherwise
     */
    private static boolean playDefaultMusic(boolean looping) {
        try {
            // Check if default music exists
            FileHandle file = Gdx.files.internal(DEFAULT_MUSIC_PATH);
            if (file.exists()) {
                Music.INSTANCE.play(DEFAULT_MUSIC_PATH, looping);
                return true;
            }
            return false;
        } catch (Exception e) {
            // If default music can't play, just log the error
            GameLogger.e(TAG, "Failed to play default music: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logs a missing asset error
     */
    private static void logMissingAsset(String type, String path, long startTime) {
        long loadTime = System.currentTimeMillis() - startTime;
        String message = String.format("MISSING ASSET: TYPE:%s PATH:%s FALLBACK:used TIME:%dms", 
                type, path, loadTime);
        GameLogger.w(TAG, message);
    }
    
    /**
     * Logs a failed asset loading error
     */
    private static void logFailedAsset(String type, String path, Exception e, long startTime) {
        long loadTime = System.currentTimeMillis() - startTime;
        String message = String.format("FAILED TO LOAD ASSET: TYPE:%s PATH:%s FALLBACK:used TIME:%dms", 
                type, path, loadTime);
        if (e != null) {
            GameLogger.e(TAG + ": " + message);
            GameLogger.e(e);
        } else {
            GameLogger.e(TAG, message);
        }
    }
}