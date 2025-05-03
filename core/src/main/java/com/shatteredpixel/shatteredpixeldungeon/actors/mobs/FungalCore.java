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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalCoreSprite;

public class FungalCore extends Mob {

	{
		// Set base stats for scaling
		baseHT = 300;
		HP = HT = baseHT;
		baseDefenseSkill = 0;
		baseAttackSkill = 0;
		baseDamageMin = 0;
		baseDamageMax = 0;
		baseMaxDR = 0;
		baseEXP = 20;
		EXP = baseEXP;
		
		spriteClass = FungalCoreSprite.class;

		state = PASSIVE;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.BOSS);
		
		// Scale stats based on dungeon depth
		scaleStatsByDepth();
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Blacksmith.Quest.beatBoss();
	}
}
