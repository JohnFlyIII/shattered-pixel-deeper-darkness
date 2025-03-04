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
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;

/**
 * Placeholder implementation for the Cleric hero class.
 * Will be filled out with complete implementations in the future.
 */
public class Cleric extends HeroBase {

    @Override
    public void initHero() {
        heroClass = HeroClass.CLERIC;
        
        // Initialize equipment
        (belongings.weapon = new Cudgel()).identify();
        belongings.weapon.activate(this);

        HolyTome tome = new HolyTome();
        (belongings.artifact = tome).identify();
        belongings.artifact.activate(this);

        Dungeon.quickslot.setSlot(0, tome);

        // Identify starting potions/scrolls
        new PotionOfPurity().identify();
        new ScrollOfRemoveCurse().identify();
    }
    
    @Override
    public void applySubclass(HeroSubClass subClass) {
        super.applySubclass(subClass);
        
        if (subClass == HeroSubClass.PRIEST) {
            // Apply Priest-specific initialization here
        } else if (subClass == HeroSubClass.PALADIN) {
            // Apply Paladin-specific initialization here
        }
    }
}