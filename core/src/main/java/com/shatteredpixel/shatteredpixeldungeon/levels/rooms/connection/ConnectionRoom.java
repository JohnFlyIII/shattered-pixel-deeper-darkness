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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public abstract class ConnectionRoom extends Room {
	
	@Override
	public int minWidth() { return 3; }
	public int maxWidth() { return 10; }
	
	@Override
	public int minHeight() { return 3; }
	public int maxHeight() { return 10; }
	
	@Override
	public int minConnections(int direction) {
		if (direction == ALL)   return 2;
		else                    return 0;
	}
	
	/**
	 * List of all possible connection room types.
	 * These rooms serve as corridors connecting other room types together.
	 * 
	 * Index mapping:
	 * 0 = TunnelRoom (simple straight corridor)
	 * 1 = BridgeRoom (bridge over water or chasms)
	 * 2 = PerimeterRoom (path along the perimeter of an area)
	 * 3 = WalkwayRoom (open area with a defined walking path)
	 * 4 = RingTunnelRoom (circular tunnel corridor)
	 * 5 = RingBridgeRoom (circular bridge over hazards)
	 */
	private static ArrayList<Class<?extends ConnectionRoom>> rooms = new ArrayList<>();
	static {
		// Index 0-1: Basic connection rooms
		rooms.add(TunnelRoom.class);      // Standard direct tunnel
		rooms.add(BridgeRoom.class);      // Bridge over hazards
		
		// Index 2-3: Perimeter-based connections
		rooms.add(PerimeterRoom.class);   // Path along walls/perimeter
		rooms.add(WalkwayRoom.class);     // Defined path through open area
		
		// Index 4-5: Circular connection rooms
		rooms.add(RingTunnelRoom.class);  // Circular tunnel
		rooms.add(RingBridgeRoom.class);  // Circular bridge
	}
	
	/**
	 * Room probability distributions for each dungeon depth.
	 * Each array defines the relative chance of selecting each connection room type.
	 * The position in the array corresponds to the room index defined above.
	 * Higher values mean higher probability of selection.
	 * 
	 * The array supports depths 1-26, and needs to be extended for Purgatory.
	 */
	private static float[][] chances = new float[41][];
	static {
		// Levels 1-4: Sewers - Mostly tunnels, some walkways and ring rooms
		chances[1] =  new float[]{20, 1,    0, 2,       2, 1};
		chances[4] =  chances[3] = chances[2] = chances[1];
		
		// Level 5: Sewers (Boss) - Only tunnels
		chances[5] =  new float[]{20, 0,    0, 0,       0, 0};
		
		// Levels 6-10: Prison - Mostly perimeter rooms, some walkways
		chances[6] =  new float[]{0, 0,     22, 3,      0, 0};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];
		
		// Levels 11-15: Caves - Mix of tunnels, walkways, and ring rooms
		chances[11] = new float[]{12, 0,    0, 5,       5, 3};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];
		
		// Levels 16-20: City - Mostly perimeter rooms, some walkways and ring rooms
		chances[16] = new float[]{0, 0,     18, 3,      3, 1};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];
		
		// Level 21: Halls (First level) - Only tunnels
		chances[21] = chances[5];
		
		// Levels 22-26: Halls - Mix of all room types with emphasis on tunnels
		chances[22] = new float[]{15, 4,    0, 2,       3, 2};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22];
		
		// Levels 30-39: Purgatory - Similar to Halls
		chances[30] = new float[]{15, 4,    0, 2,       3, 2};
		chances[39] = chances[38] = chances[37] = chances[36] = chances[35] = chances[34] = chances[33] = chances[32] = chances[31] = chances[30];
		
		// Level 40: Merchant level - Only tunnels
		chances[40] = new float[]{20, 0,    0, 0,       0, 0};
	}
	
	/**
	 * Creates an appropriate connection room for the current dungeon depth.
	 * Uses weighted random selection based on the probability distributions defined above.
	 * 
	 * @return A new instance of the selected connection room type
	 */
	public static ConnectionRoom createRoom(){
		// If current depth isn't defined in the chances array, default to halls (level 22)
		int depth = Dungeon.depth;
		if (depth >= chances.length || chances[depth] == null) {
			depth = 22; // Default to halls distribution
		}
		return Reflection.newInstance(rooms.get(Random.chances(chances[depth])));
	}
}
