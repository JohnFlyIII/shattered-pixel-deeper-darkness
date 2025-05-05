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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.ModularInfusionRelay;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.watabou.utils.Bundle;

/**
 * Represents the Artificer hero class.
 * Artificers are masters of magical items and engineering.
 * They can harness the power of various artifacts and wands more effectively.
 */
public class Artificer extends HeroBase {
    /**
     * Static method to initialize an Artificer hero
     * Used by HeroClass to provide consistent initialization
     * 
     * @param hero The hero to initialize with Artificer-specific equipment and abilities
     */
    public static void initHero(Hero hero) {
        hero.heroClass = HeroClass.ARTIFICER;

        // Based on Huntress with additional items
        (hero.belongings.weapon = new Gloves()).identify();
        ModularInfusionRelay relay = new ModularInfusionRelay();
        relay.identify().collect();

        // Add Warrior's seal
        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
            Catalog.setSeen(BrokenSeal.class);
        }

        var workshop = new PocketWorkshop().identify();
        ((PocketWorkshop)workshop).addSpareParts(200);
        workshop.collect();

        new PotionOfExperience().quantity(20).identify().collect();
        // Two potions of strength
        new PotionOfStrength().quantity(2).identify().collect();

        // Two scrolls of upgrade
        new ScrollOfUpgrade().quantity(2).identify().collect();

        // Add a random ring
        Ring ring = (Ring) Generator.random(Generator.Category.RING);
        ring.identify().collect();

        Wand wand = (Wand) Generator.random(Generator.Category.WAND);
        wand.identify().collect();
        
        // Add the Amulet of Yendor to the Artificer's starting inventory
        new Amulet().collect();
        new TengusMask().collect();

        Dungeon.quickslot.setSlot(0, relay);

        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }
    
    @Override
    public void initHero() {
        initCommon(HeroClass.ARTIFICER);
        Artificer.initHero(this);
    }
}