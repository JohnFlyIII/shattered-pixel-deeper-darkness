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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class ImprovisedEthericPlating extends ArtificerSpell {

	public static final ImprovisedEthericPlating INSTANCE = new ImprovisedEthericPlating();
	
	private static final int ICON_INDEX = HeroIcon.IMPROVISED_ETHERIC_PLATING;
	
	@Override
	public int icon() {
		return ICON_INDEX;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public float chargeUse(Hero hero) {
		int talentLevel = hero.pointsInTalent(Talent.IMPROVISED_ETHERIC_PLATING);
		if (talentLevel >= 2) {
			return 2; // Level 2 and 3 use 2 charges
		} else {
			return 3; // Level 1 uses 3 charges
		}
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		
		if (Dungeon.hero != null) {
			int talentLevel = Dungeon.hero.pointsInTalent(Talent.IMPROVISED_ETHERIC_PLATING);
			desc += "\n\n";
			
			if (talentLevel == 0) {
				desc += Messages.get(this, "need_talent");
			} else if (talentLevel == 1) {
				desc += Messages.get(this, "desc_level1");
			} else if (talentLevel == 2) {
				desc += Messages.get(this, "desc_level2");
			} else if (talentLevel == 3) {
				desc += Messages.get(this, "desc_level3");
			}

			int chargeUse = (int)chargeUse(Dungeon.hero);
			desc += "\n\n" + Messages.get(this, "cost", chargeUse);
		}
		
		return desc;
	}

	@Override
	public boolean canCast(Hero hero) {
		int talentLevel = hero.pointsInTalent(Talent.IMPROVISED_ETHERIC_PLATING);
		if (talentLevel == 0) {
			GLog.w(Messages.get(this, "no_talent"));
			return false;
		}
		
		// Check if the hero already has the buff
		if (hero.buff(EthericShield.class) != null) {
			GLog.w(Messages.get(this, "already_active"));
			return false;
		}
		
		return super.canCast(hero);
	}

	@Override
	public void onCast(PocketWorkshop workshop, Hero hero) {
		int talentLevel = hero.pointsInTalent(Talent.IMPROVISED_ETHERIC_PLATING);
		int workshopLevel = workshop.level();
		
		int shieldAmount = hero.lvl * workshopLevel;
		int duration = 45;
		
		// Apply talent level modifiers
		if (talentLevel == 2) {
			duration = 90;
		} else if (talentLevel == 3) {
			shieldAmount *= 2;
			duration = 180;
		}
		
		Buff.affect(hero, EthericShield.class).setShield(shieldAmount, duration);
		
		hero.sprite.emitter().burst(EnergyParticle.FACTORY, 15);
		hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldAmount), BuffIndicator.ARMOR);
		
		GLog.p(Messages.get(this, "shield_applied", shieldAmount));
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		
		onSpellCast(workshop, hero);
	}

	// Buff class for the etheric shielding effect
	public static class EthericShield extends com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff {
		
		private int duration;
		
		@Override
		public boolean act() {
			duration--;
			if (duration <= 0) {
				detach();
			}
			
			spend(TICK);
			return true;
		}
		
		public EthericShield setShield(int amount, int duration) {
			setShield(amount);
			this.duration = duration;
			return this;
		}
		
		@Override
		public int icon() {
			return BuffIndicator.ARMOR;
		}
		
		@Override
		public void tintIcon(com.watabou.noosa.Image icon) {
			icon.hardlight(0.5f, 0.8f, 1f); // Light blue tint
		}
		
		@Override
		public float iconFadePercent() {
			return (float)(1.0f - visualcooldown() / duration);
		}
		
		@Override
		public String toString() {
			return Messages.get(this, "name");
		}
		
		@Override
		public String desc() {
			return Messages.get(this, "desc", shielding(), duration);
		}
		
		private static final String DURATION = "duration";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DURATION, duration);
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			duration = bundle.getInt(DURATION);
		}
	}
}