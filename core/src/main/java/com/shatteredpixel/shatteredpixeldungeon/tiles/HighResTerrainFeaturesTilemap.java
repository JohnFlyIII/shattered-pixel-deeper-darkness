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
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;
import com.watabou.utils.SparseArray;

/**
 * High-resolution implementation of the terrain features tilemap.
 * Uses 64x64 tiles but displays them at the same visual size as 16x16 tiles.
 */
public class HighResTerrainFeaturesTilemap extends TerrainFeaturesTilemap {
    
    // Scale factor to reduce from 64x64 to 16x16 visual size
    public static final float SCALE_FACTOR = 0.25f;
    
    // Actual tile size is 64x64 pixels
    public static final int TILE_SIZE = 64;
    
    private static HighResTerrainFeaturesTilemap instance;

    /**
     * Constructor for high-resolution terrain features tilemap
     */
    public HighResTerrainFeaturesTilemap(SparseArray<Plant> plants, SparseArray<Trap> traps) {
        // The parent constructor needs the features asset path
        // For high-res we use the Purgatory features
        super(plants, traps);
        
        // We need to recreate the texture with the high-res version
        // First, get the texture from the TextureCache using the high-res path
        texture = TextureCache.get(Assets.Environment.TILES_PURGATORY_FEATURES);
        
        // Create a TextureFilm that uses 64x64 tiles instead of 16x16
        tileset = new TextureFilm(texture, TILE_SIZE, TILE_SIZE);
        
        // We don't apply additional scaling here
        // The game's camera system handles the visual sizing
        
        if (Dungeon.level != null) {
            map(Dungeon.level.map, Dungeon.level.width());
        }
        
        instance = this;
    }

    @Override
    public void growPlant(final int pos) {
        final Image plant = tile(pos, map[pos]);
        if (plant == null) return;
        
        plant.origin.set(32, 48); // Adjusted for 64x64 tiles (8x4=32, 12x4=48)
        plant.scale.set(0);
        plant.point(DungeonTilemap.tileToWorld(pos));
        
        // Apply smaller scale to match regular terrain features size
        plant.scale.set(0);
        
        parent.add(plant);
        
        parent.add(new ScaleTweener(plant, new PointF(SCALE_FACTOR, SCALE_FACTOR), 0.2f) {
            protected void onComplete() {
                plant.killAndErase();
                killAndErase();
                updateMapCell(pos);
            }
        });
    }

    /**
     * Creates a new trap image with proper scaling
     */
    public static Image getTrapVisualHighRes(Trap trap) {
        if (instance == null) instance = new HighResTerrainFeaturesTilemap(null, null);
        
        RectF uv = instance.tileset.get((trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16));
        if (uv == null) return null;
        
        Image img = new Image(instance.texture);
        img.frame(uv);
        img.scale.set(SCALE_FACTOR, SCALE_FACTOR);
        return img;
    }

    /**
     * Creates a new plant image with proper scaling
     */
    public static Image getPlantVisualHighRes(Plant plant) {
        if (instance == null) instance = new HighResTerrainFeaturesTilemap(null, null);
        
        RectF uv = instance.tileset.get(plant.image + 7*16);
        if (uv == null) return null;
        
        Image img = new Image(instance.texture);
        img.frame(uv);
        img.scale.set(SCALE_FACTOR, SCALE_FACTOR);
        return img;
    }
    
    // Static method reimplements parent method with high-res scaling
    public static Image tile(int pos, int tile) {
        RectF uv = instance.tileset.get(instance.getTileVisual(pos, tile, true));
        if (uv == null) return null;
        
        Image img = new Image(instance.texture);
        img.frame(uv);
        img.scale.set(SCALE_FACTOR, SCALE_FACTOR);
        return img;
    }
}