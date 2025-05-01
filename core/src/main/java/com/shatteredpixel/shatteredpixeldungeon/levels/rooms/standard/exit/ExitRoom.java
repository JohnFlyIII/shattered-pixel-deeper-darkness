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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class ExitRoom extends StandardRoom {
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 5);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 5);
	}

	@Override
	public boolean isExit() {
		return true;
	}

	public void paint(Level level) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		
		for (Room.Door door : connected.values()) {
			door.set( Room.Door.Type.REGULAR );
		}
		
		int exit = level.pointToCell(random( 2 ));
		Painter.set( level, exit, Terrain.EXIT );
		level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
	}
	
	@Override
	public boolean canPlaceCharacter(Point p, Level l) {
		return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
	}

	/**
	 * Room indexes for probability distributions:
	 * 0 = ExitRoom (basic exit)
	 * 1 = WaterBridgeExitRoom (water theme)
	 * 2 = CircleBasinExitRoom (water circle)
	 * 3 = ChasmBridgeExitRoom (chasm with bridge)
	 * 4 = PillarsExitRoom (pillars)
	 * 5 = CaveExitRoom (cave theme)
	 * 6 = CavesFissureExitRoom (cave fissure)
	 * 7 = HallwayExitRoom (hallway)
	 * 8 = StatuesExitRoom (statues)
	 * 9 = ChasmExitRoom (chasm theme)
	 * 10 = RitualExitRoom (ritual)
	 */
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		// Index 0: Basic exit
		rooms.add(ExitRoom.class);

		// Indexes 1-2: Water themed exits
		rooms.add(WaterBridgeExitRoom.class);
		rooms.add(CircleBasinExitRoom.class);

		// Indexes 3-4: Chasm/Pillar themed exits
		rooms.add(ChasmBridgeExitRoom.class);
		rooms.add(PillarsExitRoom.class);

		// Indexes 5-6: Cave themed exits
		rooms.add(CaveExitRoom.class);
		rooms.add(CavesFissureExitRoom.class);

		// Indexes 7-8: Hallway/Statue themed exits
		rooms.add(HallwayExitRoom.class);
		rooms.add(StatuesExitRoom.class);

		// Indexes 9-10: Deep chasm/Ritual themed exits
		rooms.add(ChasmExitRoom.class);
		rooms.add(RitualExitRoom.class);
	}

	/**
	 * Probability distributions for each dungeon level
	 * Format: [basicExit, waterBridge, waterBasin, chasmBridge, pillars, cave, caveFissure, hallway, statues, chasm, ritual]
	 * Each array represents the relative chance of selecting each room type
	 */
	private static float[][] chances = new float[100][]; // Expanded to handle Purgatory levels (30-40)
	static {
		// Sewers (Levels 1-5): High chance of water-themed exits
		chances[1] =  new float[]{3,  6,1, 0,0, 0,0, 0,0, 0,0};
		chances[5] =  chances[4] = chances[3] = chances[2] = chances[1];

		// Prison (Levels 6-10): High chance of chasm/pillar-themed exits
		chances[6] =  new float[]{2,  0,0, 4,4, 0,0, 0,0, 0,0};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];

		// Caves (Levels 11-15): High chance of cave-themed exits
		chances[11] = new float[]{2,  0,0, 0,0, 4,4, 0,0, 0,0};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];

		// City (Levels 16-20): High chance of hallway/statue-themed exits
		chances[16] = new float[]{2,  0,0, 0,0, 0,0, 4,4, 0,0};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];

		// Halls (Levels 21-26): High chance of deep chasm/ritual-themed exits
		chances[21] = new float[]{3,  0,0, 0,0, 0,0, 0,0, 6,1};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22] = chances[21];
		
		// Purgatory (Levels 30-39): Use deep chasm/ritual themed exits like halls
		chances[30] = new float[]{3,  0,0, 0,0, 0,0, 0,0, 6,1};
		chances[39] = chances[38] = chances[37] = chances[36] = chances[35] = chances[34] = chances[33] = chances[32] = chances[31] = chances[30];
		
		// Merchant level (Level 40): Only basic exits
		chances[40] = new float[]{1,  0,0, 0,0, 0,0, 0,0, 0,0};
	}

	public static StandardRoom createExit(){
		return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
	}
}
