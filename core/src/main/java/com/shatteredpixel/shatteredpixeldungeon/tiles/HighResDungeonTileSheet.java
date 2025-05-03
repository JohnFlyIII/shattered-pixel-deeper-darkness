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

/**
 * High-resolution adapter for the DungeonTileSheet class.
 * This class doesn't contain any new functionality but serves as a helper
 * to create appropriate texture mappings for high-resolution 64x64 tiles.
 *
 * All tile indices and mapping logic remain the same as DungeonTileSheet,
 * but this class accounts for the different texture layout in high-res texture sheets.
 */
public class HighResDungeonTileSheet extends DungeonTileSheet {

    // The tilesheets have the same WIDTH (16 tiles per row)
    // but each tile is 64x64 pixels instead of 16x16
    public static final int TILE_SIZE = 64;

}