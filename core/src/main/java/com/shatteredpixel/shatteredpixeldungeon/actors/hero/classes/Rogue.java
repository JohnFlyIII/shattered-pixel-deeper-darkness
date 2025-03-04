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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;

/**
 * Placeholder implementation for the Rogue hero class.
 * Will be filled out with complete implementations in the future.
 */
public class Rogue extends HeroBase {

    @Override
    public void initHero() {
        heroClass = HeroClass.ROGUE;
        
        // Initialize equipment
        (belongings.weapon = new Dagger()).identify();

        CloakOfShadows cloak = new CloakOfShadows();
        (belongings.artifact = cloak).identify();
        belongings.artifact.activate(this);

        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3).collect();

        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);

        // Identify starting potions/scrolls
        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }
    
    @Override
    public void applySubclass(HeroSubClass subClass) {
        super.applySubclass(subClass);
        
        if (subClass == HeroSubClass.ASSASSIN) {
            // Apply Assassin-specific initialization here
        } else if (subClass == HeroSubClass.FREERUNNER) {
            // Apply Freerunner-specific initialization here
        }
    }
}