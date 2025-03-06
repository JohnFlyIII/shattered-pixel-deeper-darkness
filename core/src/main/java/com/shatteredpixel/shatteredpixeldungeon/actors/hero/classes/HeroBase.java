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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.watabou.utils.Bundle;

/**
 * Abstract base class for all hero classes.
 * Extends Hero and provides a foundation for class-specific behaviors.
 */
public abstract class HeroBase extends Hero {

    /**
     * Initializes the hero with class-specific properties.
     * This method is called when a new game is started or when a game is loaded.
     * 
     * 1. Sets the hero's class
     * 2. Initializes class-specific equipment and items
     * 3. Handles any other class-specific initialization
     */
    public abstract void initHero();
    
    /**
     * Common initialization shared by all hero classes.
     * Called by HeroClass.initHero before class-specific initialization.
     * 
     * @param heroClass The class being initialized
     */
    public void initCommon(HeroClass heroClass) {
        this.heroClass = heroClass;
        Talent.initClassTalents(this);
    }

    /**
     * Called when a hero gains a subclass. Handles any special effects, buffs, or abilities
     * that are granted when a hero chooses a subclass.
     * 
     * @param subClass The subclass being assigned to the hero
     */
    public void applySubclass(HeroSubClass subClass) {
        // Default implementation does nothing
        // Subclasses should override this if they need special handling
    }

    /**
     * Called when a hero gains an armor ability. Handles any special effects, buffs, or abilities
     * that are granted when a hero chooses an armor ability.
     * 
     * @param ability The armor ability being assigned to the hero
     */
    public void applyArmorAbility(ArmorAbility ability) {
        // Default implementation does nothing
        // Subclasses should override this if they need special handling
    }

    /**
     * Handles class-specific attack processing.
     * 
     * @param enemy The target of the attack
     * @param damage The base damage amount
     * @return The modified damage amount
     */
    public int attackProc(Char enemy, int damage) {
        // Default implementation just returns the damage unchanged
        // Subclasses should override this to add class-specific effects
        return damage;
    }

    /**
     * Handles class-specific defense processing.
     * 
     * @param enemy The character attacking this hero
     * @param damage The base damage amount
     * @return The modified damage amount
     */
    public int defenseProc(Char enemy, int damage) {
        // Default implementation just returns the damage unchanged
        // Subclasses should override this to add class-specific effects
        return damage;
    }

    /**
     * Stacks additional elements into a bundle for saving.
     * Subclasses should override this to save their specific state.
     * 
     * @param bundle The bundle to stack into
     */
    public void storeInBundle(Bundle bundle) {
        // Default implementation does nothing
        // Subclasses should override this to save their state
    }

    /**
     * Restores this hero subclass from a bundle.
     * Subclasses should override this to restore their specific state.
     * 
     * @param bundle The bundle to restore from
     */
    public void restoreFromBundle(Bundle bundle) {
        // Default implementation does nothing
        // Subclasses should override this to restore their state
    }
}