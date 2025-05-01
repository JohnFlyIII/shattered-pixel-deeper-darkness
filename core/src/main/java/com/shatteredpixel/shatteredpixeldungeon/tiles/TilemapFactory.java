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

package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PurgatoryBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PurgatoryLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.SparseArray;

/**
 * A factory class that creates appropriate tilemaps based on the current dungeon level.
 * This allows switching between standard resolution tilemaps and high-resolution 
 * tilemaps for Purgatory levels (30+).
 */
public class TilemapFactory {

    /**
     * Flag to force high-resolution mode for testing purposes.
     * When set to true, all tilemap creation methods will return high-resolution implementations
     * regardless of the current level.
     */
    private static boolean forceHighRes = false;
    
    /**
     * Creates the appropriate DungeonTerrainTilemap based on the current depth.
     * 
     * @return A DungeonTerrainTilemap implementation appropriate for the current level
     */
    public static DungeonTerrainTilemap createDungeonTilemap() {
        if (isHighResLevel()) {
            // Use high resolution tilemap for Purgatory levels
            return new HighResDungeonTerrainTilemap();
        } else {
            // Standard terrain tilemap for regular levels
            return new DungeonTerrainTilemap();
        }
    }
    
    /**
     * Creates the appropriate TerrainFeaturesTilemap based on the current depth.
     * 
     * @param plants The plants to include in the tilemap
     * @param traps The traps to include in the tilemap
     * @return A TerrainFeaturesTilemap implementation appropriate for the current level
     */
    public static TerrainFeaturesTilemap createTerrainFeaturesTilemap(SparseArray<Plant> plants, SparseArray<Trap> traps) {
        if (isHighResLevel()) {
            // High-resolution implementation for Purgatory levels
            return new HighResTerrainFeaturesTilemap(plants, traps);
        } else {
            // Standard implementation for regular levels
            return new TerrainFeaturesTilemap(plants, traps);
        }
    }

    /**
     * Creates the appropriate DungeonWallsTilemap based on the current depth.
     * 
     * @return A DungeonWallsTilemap implementation appropriate for the current level
     */
    public static DungeonWallsTilemap createWallsTilemap() {
        if (isHighResLevel()) {
            // High-resolution implementation for Purgatory levels
            return new HighResWallsTilemap();
        } else {
            // Standard implementation for regular levels
            return new DungeonWallsTilemap();
        }
    }
    
    /**
     * Determines if the current level should use high-resolution assets.
     * Checks if the level is a Purgatory level (depth 30+) or a specific Purgatory-related level.
     * Also checks if high-resolution mode has been forced for testing.
     * 
     * @return true if the current level should use high-resolution assets, false otherwise
     */
    public static boolean isHighResLevel() {
        // First check if high-res mode is being forced for testing
        if (forceHighRes) {
            return true;
        }
        
        // Check if the current level is a Purgatory level (depth 30+)
        if (Dungeon.depth >= 30) {
            return true;
        }
        
        // Also check for specific Purgatory-related level classes
        Level currentLevel = Dungeon.level;
        return currentLevel instanceof PurgatoryLevel || currentLevel instanceof PurgatoryBossLevel;
    }
    
    /**
     * Forces high-resolution mode for testing purposes.
     * When enabled, all tilemap creation methods will return high-resolution implementations
     * regardless of the current level.
     * 
     * @param forceHighRes true to force high-resolution mode, false to use normal detection
     */
    public static void setForceHighResMode(boolean forceHighRes) {
        TilemapFactory.forceHighRes = forceHighRes;
    }
    
    /**
     * Resets the high-resolution mode setting to its default state (disabled).
     * This returns the tilemap factory to its normal behavior of detecting high-resolution
     * levels based on depth and level type.
     */
    public static void resetHighResMode() {
        forceHighRes = false;
    }
}