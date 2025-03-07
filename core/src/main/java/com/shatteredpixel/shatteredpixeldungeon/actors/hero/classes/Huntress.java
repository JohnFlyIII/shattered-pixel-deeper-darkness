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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;

/**
 * Placeholder implementation for the Huntress hero class.
 * Will be filled out with complete implementations in the future.
 */
public class Huntress extends HeroBase {

    public static void initHero(Hero hero) {
        hero.heroClass = HeroClass.HUNTRESS;
        
        // Initialize equipment
        (hero.belongings.weapon = new Gloves()).identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();

        Dungeon.quickslot.setSlot(0, bow);

        // Identify starting potions/scrolls
        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    @Override
    public void initHero() {
        initCommon(HeroClass.HUNTRESS);
        Huntress.initHero(this);
    }
    @Override
    public void applySubclass(HeroSubClass subClass) {
        super.applySubclass(subClass);
        
        if (subClass == HeroSubClass.SNIPER) {
            // Apply Sniper-specific initialization here
        } else if (subClass == HeroSubClass.WARDEN) {
            // Apply Warden-specific initialization here
        }
    }
}