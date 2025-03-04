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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.classes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.watabou.utils.Bundle;

/**
 * Represents the Warrior hero class.
 * Warriors have higher starting strength, can use heavy equipment more easily,
 * and can use their broken seal to upgrade armor and gain shielding.
 */
public class Warrior extends HeroBase {

    private static final String SEAL = "seal";
    
    private BrokenSeal seal;

    @Override
    public void initHero() {
        heroClass = HeroClass.WARRIOR;
        
        // Initialize equipment
        (belongings.weapon = new WornShortsword()).identify();
        ThrowingStone stones = new ThrowingStone();
        stones.quantity(3).collect();
        Dungeon.quickslot.setSlot(0, stones);

        // Add the seal to armor
        if (belongings.armor != null) {
            seal = new BrokenSeal();
            belongings.armor.affixSeal(seal);
            Catalog.setSeen(BrokenSeal.class);
        }

        // Identify starting potions/scrolls
        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SEAL, seal);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        seal = (BrokenSeal)bundle.get(SEAL);
    }

    @Override
    public void applySubclass(HeroSubClass subClass) {
        super.applySubclass(subClass);
        
        if (subClass == HeroSubClass.BERSERKER) {
            // Apply any Berserker-specific initialization here
        } else if (subClass == HeroSubClass.GLADIATOR) {
            // Apply any Gladiator-specific initialization here
        }
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        // Berserker rage mechanics
        if (damage > 0 && subClass == HeroSubClass.BERSERKER) {
            Berserk berserk = buff(Berserk.class);
            if (berserk != null) {
                berserk.damage(damage);
            }
        }
        
        return super.defenseProc(enemy, damage);
    }
}