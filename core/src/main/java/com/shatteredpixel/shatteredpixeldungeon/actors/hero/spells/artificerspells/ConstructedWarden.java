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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.TargetedArtificerSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding.Ward;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import java.lang.reflect.Field;

public class ConstructedWarden extends TargetedArtificerSpell {

	public static final ConstructedWarden INSTANCE = new ConstructedWarden();
	
	private int imageIndex = ItemSpriteSheet.WAND_WARDING;
	
	@Override
	public int icon() {
		return imageIndex;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	@Override
	public String name() {
		return Messages.get(this, "name");
	}

	@Override
	public float chargeUse(Hero hero) {
		int talentLevel = hero.pointsInTalent(Talent.CONSTRUCTED_WARDEN);
		if (talentLevel == 2) {
			return 2; // Level 2 uses less charges (2 instead of 3)
		} else {
			return 3; // Level 1 and 3 use 3 charges
		}
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		
		if (Dungeon.hero != null) {
			int talentLevel = Dungeon.hero.pointsInTalent(Talent.CONSTRUCTED_WARDEN);
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
		}
		
		desc += "\n\n" + Messages.get(this, "cost", 3);
		
		return desc;
	}

	@Override
	public boolean canCast(Hero hero) {
		int talentLevel = hero.pointsInTalent(Talent.CONSTRUCTED_WARDEN);
		if (talentLevel == 0) {
			GLog.w(Messages.get(this, "no_talent"));
			return false;
		}
		return super.canCast(hero);
	}

	@Override
	protected void onTargetSelected(PocketWorkshop workshop, Hero hero, Integer target) {
		if (target == null) return;
		
		if (!Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "no_los"));
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch != null && !(ch instanceof Ward)) {
			Ballistica ball = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);
			if (ball.collisionPos != target) {
				target = ball.path.get(ball.path.size() - 2);
			} else {
				GLog.w(Messages.get(this, "bad_location"));
				return;
			}
			ch = Actor.findChar(target);
			if (ch != null && !(ch instanceof Ward)) {
				GLog.w(Messages.get(this, "bad_location"));
				return;
			}
		}

		int talentLevel = hero.pointsInTalent(Talent.CONSTRUCTED_WARDEN);
		
		// Create new ward
		Ward ward = new WandOfWarding.Ward();
		ward.pos = target;
		
		// Initialize the ward's level through upgrading
		// Store the internal wandLevel via reflection or direct field access
		try {
			java.lang.reflect.Field wandLevelField = Ward.class.getDeclaredField("wandLevel");
			wandLevelField.setAccessible(true);
			wandLevelField.set(ward, Math.max(1, talentLevel));
		} catch (Exception e) {
			// If reflection fails, we'll just use upgrade
			ward.upgrade(Math.max(1, talentLevel));
		}
		
		GameScene.add(ward, 1f);
		Dungeon.level.occupyCell(ward);
		
		// Upgrade the ward based on talent level
		if (talentLevel == 2) {
			// Upgrade once (equivalent to firing wand twice)
			ward.upgrade(talentLevel);
		} else if (talentLevel == 3) {
			// Upgrade multiple times (equivalent to firing wand 5 times)
			for (int i = 0; i < 4; i++) {
				ward.upgrade(talentLevel);
			}
		}
		
		ward.sprite.emitter().burst(MagicMissile.WardParticle.UP, ward.tier);
		
		workshop.spendCharge(chargeUse(hero));
		
		hero.sprite.zap(target);
		fx(target, new Callback() {
			@Override
			public void call() {
				onSpellCast(workshop, hero);
			}
		});
	}

	private void spendCharges(Hero hero, float amount) {
		for (PocketWorkshop workshop : hero.belongings.getAllItems(PocketWorkshop.class)) {
			if (workshop.isEquipped(hero) || hero.hasTalent(Talent.AETHERIC_EXPANSION)) {
				workshop.spendCharge(amount);
				return;
			}
		}
	}

	public void fx(int cell, Callback callback) {
		MagicMissile.boltFromChar(
				Dungeon.hero.sprite.parent,
				MagicMissile.WARD,
				Dungeon.hero.sprite,
				cell,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	public static class ConstructedWardenBuff extends NPC {
		// Any buff effects specific to the constructed warden could go here
	}
}