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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class AethericCloaking extends FlavourBuff {
	
	public static final float COOLDOWN = 30f;
	
	{
		type = buffType.POSITIVE;
		announced = true;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.INVISIBLE; // Using invisibility icon for now
	}
	
	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.5f, 0.8f, 1f); // Blue-ish tint to differentiate from regular invisibility
	}
	
	@Override
	public float iconFadePercent() {
		return Math.max(0, (COOLDOWN - visualcooldown()) / COOLDOWN);
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.INVISIBLE);
		else target.sprite.remove(CharSprite.State.INVISIBLE);
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
	
	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
	
	// Static method to activate the cloaking
	public static void activate(Hero hero) {
		if (hero == null) return;
		
		if (hero.hasTalent(Talent.AETHERIC_CLOAKING)) {
			// Duration based on talent level: 4/8 turns
			int duration = 4 * hero.pointsInTalent(Talent.AETHERIC_CLOAKING);
			Buff.affect(hero, AethericCloaking.class, duration);
			Buff.affect(hero, AethericCloakingCooldown.class, COOLDOWN);
		}
	}
	
	// Helper method to check if the hero is "trap-invisible"
	public static boolean isCloakedFromTraps(Hero hero) {
		return hero != null && hero.buff(AethericCloaking.class) != null;
	}
	
	// Cooldown buff
	public static class AethericCloakingCooldown extends FlavourBuff {
		
		{
			type = buffType.NEUTRAL;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		
		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.5f, 0.8f, 1f);
		}
		
		@Override
		public float iconFadePercent() {
			return Math.max(0, (COOLDOWN - visualcooldown()) / COOLDOWN);
		}
		
		@Override
		public String toString() {
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns());
		}
	}
}