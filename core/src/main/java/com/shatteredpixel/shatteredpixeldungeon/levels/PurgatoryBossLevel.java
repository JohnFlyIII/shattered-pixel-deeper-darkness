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
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PurgatoryBossLevel extends Level {
    
    {
        color1 = 0x48763c;
        color2 = 0x59994a;
    }
    
    private static final int TOP = 2;
    private static final int ARENA_WIDTH = 11;
    private static final int ARENA_HEIGHT = 11;
    private static final int ARENA_TOP = TOP + 1;
    private static final int ARENA_CENTER_X = 16; // width/2 (width is 32)
    private static final int ARENA_CENTER_Y = ARENA_TOP + ARENA_HEIGHT/2;
    
    private int stairs = -1;
    private boolean enteredArena = false;
    private boolean keyDropped = false;
    
    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SEWERS;  // Replace with TILES_PURGATORY when available
    }
    
    @Override
    public String waterTex() {
        return Assets.Environment.WATER_SEWERS;  // Replace with WATER_PURGATORY when available
    }
    
    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play(Assets.Music.SEWERS_BOSS, true); // Replace with PURGATORY_BOSS when available
    }
    
    @Override
    protected boolean build() {
        setSize(width(), height());
        
        // Fill with empty tiles
        for (int i = 0; i < width() * height(); i++) {
            map[i] = Terrain.EMPTY;
        }
        
        // Build the arena walls
        for (int i = 0; i < width(); i++) {
            map[i] = Terrain.WALL;
            map[i + (height() - 1) * width()] = Terrain.WALL;
        }
        for (int i = 0; i < height(); i++) {
            map[i * width()] = Terrain.WALL;
            map[(i + 1) * width() - 1] = Terrain.WALL;
        }
        
        // Build the arena
        for (int i = ARENA_TOP; i < ARENA_TOP + ARENA_HEIGHT; i++) {
            for (int j = (width() - ARENA_WIDTH) / 2; j < (width() + ARENA_WIDTH) / 2; j++) {
                map[i * width() + j] = Terrain.WATER;
            }
        }
        
        // Entrance
        entrance = (ARENA_TOP - 1) * width() + width() / 2;
        map[entrance] = Terrain.ENTRANCE;
        
        // Exit
        exit = (ARENA_TOP + ARENA_HEIGHT) * width() + width() / 2;
        map[exit] = Terrain.LOCKED_EXIT;
        
        LevelTransition entranceTransition = new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(entranceTransition);
        
        LevelTransition exitTransition = new LevelTransition(this, exit, LevelTransition.Type.REGULAR_EXIT, 40, 0, LevelTransition.Type.REGULAR_ENTRANCE);
        transitions.add(exitTransition);
        
        // Center of the arena
        int center = (ARENA_TOP + ARENA_HEIGHT / 2) * width() + width() / 2;
        
        // Create some terrain features
        for (int i = 0; i < 4; i++) {
            int pillarPos = center - 2*width() + 2 + i*2;
            map[pillarPos] = Terrain.WALL;
            map[pillarPos + 4*width()] = Terrain.WALL;
            
            pillarPos = center - 2 + (i*2)*width();
            map[pillarPos] = Terrain.WALL;
            map[pillarPos + 4] = Terrain.WALL;
        }
        
        // Some statue decorations
        for (int i = ARENA_TOP; i < ARENA_TOP + ARENA_HEIGHT; i++) {
            int pos = i * width() + (width() - ARENA_WIDTH) / 2 - 1;
            if (Random.Int(2) == 0 && map[pos] == Terrain.WALL) map[pos] = Terrain.STATUE;
            
            pos = i * width() + (width() + ARENA_WIDTH) / 2;
            if (Random.Int(2) == 0 && map[pos] == Terrain.WALL) map[pos] = Terrain.STATUE;
        }
        
        return true;
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EXIT) {
                stairs = i;
                break;
            }
        }
        enteredArena = bundle.getBoolean("enteredArena");
        keyDropped = bundle.getBoolean("keyDropped");
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("enteredArena", enteredArena);
        bundle.put("keyDropped", keyDropped);
    }
    
    @Override
    protected void createMobs() {
        Mob boss = new Goo(); // Temporary boss - replace with Purgatory boss later
        boss.pos = (ARENA_TOP + ARENA_HEIGHT/2) * width() + width()/2;
        mobs.add(boss);
    }
    
    @Override
    protected void createItems() {

    }
    
    @Override
    public int randomRespawnCell(Char mob) {
        // Relocate respawning mobs to the arena
        int pos;
        do {
            pos = Random.IntRange(ARENA_TOP + 1, ARENA_TOP + ARENA_HEIGHT - 2) * width()
                    + Random.IntRange((width() - ARENA_WIDTH) / 2 + 1, (width() + ARENA_WIDTH) / 2 - 2);
        } while (
                pos == entrance ||
                (Dungeon.level.map[pos] != Terrain.EMPTY && Dungeon.level.map[pos] != Terrain.EMPTY_DECO) ||
                Dungeon.level.findMob(pos) != null);

        return pos;
    }
    
    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(SewerLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }
    
    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(SewerLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SewerLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }
    
    @Override
    public int width() {
        return 32;
    }
    
    @Override
    public int height() {
        return 32;
    }
}