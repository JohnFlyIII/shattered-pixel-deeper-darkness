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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class StandardRoom extends Room {
	
	public enum SizeCategory {
		
		NORMAL(4, 10, 1),
		LARGE(10, 14, 2),
		GIANT(14, 18, 3);
		
		public final int minDim, maxDim;
		public final int roomValue;
		
		SizeCategory(int min, int max, int val){
			minDim = min;
			maxDim = max;
			roomValue = val;
		}
		
	}
	
	public SizeCategory sizeCat;
	{ setSizeCat(); }
	
	//Note that if a room wishes to allow itself to be forced to a certain size category,
	//but would (effectively) never roll that size category, consider using Float.MIN_VALUE
	public float[] sizeCatProbs(){
		//always normal by default
		return new float[]{1, 0, 0};
	}
	
	public boolean setSizeCat(){
		return setSizeCat(0, SizeCategory.values().length-1);
	}
	
	//assumes room value is always ordinal+1
	public boolean setSizeCat( int maxRoomValue ){
		return setSizeCat(0, maxRoomValue-1);
	}
	
	//returns false if size cannot be set
	public boolean setSizeCat( int minOrdinal, int maxOrdinal ) {
		float[] probs = sizeCatProbs();
		SizeCategory[] categories = SizeCategory.values();
		
		if (probs.length != categories.length) return false;
		
		for (int i = 0; i < minOrdinal; i++)                    probs[i] = 0;
		for (int i = maxOrdinal+1; i < categories.length; i++)  probs[i] = 0;
		
		int ordinal = Random.chances(probs);
		
		if (ordinal != -1){
			sizeCat = categories[ordinal];
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int minWidth() { return sizeCat.minDim; }
	public int maxWidth() { return sizeCat.maxDim; }
	
	@Override
	public int minHeight() { return sizeCat.minDim; }
	public int maxHeight() { return sizeCat.maxDim; }

	//larger standard rooms generally count as multiple rooms for various counting/weighting purposes
	//but there can be exceptions
	public int sizeFactor(){
		return sizeCat.roomValue;
	}

	public int mobSpawnWeight(){
		return sizeFactor();
	}

	public int connectionWeight(){
		return sizeFactor() * sizeFactor();
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		int cell = l.pointToCell(pointInside(p, 1));
		return (Terrain.flags[l.map[cell]] & Terrain.SOLID) == 0;
	}

	/**
	 * List of all possible standard room types.
	 * This array defines the order for the probability arrays below.
	 * 
	 * Index mapping:
	 * 0 = EmptyRoom (basic empty room)
	 * 
	 * Sewers themed rooms (1-4):
	 * 1 = SewerPipeRoom (room with sewer pipes)
	 * 2 = RingRoom (circular room with a ring-like structure)
	 * 3 = WaterBridgeRoom (room with water and bridge)
	 * 4 = CircleBasinRoom (circular room with water basin)
	 * 
	 * Prison themed rooms (5-8):
	 * 5 = SegmentedRoom (divided room with segments)
	 * 6 = PillarsRoom (room with pillars)
	 * 7 = ChasmBridgeRoom (room with chasm and bridge)
	 * 8 = CellBlockRoom (prison cell block styled room)
	 * 
	 * Caves themed rooms (9-12):
	 * 9 = CaveRoom (natural cave formation)
	 * 10 = CavesFissureRoom (cave with fissures)
	 * 11 = CirclePitRoom (circular room with pit)
	 * 12 = CircleWallRoom (circular room with inner wall)
	 * 
	 * City themed rooms (13-16):
	 * 13 = HallwayRoom (city hallway styled room)
	 * 14 = StatuesRoom (room with statues)
	 * 15 = LibraryRingRoom (library with circular layout)
	 * 16 = SegmentedLibraryRoom (library with segments)
	 * 
	 * Halls themed rooms (17-20):
	 * 17 = RuinsRoom (room with ruins)
	 * 18 = ChasmRoom (room with chasms)
	 * 19 = SkullsRoom (room with skulls)
	 * 20 = RitualRoom (ritual chamber)
	 * 
	 * Special rooms (21-30):
	 * 21 = PlantsRoom (room with plants)
	 * 22 = AquariumRoom (room filled with water)
	 * 23 = PlatformRoom (room with raised platforms)
	 * 24 = BurnedRoom (burned/charred room)
	 * 25 = FissureRoom (room with fissures)
	 * 26 = GrassyGraveRoom (grass with graves)
	 * 27 = StripedRoom (room with striped pattern)
	 * 28 = StudyRoom (study/library room)
	 * 29 = SuspiciousChestRoom (room with suspicious chest)
	 * 30 = MinefieldRoom (room with traps like a minefield)
	 */
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		// Index 0: Basic empty room
		rooms.add(EmptyRoom.class);

		// Indexes 1-4: Sewers themed rooms
		rooms.add(SewerPipeRoom.class);
		rooms.add(RingRoom.class);
		rooms.add(WaterBridgeRoom.class);
		rooms.add(CircleBasinRoom.class);

		// Indexes 5-8: Prison themed rooms
		rooms.add(SegmentedRoom.class);
		rooms.add(PillarsRoom.class);
		rooms.add(ChasmBridgeRoom.class);
		rooms.add(CellBlockRoom.class);

		// Indexes 9-12: Caves themed rooms
		rooms.add(CaveRoom.class);
		rooms.add(CavesFissureRoom.class);
		rooms.add(CirclePitRoom.class);
		rooms.add(CircleWallRoom.class);

		// Indexes 13-16: City themed rooms
		rooms.add(HallwayRoom.class);
		rooms.add(StatuesRoom.class);
		rooms.add(LibraryRingRoom.class);
		rooms.add(SegmentedLibraryRoom.class);

		// Indexes 17-20: Halls themed rooms
		rooms.add(RuinsRoom.class);
		rooms.add(ChasmRoom.class);
		rooms.add(SkullsRoom.class);
		rooms.add(RitualRoom.class);

		// Indexes 21-30: Special rooms (can appear in various regions)
		rooms.add(PlantsRoom.class);
		rooms.add(AquariumRoom.class);
		rooms.add(PlatformRoom.class);
		rooms.add(BurnedRoom.class);
		rooms.add(FissureRoom.class);
		rooms.add(GrassyGraveRoom.class);
		rooms.add(StripedRoom.class);
		rooms.add(StudyRoom.class);
		rooms.add(SuspiciousChestRoom.class);
		rooms.add(MinefieldRoom.class);
	}
	
	/**
	 * Room probability distributions for each dungeon depth.
	 * Each array defines the relative chance of selecting each standard room type.
	 * The position in the array corresponds to the room index defined above.
	 * Higher values mean higher probability of selection.
	 * 
	 * The array has space for depths 1-99, but currently only depths 1-26 are defined.
	 * Deeper levels will need to have their probabilities defined to support purgatory levels.
	 */
	private static float[][] chances = new float[100][];
	static {
		// Level 1: Sewers - Limited special rooms
		chances[1] =  new float[]{5,  10,10,10,5, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  1,0,1,0,1,0,1,1,0,0};
		
		// Levels 2-4: Sewers - All special rooms
		chances[2] =  new float[]{5,  10,10,10,5, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[4] =  chances[3] = chances[2];
		
		// Level 5: Sewers (Boss) - No special rooms
		chances[5] =  new float[]{5,  10,10,10,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  0,0,0,0,0,0,0,0,0,0};

		// Levels 6-10: Prison
		chances[6] =  new float[]{5,  0,0,0,0, 10,10,10,5, 0,0,0,0, 0,0,0,0, 0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];

		// Levels 11-15: Caves
		chances[11] = new float[]{5,  0,0,0,0, 0,0,0,0, 15,10,5,5,  0,0,0,0, 0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];

		// Levels 16-20: City
		chances[16] = new float[]{5,  0,0,0,0, 0,0,0,0, 0,0,0,0, 10,10,10,5, 0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];

		// Levels 21-26: Halls
		chances[21] = new float[]{5,  0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 15,10,5,5,   1,1,1,1,1,1,1,1,1,1};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22] = chances[21];
		
		// Level 30: Purgatory - Limited special rooms
		chances[30] =  new float[]{5,  10,10,10,5, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  1,0,1,0,1,0,1,1,0,0};
		
		// Levels 31-39: Sewers - All special rooms
		chances[31] =  new float[]{5,  10,10,10,5, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  1,1,1,1,1,1,1,1,1,1};
		chances[39] = chances[38] =chances[37] =chances[36] = chances[35]= chances[34]= chances[33]= chances[32]= chances[31];
	
		// Level 40 : Merchant Room
		chances[40] =  new float[]{5,  10,10,10,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0,  0,0,0,0,0,0,0,0,0,0};
	}

	
	/**
	 * Creates an appropriate standard room for the current dungeon depth.
	 * Uses weighted random selection based on the probability distributions defined above.
	 * 
	 * @return A new instance of the selected standard room type
	 */
	public static StandardRoom createRoom(){
		// If current depth isn't defined in the chances array, default to halls (level 21)
		int depth = Dungeon.depth;
		if (depth >= chances.length || chances[depth] == null) {
			depth = 21; // Default to halls distribution
		}
		return Reflection.newInstance(rooms.get(Random.chances(chances[depth])));
	}
	
}
