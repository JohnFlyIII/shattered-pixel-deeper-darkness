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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class SeekingMine extends TargetedArtificerSpell {

    public static final SeekingMine INSTANCE = new SeekingMine();

    @Override
    public int icon() {
        return HeroIcon.SPECTRAL_BLADES; // Using an existing icon as placeholder until we have a dedicated one
    }

    @Override
    protected void onTargetSelected(PocketWorkshop workshop, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Ballistica aim = new Ballistica(hero.pos, target, targetingFlags());

        if (Actor.findChar( aim.collisionPos ) == hero){
            GLog.i( Messages.get(Wand.class, "self_target") );
            return;
        }

        if (Actor.findChar(aim.collisionPos) != null) {
            QuickSlotButton.target(Actor.findChar(aim.collisionPos));
        } else {
            QuickSlotButton.target(Actor.findChar(target));
        }

        hero.busy();
        Sample.INSTANCE.play( Assets.Sounds.ZAP );
        hero.sprite.zap(target);
        
        // Using a fireball missile effect for the mine
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.FIRE, hero.sprite, aim.collisionPos, new Callback() {
            @Override
            public void call() {

                Char ch = Actor.findChar( aim.collisionPos );
                if (ch != null) {
                    // Deal 2-6 damage plus workshop level
                    PocketWorkshop workshop = getWorkshopForHero(hero);
                    int workshopLevel = workshop != null ? workshop.level() : 0;
                    ch.damage(Random.NormalIntRange(2, 6) + workshopLevel, SeekingMine.this);
                    Sample.INSTANCE.play(Assets.Sounds.BLAST, 1, Random.Float(0.87f, 1.15f));
                    ch.sprite.burst(0xFFCC3300, 5); // Orange/red explosion effect
                } else {
                    Dungeon.level.pressCell(aim.collisionPos);
                }

                hero.spend( 1f );
                hero.next();

                onSpellCast(workshop, hero);
            }
        });
    }

    @Override
    public float chargeUse(Hero hero) {
        return 1; // Uses 1 crafting charge as specified
    }

    public String desc(){
        // Get the workshop level for display in the description
        int workshopLevel = 0;
        if (Dungeon.hero != null) {
            PocketWorkshop workshop = getWorkshopForHero(Dungeon.hero);
            if (workshop != null) {
                workshopLevel = workshop.level();
            }
        }
        
        // Create a description that includes workshop level information
        String baseDesc = Messages.get(this, "desc");
        String damageInfo = "\n\nCurrently deals " + (2 + workshopLevel) + "-" + (6 + workshopLevel) + " damage based on your workshop level (" + workshopLevel + ").";
        
        return baseDesc + damageInfo + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }
    
    // Helper method to get the workshop
    private PocketWorkshop getWorkshopForHero(Hero hero) {
        // First check if equipped
        if (hero.belongings.artifact instanceof PocketWorkshop) {
            return (PocketWorkshop) hero.belongings.artifact;
        }
        
        // Otherwise check inventory
        for (Item item : hero.belongings.backpack) {
            if (item instanceof PocketWorkshop) {
                return (PocketWorkshop) item;
            }
        }
        
        return null;
    }
}