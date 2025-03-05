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

import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
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

    private static final String CHARGES = "fullTankCharges";
    
    // Number of charges accumulated by the Full Tank talent
    private int fullTankCharges = 0;
    
    @Override
    public void initHero() {
        heroClass = HeroClass.ARTIFICER;
        
        // Based on Huntress with additional items
        (belongings.weapon = new Gloves()).identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();

        // Add Warrior's seal
        if (belongings.armor != null) {
            belongings.armor.affixSeal(new BrokenSeal());
            Catalog.setSeen(BrokenSeal.class);
        }

        // Two potions of strength
        PotionOfStrength potion = new PotionOfStrength();
        potion.identify();
        potion.collect();
        potion = new PotionOfStrength();
        potion.identify();
        potion.collect();

        // Two scrolls of upgrade
        ScrollOfUpgrade scroll = new ScrollOfUpgrade();
        scroll.identify();
        scroll.collect();
        scroll = new ScrollOfUpgrade();
        scroll.identify();
        scroll.collect();

        // Add a random ring
        Ring ring = (Ring) Generator.random(Generator.Category.RING);
        ring.identify().collect();

        Wand wand = (Wand) Generator.random(Generator.Category.WAND);
        wand.identify().collect();

        Dungeon.quickslot.setSlot(0, bow);

        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CHARGES, fullTankCharges);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        fullTankCharges = bundle.getInt(CHARGES);
    }
    
    /**
     * Add charges to the Full Tank talent
     * @param amount Number of charges to add
     */
    public void addFullTankCharges(int amount) {
        fullTankCharges += amount;
    }
    
    /**
     * Get the current number of Full Tank charges
     * @return Current charges
     */
    public int getFullTankCharges() {
        return fullTankCharges;
    }
    
    /**
     * Use Full Tank charges
     * @param amount Number of charges to use
     * @return True if enough charges were available and consumed
     */
    public boolean useFullTankCharges(int amount) {
        if (fullTankCharges >= amount) {
            fullTankCharges -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Handles special behavior when an Artificer eats food
     * - Implements the Full Tank talent
     * 
     * @param food The food item being eaten
     */
    public void onEatFood(Food food) {
        // If the hero has the Full Tank talent, gain charges
        if (hasTalent(Talent.FULL_TANK)) {
            int charges = pointsInTalent(Talent.FULL_TANK);
            addFullTankCharges(charges);
        }
    }
}