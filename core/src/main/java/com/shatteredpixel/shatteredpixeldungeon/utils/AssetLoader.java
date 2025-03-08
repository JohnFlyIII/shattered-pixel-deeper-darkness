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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.gltextures.SmartTexture;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;

/**
 * Main entry point for the safe asset loading system.
 * This class provides a unified interface for loading various types of assets
 * with built-in error handling and fallbacks.
 */
public class AssetLoader {
    
    private static final String TAG = "AssetLoader";
    
    /**
     * Initializes the asset loader system
     * Should be called during application startup
     */
    public static void init() {
        GameLogger.i(TAG, "Initializing safe asset loading system");
        
        // Load default assets
        preloadDefaultAssets();
    }
    
    /**
     * Preloads the default fallback assets
     */
    private static void preloadDefaultAssets() {
        try {
            // Load the default texture
            SafeTextureLoader.get(Assets.Defaults.TEXTURE);
            
            // Load the default sound
            SafeSoundLoader.loadSingle(Assets.Defaults.SOUND);
            
            GameLogger.i(TAG, "Default assets preloaded successfully");
        } catch (Exception e) {
            GameLogger.e(TAG + ": Failed to preload default assets");
            GameLogger.e(e);
        }
    }
    
    /**
     * Loads a texture safely with fallback
     * 
     * @param path Path to the texture to load
     * @return The loaded texture or a default fallback
     */
    public static SmartTexture getTexture(String path) {
        return SafeTextureLoader.get(path);
    }
    
    /**
     * Loads sound effects safely with fallbacks
     * 
     * @param assets Array of sound paths to load
     */
    public static void loadSounds(String... assets) {
        SafeSoundLoader.load(assets);
    }
    
    /**
     * Plays a sound safely with fallback
     * 
     * @param soundPath Path to the sound to play
     * @return Sound ID or -1 if sound couldn't be played
     */
    public static long playSound(String soundPath) {
        return SafeSoundLoader.play(soundPath);
    }
    
    /**
     * Plays a sound safely with fallback at a specific volume
     * 
     * @param soundPath Path to the sound to play
     * @param volume Volume level (0.0 to 1.0)
     * @return Sound ID or -1 if sound couldn't be played
     */
    public static long playSound(String soundPath, float volume) {
        return SafeSoundLoader.play(soundPath, volume);
    }
    
    /**
     * Plays a music track safely with fallback
     * 
     * @param musicPath Path to the music track
     * @param looping Whether the track should loop
     * @return true if successful, false otherwise
     */
    public static boolean playMusic(String musicPath, boolean looping) {
        return SafeMusicLoader.play(musicPath, looping);
    }
    
    /**
     * Checks if an asset exists at the specified path
     * 
     * @param path Path to check
     * @return true if the asset exists, false otherwise
     */
    public static boolean exists(String path) {
        return SafeTextureLoader.exists(path);
    }
}