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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MerchantLevel extends Level {
    
    {
        color1 = 0x48763c;
        color2 = 0x59994a;
    }
    
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;
    
    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SEWERS;  // Replace with merchant tiles when available
    }
    
    @Override
    public String waterTex() {
        return Assets.Environment.WATER_SEWERS;  // Replace with merchant water when available
    }
    
    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.THEME_1, true);
    }
    
    @Override
    protected boolean build() {
        setSize(WIDTH, HEIGHT);
        
        // Fill with empty tiles
        for (int i = 0; i < width() * height(); i++) {
            map[i] = Terrain.EMPTY;
        }
        
        // Build the outer walls
        for (int i = 0; i < width(); i++) {
            map[i] = Terrain.WALL;
            map[i + (height() - 1) * width()] = Terrain.WALL;
        }
        for (int i = 0; i < height(); i++) {
            map[i * width()] = Terrain.WALL;
            map[(i + 1) * width() - 1] = Terrain.WALL;
        }
        
        // Create a central area
        for (int i = 3; i < height() - 3; i++) {
            for (int j = 3; j < width() - 3; j++) {
                map[i * width() + j] = Terrain.EMPTY_SP;
            }
        }
        
        // Add some decorative elements
        for (int i = 4; i < height() - 4; i++) {
            map[i * width() + 4] = Terrain.EMPTY_DECO;
            map[i * width() + width() - 5] = Terrain.EMPTY_DECO;
        }
        
        for (int i = 4; i < width() - 4; i++) {
            map[4 * width() + i] = Terrain.EMPTY_DECO;
            map[(height() - 5) * width() + i] = Terrain.EMPTY_DECO;
        }
        
        // Entrance and exit
        entrance = 7 * width() + 2;
        map[entrance] = Terrain.ENTRANCE;
        
        exit = 7 * width() + width() - 3;
        map[exit] = Terrain.EXIT;
        
        LevelTransition entranceTransition = new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(entranceTransition);
        
        // The exit will lead to future content (currently acts as end of available levels)
        LevelTransition exitTransition = new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT, 41, 0, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(exitTransition);
        
        // Add some tiles for shopkeeper
        map[7 * width() + 7] = Terrain.PEDESTAL;
        
        return true;
    }
    
    @Override
    protected void createMobs() {
        // Add a shopkeeper in the center
        Shopkeeper shopkeeper = new Shopkeeper();
        shopkeeper.pos = 7 * width() + 8;
        mobs.add(shopkeeper);
    }
    
    @Override
    protected void createItems() {
        // Add a bit of gold as a placeholder
        for (int i = 0; i < 3; i++) {
            int pos;
            do {
                pos = Random.Int(length());
            } while (map[pos] != Terrain.EMPTY_SP || findMob(pos) != null);
            
            drop(new Gold().random(), pos);
        }
    }
    
    @Override
    public int randomRespawnCell(Char ch) {
        int pos;
        do {
            pos = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!passable[pos]);
        return pos;
    }
}