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
import com.watabou.noosa.TextureFilm;

/**
 * High-resolution implementation of RaisedTerrainTilemap.
 * Handles raised terrain features like statues and high grass.
 */
public class HighResRaisedTerrainTilemap extends RaisedTerrainTilemap {

    public HighResRaisedTerrainTilemap() {
        // Call parent no-arg constructor
        super();
        
        // Set up texture film with high-res tile size
        this.tileset = new TextureFilm(texture, HighResDungeonTilemap.SIZE, HighResDungeonTilemap.SIZE);
        
        // We don't apply additional scaling here
        // The game's camera system handles the visual sizing
    }
    
    @Override
    protected boolean needsRender(int pos) {
        // If the cell should be skipped, don't render
        if (HighResWallsTilemap.skipCells.contains(pos)) {
            return false;
        }
        return super.needsRender(pos);
    }
}