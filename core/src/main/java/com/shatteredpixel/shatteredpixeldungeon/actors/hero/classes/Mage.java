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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.watabou.utils.Bundle;

/**
 * Represents the Mage hero class.
 * Mages start with a unique staff that can be imbued with the power of wands,
 * have more effective wands, and can use magic more effectively.
 */
public class Mage extends HeroBase {
    
    private Wand wand;
    

    public static void initHero(Hero hero) {
        // Initialize the mage's staff with a magic missile wand
        MagesStaff staff = new MagesStaff(new WandOfMagicMissile());
        
        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.activate(hero);
        
        Dungeon.quickslot.setSlot(0, staff);
        
        // Identify starting potions/scrolls
        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();
    }

    @Override
    public void initHero() {
        initCommon(HeroClass.MAGE);
        initHero(this);
    }
    
    @Override
    public void applySubclass(HeroSubClass subClass) {
        super.applySubclass(subClass);
        
        if (subClass == HeroSubClass.BATTLEMAGE) {
            // Apply Battlemage-specific initialization here
        } else if (subClass == HeroSubClass.WARLOCK) {
            // Apply Warlock-specific initialization here
        }
    }
    
    @Override
    public int attackProc(Char enemy, int damage) {
        // Apply any Mage-specific attack procs here
        
        if (subClass == HeroSubClass.BATTLEMAGE) {
            if (belongings.weapon() instanceof MagesStaff) {
                // Battle mage staff on-hit effects would go here
            }
        }
        
        return super.attackProc(enemy, damage);
    }
    
    @Override
    public int damageRoll() {
        // Apply weapon recharging bonus damage
        int dmg = super.damageRoll();
        
        if (heroClass != HeroClass.DUELIST
                && hasTalent(Talent.WEAPON_RECHARGING)
                && (buff(Recharging.class) != null || buff(ArtifactRecharge.class) != null)) {
            dmg = Math.round(dmg * 1.025f + (.025f*pointsInTalent(Talent.WEAPON_RECHARGING)));
        }
        
        return dmg;
    }
}