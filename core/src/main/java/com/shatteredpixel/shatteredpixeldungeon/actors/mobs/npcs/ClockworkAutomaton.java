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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MirrorSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ClockworkAutomaton extends NPC {
	
	{
		spriteClass = MirrorSprite.class; // Using MirrorSprite as placeholder until proper sprite is created
		
		HP = HT = 15; // Slightly more durable than prismatic image
		defenseSkill = 1;
		
		alignment = Alignment.ALLY;
		intelligentAlly = true;
		
		// Default to immobile (T1)
		WANDERING = new Idling();
		state = WANDERING;
		
		//before other mobs
		actPriority = MOB_PRIO + 1;
	}
	
	private Hero hero;
	private int heroID;
	private boolean mobile;
	private int duration = 30; // Automaton lasts for 30 turns
	
	@Override
	protected boolean act() {
		duration--;
		
		if (duration <= 0) {
			die(null);
			return true;
		}
		
		if (hero == null) {
			hero = (Hero) Actor.findById(heroID);
			if (hero == null) {
				destroy();
				sprite.die();
				return true;
			}
		}
		
		return super.act();
	}
	
	@Override
	public void die(Object cause) {
		CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 10);
		Sample.INSTANCE.play(Assets.Sounds.HIT_CRUSH);
		
		super.die(cause);
	}
	
	private static final String HEROID = "hero_id";
	private static final String MOBILE = "mobile";
	private static final String DURATION = "duration";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HEROID, heroID);
		bundle.put(MOBILE, mobile);
		bundle.put(DURATION, duration);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		heroID = bundle.getInt(HEROID);
		mobile = bundle.getBoolean(MOBILE);
		duration = bundle.getInt(DURATION);
		
		// Set the correct AI state based on mobility
		if (mobile) {
			WANDERING = new Wandering();
		} else {
			WANDERING = new Idling();
		}
	}
	
	public void activate(Hero hero, boolean mobile) {
		this.hero = hero;
		this.heroID = hero.id();
		this.mobile = mobile;
		
		// Enhanced with Talent level 2
		if (mobile) {
			WANDERING = new Wandering();
			state = HUNTING; // Mobile automaton starts in hunting mode
			HT = HP = 20; // More HP when enhanced
		} else {
			WANDERING = new Idling();
			state = WANDERING; // Stationary automaton starts idling
		}
	}
	
	@Override
	public int damageRoll() {
		if (hero != null) {
			// Base damage similar to prismatic image but slightly better with T2 talent
			int damage = Random.NormalIntRange(2 + hero.lvl/4, 4 + hero.lvl/2);
			if (mobile) damage += 2; // Extra damage with T2 talent
			return damage;
		} else {
			return Random.NormalIntRange(2, 4);
		}
	}
	
	@Override
	public int attackSkill(Char target) {
		if (hero != null) {
			// Similar to hero's attack skill
			int accuracy = (int)((9 + hero.lvl) * RingOfAccuracy.accuracyMultiplier(hero));
			if (mobile) accuracy += 2; // Enhanced accuracy with T2 talent
			return accuracy;
		} else {
			return 0;
		}
	}
	
	@Override
	public int defenseSkill(Char enemy) {
		if (hero != null) {
			int evasion = 4 + hero.lvl;
			if (mobile) evasion += 2; // Better evasion with T2 talent
			return evasion;
		} else {
			return 0;
		}
	}
	
	@Override
	public int drRoll() {
		int dr = super.drRoll();
		if (hero != null) {
			dr += 1 + hero.lvl/4;
			if (mobile) dr += 2; // Extra damage reduction with T2 talent
		}
		return dr;
	}
	
	@Override
	public int attackProc(Char enemy, int damage) {
		if (enemy instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob) {
			((com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob)enemy).aggro(this);
		}
		
		return super.attackProc(enemy, damage);
	}
	
	@Override
	public String description() {
		return Messages.get(this, mobile ? "desc_mobile" : "desc_stationary");
	}
	
	{
		immunities.add(ToxicGas.class);
		immunities.add(CorrosiveGas.class);
		immunities.add(Burning.class);
		immunities.add(AllyBuff.class);
	}
	
	// Stationary behavior - doesn't move but attacks enemies in range
	private class Idling extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				// If enemy is in view, switch to hunting
				state = HUNTING;
				target = enemy.pos;
				return true;
			}
			spend(TICK);
			return true;
		}
	}
	
	// Mobile behavior - follows the hero and hunts enemies
	private class Wandering extends Mob.Wandering {
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				// If enemy is in view, switch to hunting
				state = HUNTING;
				target = enemy.pos;
				return true;
			} else {
				// If no enemy, follow the hero
				if (hero != null && hero.isAlive()) {
					target = hero.pos;
					int oldPos = pos;
					
					// Try to move closer to the hero
					if (getCloser(target)) {
						spend(1 / speed());
						return moveSprite(oldPos, pos);
					} else {
						spend(TICK);
					}
				}
				return true;
			}
		}
	}
}