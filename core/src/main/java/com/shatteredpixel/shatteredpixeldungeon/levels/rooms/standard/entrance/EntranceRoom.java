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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
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

public class EntranceRoom extends StandardRoom {
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 5);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 5);
	}

	@Override
	public boolean isEntrance() {
		return true;
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		if (Dungeon.depth <= 2) {
			return false;
		} else {
			return super.canMerge(l, other, p, mergeTerrain);
		}
	}

	@Override
	public boolean canPlaceTrap(Point p) {
		if (Dungeon.depth == 1) {
			return false;
		} else {
			return super.canPlaceTrap(p);
		}
	}

	public void paint(Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		
		for (Room.Door door : connected.values()) {
			door.set( Room.Door.Type.REGULAR );
		}

		int entrance;
		do {
			entrance = level.pointToCell(random(2));
		} while (level.findMob(entrance) != null);
		Painter.set( level, entrance, Terrain.ENTRANCE );

		if (Dungeon.depth == 1){
			level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.SURFACE));
		} else {
			level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
		}

		//use a separate generator here so meta progression doesn't affect levelgen
		Random.pushGenerator();

		//places the first guidebook page on floor 1
		if (Dungeon.depth == 1 &&
				(!Document.ADVENTURERS_GUIDE.isPageRead(Document.GUIDE_INTRO) || SPDSettings.intro() )){
			int pos;
			do {
				//can't be on bottom row of tiles
				pos = level.pointToCell(new Point( Random.IntRange( left + 1, right - 1 ),
						Random.IntRange( top + 1, bottom - 2 )));
			} while (pos == level.entrance() || level.findMob(level.entrance()) != null);
			level.drop( new Guidebook(), pos );
			Document.ADVENTURERS_GUIDE.deletePage(Document.GUIDE_INTRO);
		}

		//places the third guidebook page on floor 2
		if (Dungeon.depth == 2 && !Document.ADVENTURERS_GUIDE.isPageFound(Document.GUIDE_SEARCHING)){
			int pos;
			do {
				//can't be on bottom row of tiles
				pos = level.pointToCell(new Point( Random.IntRange( left + 1, right - 1 ),
						Random.IntRange( top + 1, bottom - 2 )));
			} while (pos == level.entrance() || level.findMob(level.entrance()) != null);
			GuidePage p = new GuidePage();
			p.page(Document.GUIDE_SEARCHING);
			level.drop( p, pos );
		}

		Random.popGenerator();

	}

	/**
	 * List of possible entrance room types.
	 * This array defines the order for the probability arrays below.
	 * 
	 * Index mapping:
	 * 0 = EntranceRoom (basic entrance)
	 * 1 = WaterBridgeEntranceRoom (water-themed entrance with bridge)
	 * 2 = CircleBasinEntranceRoom (circular water basin entrance)
	 * 3 = ChasmBridgeEntranceRoom (chasm with bridge entrance)
	 * 4 = PillarsEntranceRoom (entrance room with pillars)
	 * 5 = CaveEntranceRoom (cave-themed entrance)
	 * 6 = CavesFissureEntranceRoom (entrance with cave fissures)
	 * 7 = HallwayEntranceRoom (hallway-style entrance)
	 * 8 = StatuesEntranceRoom (entrance with statues)
	 * 9 = ChasmEntranceRoom (chasm entrance without bridge)
	 * 10 = RitualEntranceRoom (ritual-themed entrance)
	 */
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		// Index 0: Basic entrance room
		rooms.add(EntranceRoom.class);

		// Indexes 1-2: Water-themed entrances
		rooms.add(WaterBridgeEntranceRoom.class);
		rooms.add(CircleBasinEntranceRoom.class);

		// Indexes 3-4: Chasm and pillar entrances
		rooms.add(ChasmBridgeEntranceRoom.class);
		rooms.add(PillarsEntranceRoom.class);

		// Indexes 5-6: Cave-themed entrances
		rooms.add(CaveEntranceRoom.class);
		rooms.add(CavesFissureEntranceRoom.class);

		// Indexes 7-8: Hallway and statue entrances
		rooms.add(HallwayEntranceRoom.class);
		rooms.add(StatuesEntranceRoom.class);

		// Indexes 9-10: Chasm and ritual entrances
		rooms.add(ChasmEntranceRoom.class);
		rooms.add(RitualEntranceRoom.class);
	}

	/**
	 * Room probability distributions for each dungeon depth.
	 * Each array defines the relative chance of selecting each entrance room type.
	 * The position in the array corresponds to the room index defined above.
	 * Higher values mean higher probability of selection.
	 */
	private static float[][] chances = new float[41][];
	static {
		// Sewers (Levels 1-2): Only basic entrance
		chances[1] =  new float[]{1,  0,0, 0,0, 0,0, 0,0, 0,0};
		chances[2] =  chances[1];
		
		// Sewers (Levels 3-5): Basic entrance (3/10), water entrances (7/10)
		chances[3] =  new float[]{3,  6,1, 0,0, 0,0, 0,0, 0,0};
		chances[5] =  chances[4] = chances[3];

		// Prison (Levels 6-10): Basic entrance (2/10), chasm/pillar entrances (8/10)
		chances[6] =  new float[]{2,  0,0, 4,4, 0,0, 0,0, 0,0};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];

		// Caves (Levels 11-15): Basic entrance (2/10), cave entrances (8/10)
		chances[11] = new float[]{2,  0,0, 0,0, 4,4, 0,0, 0,0};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];

		// City (Levels 16-20): Basic entrance (2/10), hallway/statue entrances (8/10)
		chances[16] = new float[]{2,  0,0, 0,0, 0,0, 4,4, 0,0};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];

		// Halls (Levels 21-26): Basic entrance (3/10), chasm/ritual entrances (7/10)
		chances[21] = new float[]{3,  0,0, 0,0, 0,0, 0,0, 6,1};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22] = chances[21];
		
		// Purgatory levels (30-39): Similar to halls levels
		// Basic entrance (3/10), chasm/ritual entrances (7/10)
		chances[30] = new float[]{3,  0,0, 0,0, 0,0, 0,0, 6,1};
		chances[39] = chances[38] = chances[37] = chances[36] = chances[35] = chances[34] = chances[33] = chances[32] = chances[31] = chances[30];
		
		// Merchant level (Level 40): Only basic entrance
		chances[40] = new float[]{1,  0,0, 0,0, 0,0, 0,0, 0,0};
	}

	/**
	 * Creates an appropriate entrance room for the current dungeon depth.
	 * Uses weighted random selection based on the probability distributions defined above.
	 * 
	 * @return A new instance of the selected entrance room type
	 */
	public static StandardRoom createEntrance(){
		return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
	}

}
