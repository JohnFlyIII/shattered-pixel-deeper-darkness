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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

import java.util.ArrayList;

public abstract class ArtificerSpell {

	public abstract void onCast(PocketWorkshop tome, Hero hero);

	public float chargeUse( Hero hero ){
		return 1;
	}

	public int level() { return 0; }

	public int maxLevel(){ return 0; }

	public boolean canCast( Hero hero ){
		return true;
	}

	public String name(){
		return Messages.get(this, "name");
	}

	public String shortDesc(){
		return Messages.get(this, "short_desc") + " " + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public String desc(){
		return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	public boolean usesTargeting(){
		return false;
	}

	public int targetingFlags(){
		return -1; //-1 for no targeting
	}

	public int icon(){
		return HeroIcon.NONE;
	}

	public void onSpellCast(PocketWorkshop pocketWorkshop, Hero hero){
		Invisibility.dispel();

		// Call spendCharge which handles Efficient Crafting talent
		pocketWorkshop.spendCharge(chargeUse(hero));
		Talent.onArtifactUsed(hero);
	}

	public static ArrayList<ArtificerSpell> getSpellList(Hero artificer, int tier){
		ArrayList<ArtificerSpell> spells = new ArrayList<>();

		if (tier == 1) {
			spells.add(SeekingMine.INSTANCE);
			spells.add(MechanistsDisassembly.INSTANCE);
			if (artificer.hasTalent(Talent.AETHERIC_CLOAKING)) {
				spells.add(TrapCloaker.INSTANCE);
			}
			// Add other tier 1 spells as they are implemented
		} else if (tier == 2) {
			// Tier 2 spells will go here
			if (artificer.hasTalent(Talent.INFUSE_ESSENCE)) {
					spells.add(InfuseEssence.INSTANCE);
			}
			if (artificer.hasTalent(Talent.AETHERIC_SCANNER)) {
				spells.add(AethericScanner.INSTANCE);
			}
			if (artificer.hasTalent(Talent.MECHANICAL_ASSISTANT)) {
				spells.add(ClockworkCompanion.INSTANCE);
			}
		} else if (tier == 3) {
			// Tier 3 spells will go here
		} else if (tier == 4) {
			// Tier 4 spells will go here
		}

		return spells;
	}

	public static ArrayList<ArtificerSpell> getAllSpells() {
		ArrayList<ArtificerSpell> spells = new ArrayList<>();
		spells.add(SeekingMine.INSTANCE);
		spells.add(MechanistsDisassembly.INSTANCE);
		spells.add(TrapCloaker.INSTANCE);
		spells.add(ClockworkCompanion.INSTANCE);
		spells.add(AethericScanner.INSTANCE);
		spells.add(InfuseEssence.INSTANCE);
		// Add all other spells as they are implemented
		return spells;
	}
}