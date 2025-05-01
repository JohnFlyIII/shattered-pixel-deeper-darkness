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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

import java.util.HashSet;

/**
 * High-resolution implementation of the DungeonWallsTilemap.
 * Handles rendering of walls, doors, and other wall-related features
 * with 64x64 pixel tiles instead of the standard 16x16 pixel tiles.
 */
public class HighResWallsTilemap extends DungeonWallsTilemap {

    // Track cells that should be skipped during rendering
    public static HashSet<Integer> skipCells = new HashSet<>();

    /**
     * Constructor for the high-resolution walls tilemap
     */
    public HighResWallsTilemap() {
        super();
        // Set high-resolution scaling for tiles
        this.tileset = new TextureFilm(texture, HighResDungeonTilemap.SIZE, HighResDungeonTilemap.SIZE);
        scale.set(HighResDungeonTilemap.SCALE_FACTOR);
    }

    // Static method for creating high-res wall tiles for images
    public static Image tile(int pos, int tile) {
        Image img = DungeonWallsTilemap.tile(pos, tile);
        if (img != null) {
            img.scale.set(HighResDungeonTilemap.SCALE_FACTOR);
        }
        return img;
    }
    
    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        // Skip flat tiles (no wall overlays for flat visuals)
        if (flat) return -1;

        // Handle wall stitching visuals
        if (DungeonTileSheet.wallStitcheable(tile)) {
            if (pos + mapWidth < size && !DungeonTileSheet.wallStitcheable(map[pos + mapWidth])) {
                // Handle sideways door visuals when a wall is above a door
                if (map[pos + mapWidth] == Terrain.DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS;
                } else if (map[pos + mapWidth] == Terrain.LOCKED_DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_LOCKED;
                } else if (map[pos + mapWidth] == Terrain.CRYSTAL_DOOR) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_CRYSTAL;
                } else if (map[pos + mapWidth] == Terrain.OPEN_DOOR) {
                    return DungeonTileSheet.NULL_TILE;
                }
            } else {
                // Handle internal wall stitching
                return DungeonTileSheet.stitchInternalWallTile(
                        tile,
                        (pos+1) % mapWidth != 0 ?                           map[pos + 1] : -1,
                        (pos+1) % mapWidth != 0 && pos + mapWidth < size ?  map[pos + 1 + mapWidth] : -1,
                        pos + mapWidth < size ?                             map[pos + mapWidth] : -1,
                        pos % mapWidth != 0 && pos + mapWidth < size ?      map[pos - 1 + mapWidth] : -1,
                        pos % mapWidth != 0 ?                               map[pos - 1] : -1
                );
            }
        }

        // Skip cells that are manually marked for skipping
        if (skipCells.contains(pos)) {
            return -1;
        }

        // Handle exit and entry visuals
        if (map[pos] == Terrain.LOCKED_EXIT || map[pos] == Terrain.UNLOCKED_EXIT) {
            return DungeonTileSheet.EXIT_UNDERHANG;
        } else if (pos + mapWidth < size && DungeonTileSheet.wallStitcheable(map[pos+mapWidth])) {
            // Wall overhang stitching
            return DungeonTileSheet.stitchWallOverhangTile(
                    tile,
                    (pos+1) % mapWidth != 0 ?   map[pos + 1 + mapWidth] : -1,
                                                map[pos + mapWidth],
                    pos % mapWidth != 0 ?       map[pos - 1 + mapWidth] : -1
            );
        } else if (Dungeon.level.insideMap(pos) && (map[pos+mapWidth] == Terrain.DOOR || map[pos+mapWidth] == Terrain.LOCKED_DOOR)) {
            // Door overhang visuals
            return DungeonTileSheet.DOOR_OVERHANG;
        } else if (Dungeon.level.insideMap(pos) && map[pos+mapWidth] == Terrain.OPEN_DOOR) {
            // Open door overhang visuals
            return DungeonTileSheet.DOOR_OVERHANG_OPEN;
        } else if (Dungeon.level.insideMap(pos) && map[pos+mapWidth] == Terrain.CRYSTAL_DOOR) {
            // Crystal door overhang visuals
            return DungeonTileSheet.DOOR_OVERHANG_CRYSTAL;
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.STATUE) {
            // Statue overhang visuals
            return DungeonTileSheet.STATUE_OVERHANG;
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.STATUE_SP) {
            // Special statue overhang visuals
            return DungeonTileSheet.STATUE_SP_OVERHANG;
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.MINE_CRYSTAL) {
            // Mine crystal overhang visuals
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.MINE_CRYSTAL_OVERHANG, pos + mapWidth);
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.MINE_BOULDER) {
            // Mine boulder overhang visuals
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.MINE_BOULDER_OVERHANG, pos + mapWidth);
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.ALCHEMY) {
            // Alchemy pot overhang visuals
            return DungeonTileSheet.ALCHEMY_POT_OVERHANG;
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.BARRICADE) {
            // Barricade overhang visuals
            return DungeonTileSheet.BARRICADE_OVERHANG;
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.HIGH_GRASS) {
            // High grass overhang visuals
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.HIGH_GRASS_OVERHANG, pos + mapWidth);
        } else if (pos + mapWidth < size && map[pos+mapWidth] == Terrain.FURROWED_GRASS) {
            // Furrowed grass overhang visuals
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.FURROWED_OVERHANG, pos + mapWidth);
        }

        // No specific wall visual for this cell
        return -1;
    }

    /**
     * Ensure that all pointer events are captured by this tilemap,
     * even when they don't visually hit a wall tile.
     */
    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    /**
     * Ensure that all screen points are captured by this tilemap,
     * even when they don't visually hit a wall tile.
     */
    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }
}