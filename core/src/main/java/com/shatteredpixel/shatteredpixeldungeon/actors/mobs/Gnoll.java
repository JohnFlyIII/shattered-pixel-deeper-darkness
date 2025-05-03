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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.watabou.utils.Random;

public class Gnoll extends Mob {
	
	{
		spriteClass = GnollSprite.class;
		
		// Set base stats for scaling
		baseHT = 12;
		HP = HT = baseHT;
		baseDefenseSkill = 4;
		defenseSkill = baseDefenseSkill;
		baseAttackSkill = 10;
		baseDamageMin = 1;
		baseDamageMax = 6;
		baseMaxDR = 2; // 0-2 damage reduction
		baseEXP = 2;
		EXP = baseEXP;
		
		maxLvl = 8;
		
		loot = Gold.class;
		lootChance = 0.5f;
	}
	
	@Override
	public int damageRoll() {
		return super.damageRoll();
	}
	
	@Override
	public int attackSkill( Char target ) {
		if (target == null) return baseAttackSkill;
		return super.attackSkill(target);
	}
	
	@Override
	public int drRoll() {
		return super.drRoll();
	}
}
