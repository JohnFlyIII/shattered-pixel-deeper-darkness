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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

/**
 * Factory class for creating the appropriate HeroBase instances based on HeroClass.
 * This uses the Factory pattern to instantiate the correct hero subclass.
 */
public class HeroFactory {

    /**
     * Creates a new Hero instance based on the specified HeroClass.
     * 
     * @param heroClass The class of hero to create
     * @return A new HeroBase instance of the appropriate type
     */
    public static HeroBase createHero(HeroClass heroClass) {
        switch (heroClass) {
            case WARRIOR:
                return new Warrior();
            case MAGE:
                return new Mage();
            case ROGUE:
                return new Rogue();
            case HUNTRESS:
                return new Huntress();
            case DUELIST:
                return new Duelist();
            case CLERIC:
                return new Cleric();
            case ARTIFICER:
                return new Artificer();
            default:
                // Fallback to a default hero implementation
                // This should never happen with the current design
                return new Warrior();
        }
    }

    /**
     * Creates the appropriate hero subclass for an existing hero instance.
     * This is primarily used when loading a saved game.
     * 
     * @param hero The existing hero to convert to the appropriate subclass
     * @return The same hero instance, but as the appropriate subclass
     */
    public static HeroBase convertHero(Hero hero) {
        HeroBase newHero;
        
        switch (hero.heroClass) {
            case WARRIOR:
                newHero = new Warrior();
                break;
            case MAGE:
                newHero = new Mage();
                break;
            case ROGUE:
                newHero = new Rogue();
                break;
            case HUNTRESS:
                newHero = new Huntress();
                break;
            case DUELIST:
                newHero = new Duelist();
                break;
            case CLERIC:
                newHero = new Cleric();
                break;
            case ARTIFICER:
                newHero = new Artificer();
                break;
            default:
                newHero = new Warrior();
                break;
        }
        
        // Copy all relevant data from the original hero
        copyHeroData(hero, newHero);
        
        return newHero;
    }
    
    /**
     * Copies all relevant data from one hero to another.
     * Used when converting a Hero to a HeroBase subclass.
     * 
     * @param from The source hero
     * @param to The target hero
     */
    private static void copyHeroData(Hero from, Hero to) {
        // Core properties
        to.pos = from.pos;
        //to.name = from.name;
        to.heroClass = from.heroClass;
        to.subClass = from.subClass;
        to.armorAbility = from.armorAbility;
        
        // Stats
        to.HP = from.HP;
        to.HT = from.HT;
        to.STR = from.STR;
        to.lvl = from.lvl;
        to.exp = from.exp;
        
        // Belongings, inventory, buffs would need to be handled properly
        // This is a simplified version
    }
}