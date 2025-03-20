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
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MechanistsDisassembly extends TargetedArtificerSpell {

    public static final MechanistsDisassembly INSTANCE = new MechanistsDisassembly();
    
    // Base chance the trap is not triggered when disassembling
    private static final float BASE_SAFETY_CHANCE = 0.10f; // 10% base chance plus 10% per workshop level
    
    // Parts gained from successful disassembly
    private static final int PARTS_GAINED = 10;

    @Override
    public int icon() {
        return HeroIcon.SMOKE_BOMB; // Using an existing icon as placeholder until we have a dedicated one
    }

    @Override
    protected void onTargetSelected(PocketWorkshop workshop, Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        final int cell = target;
        
        if (Actor.findChar(cell) == hero) {
            GLog.i(Messages.get(Wand.class, "self_target"));
            return;
        }

        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
        hero.sprite.zap(target);
        
        // Use a blue magic missile effect
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.FROST, hero.sprite, cell, new Callback() {
            @Override
            public void call() {
                Trap trap = Dungeon.level.traps.get(cell);
                
                if (trap != null && trap.visible) {
                    // Found a visible trap to disassemble
                    // Calculate success chance: 10% + 10% per workshop level (capped at 100%)
                    int workshopLevel = workshop != null ? workshop.level() : 0;
                    float successChance = Math.min(1.0f, BASE_SAFETY_CHANCE + (0.10f * workshopLevel));
                    boolean success = Random.Float() < successChance;
                    
                    if (success) {
                        // Successfully disassembled without triggering
                        disarmTrapWithVisualEffect(trap, cell);
                        workshop.addSpareParts(PARTS_GAINED);
                        GLog.p(Messages.get(MechanistsDisassembly.class, "disarm_success", PARTS_GAINED));
                        GLog.p("You have " + workshop.getSpareParts() + " spare parts." );
                    } else {
                        // Failed to disassemble safely, trigger the trap
                        GLog.w(Messages.get(MechanistsDisassembly.class, "disarm_failure"));
                        trap.trigger();
                    }
                } else {
                    // No visible trap found at target location
                    GLog.i(Messages.get(MechanistsDisassembly.class, "no_trap"));
                }

                hero.spend(1f);
                hero.next();
                onSpellCast(workshop, hero);
            }
        });
    }
    
    @Override
    public int targetingFlags() {
        return Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
    }
    
    private void disarmTrapWithVisualEffect(Trap trap, int pos) {
        // Create visual effects for trap disassembly
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        trap.disarm();
        
        // Add particles effects
        Emitter emitter = CellEmitter.get(pos);
        emitter.burst(Speck.factory(Speck.STAR, false), 10);
    }

    @Override
    protected String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2; // Uses 2 crafting charges
    }

    public String desc(){
        // Get the workshop level for display in the description
        int workshopLevel = 0;
        if (Dungeon.hero != null) {
            // Check equipped artifact
            if (Dungeon.hero.belongings.artifact instanceof PocketWorkshop) {
                workshopLevel = Dungeon.hero.belongings.artifact.level();
            } else {
                // Check inventory
                for (Item item : Dungeon.hero.belongings.backpack) {
                    if (item instanceof PocketWorkshop) {
                        workshopLevel = item.level();
                        break;
                    }
                }
            }
        }
        
        // Calculate success chance for the description (10% + 10% per level, cap at 100%)
        float successChance = Math.min(1.0f, BASE_SAFETY_CHANCE + (0.10f * workshopLevel));
        int successPercent = Math.round(successChance * 100);
        
        // Create a description that includes success chance information
        String baseDesc = Messages.get(this, "desc");
        String chanceInfo = "\n\nCurrently has a " + successPercent + "% chance to succeed based on your workshop level (" + workshopLevel + ").";
        
        return baseDesc + chanceInfo + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }
}