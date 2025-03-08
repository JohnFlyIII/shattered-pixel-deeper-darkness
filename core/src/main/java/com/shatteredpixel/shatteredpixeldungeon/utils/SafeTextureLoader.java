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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;

/**
 * SafeTextureLoader provides a safer way to load textures with default fallbacks.
 * If a texture fails to load, a default placeholder is used and an error is logged.
 */
public class SafeTextureLoader {
    
    private static final String DEFAULT_TEXTURE_PATH = Assets.Defaults.TEXTURE;
    private static SmartTexture defaultTexture;
    
    private static final String TAG = "SafeTextureLoader";
    
    /**
     * Loads a texture with fallback to a default texture if the requested texture is missing
     * @param path Path to the texture to load
     * @return The requested texture or a default texture if loading failed
     */
    public static SmartTexture get(String path) {
        long startTime = System.currentTimeMillis();
        
        if (defaultTexture == null) {
            initDefaultTexture();
        }
        
        // Check if the texture exists
        FileHandle file = Gdx.files.internal(path);
        if (!file.exists()) {
            logMissingAsset("texture", path, startTime);
            return defaultTexture;
        }
        
        try {
            // Try to load the texture
            SmartTexture texture = TextureCache.get(path);
            if (texture == null) {
                logFailedAsset("texture", path, null, startTime);
                return defaultTexture;
            }
            return texture;
        } catch (Exception e) {
            logFailedAsset("texture", path, e, startTime);
            return defaultTexture;
        }
    }
    
    /**
     * Creates a default checkerboard texture if the regular default texture fails to load
     */
    private static SmartTexture createDefaultTexture() {
        try {
            // Create a simple pink/black checkerboard texture (8x8 pixels)
            Pixmap pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
            
            // Bright pink and black checkerboard
            Color pink = new Color(1f, 0f, 1f, 1f);
            Color black = new Color(0f, 0f, 0f, 1f);
            
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if ((x + y) % 2 == 0) {
                        pixmap.setColor(pink);
                    } else {
                        pixmap.setColor(black);
                    }
                    pixmap.drawPixel(x, y);
                }
            }
            
            // Draw "MISSING" text (simplified, just a visual indicator)
            pixmap.setColor(Color.WHITE);
            
            // Create and return texture
            SmartTexture texture = new SmartTexture(pixmap);
            pixmap.dispose();
            
            return texture;
        } catch (Exception e) {
            // Last resort - create a single-pixel pink texture
            GameLogger.e(TAG, "Failed to create checkerboard texture: " + e.getMessage());
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.PINK);
            pixmap.fill();
            SmartTexture texture = new SmartTexture(pixmap);
            pixmap.dispose();
            return texture;
        }
    }
    
    /**
     * Initializes the default texture
     */
    private static void initDefaultTexture() {
        try {
            // Try to load the default texture
            FileHandle file = Gdx.files.internal(DEFAULT_TEXTURE_PATH);
            if (file.exists()) {
                defaultTexture = TextureCache.get(DEFAULT_TEXTURE_PATH);
            }
            
            // If loading fails, create a programmatic default
            if (defaultTexture == null) {
                defaultTexture = createDefaultTexture();
            }
        } catch (Exception e) {
            // If all else fails, create a programmatic default
            GameLogger.e(TAG, "Failed to load default texture: " + e.getMessage());
            defaultTexture = createDefaultTexture();
        }
    }
    
    /**
     * Checks if a texture file exists
     */
    public static boolean exists(String path) {
        return Gdx.files.internal(path).exists();
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