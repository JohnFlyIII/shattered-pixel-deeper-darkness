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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BruteSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Brute extends Mob {
	
	{
		spriteClass = BruteSprite.class;
		
		// Base stats for scaling
		baseHT = 40;
		baseDefenseSkill = 15;
		baseAttackSkill = 20;
		// Brute has special damage handling in damageRoll
		// Normal: 5-25, Enraged: 15-40
		baseDamageMin = 5;
		baseDamageMax = 25;
		baseMaxDR = 8; // For random 0-8 in drRoll
		baseEXP = 8;
		
		// Initialize with base values (will be properly scaled in scaleStatsByDepth)
		HP = HT = baseHT;
		defenseSkill = baseDefenseSkill;
		EXP = baseEXP;
		maxLvl = 16;
		
		loot = Gold.class;
		lootChance = 0.5f;
		
		// Apply depth scaling to all stats
		scaleStatsByDepth();
	}
	
	protected boolean hasRaged = false;
	
	@Override
	public int damageRoll() {
		// Get scaled damage values from base class
		int[] scaledDamage = getScaledDamage();
		
		if (buff(BruteRage.class) != null) {
			// When enraged, increase damage by 3x for minimum and 1.6x for maximum
			return Random.NormalIntRange(
					Math.round(scaledDamage[0] * 3.0f), 
					Math.round(scaledDamage[1] * 1.6f));
		} else {
			// Normal damage
			return Random.NormalIntRange(scaledDamage[0], scaledDamage[1]);
		}
	}
	
	@Override
	public int attackSkill( Char target ) {
		return super.attackSkill(target); // Use the scaled attack skill implementation from Mob
	}
	
	@Override
	public int drRoll() {
		return super.drRoll(); // Use the scaled DR implementation from Mob which handles baseMaxDR
	}

	@Override
	public void die(Object cause) {
		super.die(cause);

		if (cause == Chasm.class){
			hasRaged = true; //don't let enrage trigger for chasm deaths
		}
	}

	@Override
	public synchronized boolean isAlive() {
		if (super.isAlive()){
			return true;
		} else {
			if (!hasRaged){
				triggerEnrage();
			}
			return !buffs(BruteRage.class).isEmpty();
		}
	}
	
	protected void triggerEnrage(){
		// Shield is half of current HT (which is already scaled) plus 4
		int shieldAmount = HT/2 + 4;
		Buff.affect(this, BruteRage.class).setShield(shieldAmount);
		sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(shieldAmount), FloatingText.SHIELDING );
		if (Dungeon.level.heroFOV[pos]) {
			SpellSprite.show( this, SpellSprite.BERSERK);
		}
		spend( TICK );
		hasRaged = true;
	}
	
	private static final String HAS_RAGED = "has_raged";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HAS_RAGED, hasRaged);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hasRaged = bundle.getBoolean(HAS_RAGED);
	}
	
	public static class BruteRage extends ShieldBuff {
		
		{
			type = buffType.POSITIVE;
		}
		
		@Override
		public boolean act() {
			
			if (target.HP > 0){
				detach();
				return true;
			}
			
			absorbDamage( Math.round(4*AscensionChallenge.statModifier(target)));
			
			if (shielding() <= 0){
				target.die(null);
			}
			
			spend( TICK );
			
			return true;
		}
		
		@Override
		public int icon () {
			return BuffIndicator.FURY;
		}
		
		@Override
		public String desc () {
			return Messages.get(this, "desc", shielding());
		}

	}
}
