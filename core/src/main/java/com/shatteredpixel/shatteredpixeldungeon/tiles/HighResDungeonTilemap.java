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
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

/**
 * High-resolution version of DungeonTilemap that uses 64x64 tiles instead of 16x16,
 * but maintains the same visual size on screen.
 */
public abstract class HighResDungeonTilemap extends DungeonTilemap {

    public static final int SIZE = 64; // 64x64 instead of 16x16
    public static final float SCALE_FACTOR = 1.0f; // Don't apply additional scaling
    
    public HighResDungeonTilemap(String tex) {
        super(tex);
        // Set up texture film with high-res tile size
        this.tileset = new TextureFilm(tex, SIZE, SIZE);
        
        // We don't apply any additional scaling at this level
        // The camera and default zoom handle scaling the visual size
    }
    
    @Override
    public int screenToTile(int x, int y, boolean wallAssist) {
        // Convert from screen to world coordinates
        PointF p = camera().screenToCamera(x, y)
                .offset(this.point().negate())
                .invScale(SIZE); // Simply divide by tile size
        
        // Snap to the edges of the tilemap
        p.x = GameMath.gate(0, p.x, Dungeon.level.width()-0.001f);
        p.y = GameMath.gate(0, p.y, Dungeon.level.height()-0.001f);

        int cell = (int)p.x + (int)p.y * Dungeon.level.width();

        if (wallAssist
                && map != null
                && DungeonTileSheet.wallStitcheable(map[cell])){

            if (cell + mapWidth < size
                    && p.y % 1 >= 0.75f
                    && !DungeonTileSheet.wallStitcheable(map[cell + mapWidth])){
                cell += mapWidth;
            }
        }
        
        return cell;
    }
    
    @Override
    public void discover(int pos, int oldValue) {
        final Image tile = new Image(texture);
        tile.frame(tileset.get(getTileVisual(pos, oldValue, false)));
        tile.point(tileToWorld(pos));
        tile.scale.set(SCALE_FACTOR); // Scale to match our overall scale
        
        parent.add(tile);
        
        parent.add(new AlphaTweener(tile, 0, 0.6f) {
            protected void onComplete() {
                tile.killAndErase();
                killAndErase();
            }
        });
    }
    
    // Convert tile coordinates to world coordinates
    public static PointF tileToWorld(int pos) {
        return new PointF(
                (pos % Dungeon.level.width()) * SIZE,
                (pos / Dungeon.level.width()) * SIZE
        );
    }
    
    // Convert tile center to world coordinates
    public static PointF tileCenterToWorld(int pos) {
        return new PointF(
                ((pos % Dungeon.level.width()) + 0.5f) * SIZE,
                ((pos / Dungeon.level.width()) + 0.5f) * SIZE
        );
    }
    
    // Convert raised tile center to world coordinates
    public static PointF raisedTileCenterToWorld(int pos) {
        return new PointF(
                ((pos % Dungeon.level.width()) + 0.5f) * SIZE,
                ((pos / Dungeon.level.width()) + 0.1f) * SIZE
        );
    }
}