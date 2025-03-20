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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BeamingRay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BlessSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.AethericScanner;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.ClockworkCompanion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Cleanse;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineIntervention;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Flash;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HallowedGround;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyIntuition;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyLance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Judgement;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LayOnHands;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MindForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.MnemonicPrayer;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Radiance;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallInscription;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ShieldOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Smite;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Sunray;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
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
			if (artificer.hasTalent(Talent.MECHANICAL_ASSISTANT)) {
				spells.add(ClockworkCompanion.INSTANCE);
			}
			if (artificer.hasTalent(Talent.AETHERIC_SCANNER)) {
				spells.add(AethericScanner.INSTANCE);
			}
			// Add other tier 1 spells as they are implemented
		} else if (tier == 2) {
			// Tier 2 spells will go here
			if (artificer.hasTalent(Talent.INFUSE_ESSENCE)) {
				try {
					Class.forName("com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.InfuseEssence");
					spells.add(InfuseEssence.INSTANCE);
				} catch (ClassNotFoundException e) {
					// Not implemented yet, that's fine
				}
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
		// Check if InfuseEssence class exists yet
		try {
			Class.forName("com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.InfuseEssence");
			spells.add(InfuseEssence.INSTANCE);
		} catch (ClassNotFoundException e) {
			// Not implemented yet, that's fine
		}
		// Add all other spells as they are implemented
		return spells;
	}
}