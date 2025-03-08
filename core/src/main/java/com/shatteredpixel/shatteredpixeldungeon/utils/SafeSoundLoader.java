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
import com.watabou.noosa.audio.Sample;

import java.util.HashSet;
import java.util.Set;

/**
 * SafeSoundLoader provides a safer way to load sound effects with default fallbacks.
 * If a sound fails to load, the error is logged but the game continues.
 */
public class SafeSoundLoader {
    
    private static final String DEFAULT_SOUND_PATH = Assets.Defaults.SOUND;
    private static final String TAG = "SafeSoundLoader";
    
    private static Set<String> reportedMissing = new HashSet<>();
    
    /**
     * Loads multiple sound effects safely.
     * Any missing or corrupted sounds will use fallbacks and errors will be logged.
     *
     * @param assets Array of sound file paths to load
     */
    public static void load(String... assets) {
        for (String asset : assets) {
            loadSingle(asset);
        }
    }
    
    /**
     * Loads a single sound effect safely.
     * If the sound is missing or corrupted, a fallback is used and an error is logged.
     *
     * @param path Path to the sound file to load
     */
    public static void loadSingle(String path) {
        long startTime = System.currentTimeMillis();
        
        // Check if the sound file exists
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            // Only report each missing asset once to avoid log spam
            if (!reportedMissing.contains(path)) {
                logMissingAsset("sound", path, startTime);
                reportedMissing.add(path);
            }
            
            // Load default sound instead
            if (!path.equals(DEFAULT_SOUND_PATH)) {
                ensureDefaultSoundLoaded();
            }
            return;
        }
        
        try {
            // Attempt to load the sound normally
            Sample.INSTANCE.load(path);
        } catch (Exception e) {
            logFailedAsset("sound", path, e, startTime);
            
            // Load default sound instead
            if (!path.equals(DEFAULT_SOUND_PATH)) {
                ensureDefaultSoundLoaded();
            }
        }
    }
    
    /**
     * Makes sure the default sound is loaded
     */
    private static void ensureDefaultSoundLoaded() {
        try {
            // Check if default sound exists
            FileHandle file = Gdx.files.internal(DEFAULT_SOUND_PATH);
            if (file.exists()) {
                Sample.INSTANCE.load(DEFAULT_SOUND_PATH);
            }
        } catch (Exception e) {
            // If default sound can't load, just log the error
            GameLogger.e(TAG, "Failed to load default sound: " + e.getMessage());
        }
    }
    
    /**
     * Plays a sound safely, falling back to the default sound if the requested sound is not available
     * 
     * @param soundPath The path to the sound to play
     * @return The sound ID or -1 if the sound couldn't be played
     */
    public static long play(String soundPath) {
        return play(soundPath, 1.0f);
    }
    
    /**
     * Plays a sound safely with a specific volume, falling back to the default sound if needed
     * 
     * @param soundPath The path to the sound to play
     * @param volume The volume to play the sound at (0.0 to 1.0)
     * @return The sound ID or -1 if the sound couldn't be played
     */
    public static long play(String soundPath, float volume) {
        // Try to play the requested sound
        long id = Sample.INSTANCE.play(soundPath, volume);
        
        // If sound couldn't be played, try the default sound
        if (id == -1 && !soundPath.equals(DEFAULT_SOUND_PATH)) {
            // Log the issue
            GameLogger.w(TAG, "SOUND PLAYBACK FAILED: " + soundPath + ", using fallback");
            
            // Load and play the default sound
            ensureDefaultSoundLoaded();
            return Sample.INSTANCE.play(DEFAULT_SOUND_PATH, volume);
        }
        
        return id;
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