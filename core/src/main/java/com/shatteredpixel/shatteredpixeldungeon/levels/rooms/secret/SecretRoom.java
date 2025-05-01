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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class SecretRoom extends SpecialRoom {
	
	
	/**
	 * List of all possible secret room types.
	 * These rooms are hidden behind walls and typically contain valuable rewards.
	 * 
	 * Room types:
	 * - SecretGardenRoom: A hidden garden with plants and dew
	 * - SecretLaboratoryRoom: A laboratory with potions and experimental equipment
	 * - SecretLibraryRoom: A hidden library with scrolls and books
	 * - SecretLarderRoom: A food storage area with rations and other edibles
	 * - SecretWellRoom: A room containing a special well
	 * - SecretRunestoneRoom: A chamber containing magical runestones
	 * - SecretArtilleryRoom: A weaponry cache with ranged weapons
	 * - SecretChestChasmRoom: A room with a chest near a chasm
	 * - SecretHoneypotRoom: A hidden apiary with honeypots
	 * - SecretHoardRoom: A treasure room with gold and valuables
	 * - SecretMazeRoom: A small, maze-like chamber
	 * - SecretSummoningRoom: A ritual room for summoning
	 */
	private static final ArrayList<Class<? extends SecretRoom>> ALL_SECRETS = new ArrayList<>( Arrays.asList(
			SecretGardenRoom.class, SecretLaboratoryRoom.class, SecretLibraryRoom.class,
			SecretLarderRoom.class, SecretWellRoom.class, SecretRunestoneRoom.class,
			SecretArtilleryRoom.class, SecretChestChasmRoom.class, SecretHoneypotRoom.class,
			SecretHoardRoom.class, SecretMazeRoom.class, SecretSummoningRoom.class));
	
	/**
	 * Track which secret rooms are available for the current game run.
	 * This list is shuffled at the start of each run and rooms are drawn from it in a queue-like manner.
	 */
	public static ArrayList<Class<? extends SecretRoom>> runSecrets = new ArrayList<>();

	/**
	 * Base number of secret rooms per region.
	 * - The whole number part is the guaranteed number of secret rooms
	 * - The fractional part is the chance for an extra secret room
	 * 
	 * Values:
	 * - Index 0 (Sewers): 2.00 → 2 rooms guaranteed
	 * - Index 1 (Prison): 2.25 → 2 rooms guaranteed, 25% chance for a 3rd
	 * - Index 2 (Caves): 2.50 → 2 rooms guaranteed, 50% chance for a 3rd
	 * - Index 3 (City): 2.75 → 2 rooms guaranteed, 75% chance for a 3rd
	 * - Index 4 (Halls): 3.00 → 3 rooms guaranteed
	 */
	private static float[] baseRegionSecrets = new float[]{2f, 2.25f, 2.5f, 2.75f, 3.0f};
	
	/**
	 * Tracks how many secret rooms are left to be placed in each region for the current run.
	 * Initialized at the start of each run based on baseRegionSecrets.
	 */
	private static int[] regionSecretsThisRun = new int[5];
	
	/**
	 * Initialize secret room distribution for a new game run.
	 * This determines how many secret rooms will appear in each region
	 * and shuffles the available room types.
	 */
	public static void initForRun(){
		// Create a copy of the base values to avoid modifying them
		float[] regionChances = baseRegionSecrets.clone();
		
		// Determine number of secret rooms for each region
		for (int i = 0; i < regionSecretsThisRun.length; i++){
			// Always include the whole number part
			regionSecretsThisRun[i] = (int)regionChances[i];
			// Check for fractional part (chance for an extra room)
			if (Random.Float() < regionChances[i] % 1f){
				regionSecretsThisRun[i]++;
			}
		}
		
		// Initialize the room pool and shuffle it for randomization
		runSecrets = new ArrayList<>(ALL_SECRETS);
		Random.shuffle(runSecrets);
	}
	
	/**
 * Calculate how many secret rooms should appear on a specific floor.
 * This distributes the region's secret rooms across its floors.
 * 
 * @param depth The current dungeon depth
 * @return The number of secret rooms to generate on this floor
 */
public static int secretsForFloor(int depth){
    int secrets;
    int region;
    int floor;
    int floorsLeft;
    
    if(depth < 30) {
        // Special case: no secret rooms on floor 1
        if (depth == 1) return 0;
        
        // Calculate which region this depth belongs to (5 floors per region)
        region = depth/5;
        floor = depth%5;
        
        // How many floors are left in this region
        floorsLeft = 5 - floor;
    } else {
        // For depth >= 30, use 10 floors per region
        // Region 6 starts at depth 30
        region = 5 + (depth - 30)/10;
        floor = (depth - 30)%10;
        
        // How many floors are left in this region
        floorsLeft = 10 - floor;

		// Ensure the region is within bounds of our array
        // If we're accessing a region beyond our current array size,
        // use the values from the last defined region (index 4)
        if (region >= regionSecretsThisRun.length) {
            region = 4; // Use values from the Halls region
        }
    }
    
    // Calculate secrets for the floor
    if (floorsLeft == 0) {
        // Last floor in region gets all remaining secrets
        secrets = regionSecretsThisRun[region];
    } else {
        // Distribute remaining secrets across remaining floors
        float secretsFloat = regionSecretsThisRun[region] / (float)floorsLeft;
        // Randomly determine if we round up or down based on the fractional part
        if (Random.Float() < secretsFloat % 1f){
            secrets = (int)Math.ceil(secretsFloat);
        } else {
            secrets = (int)Math.floor(secretsFloat);
        }
    }
    
    // Reduce the region's remaining secret count
    regionSecretsThisRun[region] -= secrets;
    return secrets;
}
	
	/**
	 * Creates a new secret room from the available room types.
	 * Uses a weighted selection system that prioritizes rooms near the front of the queue.
	 * 
	 * @return A newly created secret room instance
	 */
	public static SecretRoom createRoom(){
		// Weighted chance selection:
		// - 60% chance to choose the first room in the queue
		// - 30% chance to choose the second room
		// - 10% chance to choose the third room
		int index = Random.chances(new float[]{6, 3, 1});
		while (index >= runSecrets.size()) index--;

		// Create an instance of the selected room type
		SecretRoom r = Reflection.newInstance(runSecrets.get(index));
		
		// Move the selected room to the back of the queue
		runSecrets.add(runSecrets.remove(index));
		
		return r;
	}
	
	private static final String ROOMS	= "secret_rooms";
	private static final String REGIONS	= "region_secrets";
	
	public static void restoreRoomsFromBundle( Bundle bundle ) {
		runSecrets.clear();
		if (bundle.contains( ROOMS )) {
			for (Class<? extends SecretRoom> type : bundle.getClassArray(ROOMS)) {
				if (type != null) runSecrets.add(type);
			}
			regionSecretsThisRun = bundle.getIntArray(REGIONS);
		} else {
			initForRun();
			ShatteredPixelDungeon.reportException(new Exception("secrets array didn't exist!"));
		}
	}
	
	public static void storeRoomsInBundle( Bundle bundle ) {
		bundle.put( ROOMS, runSecrets.toArray(new Class[0]) );
		bundle.put( REGIONS, regionSecretsThisRun );
	}

}
