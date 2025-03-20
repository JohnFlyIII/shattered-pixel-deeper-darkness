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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ClockworkAutomaton;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;

// A spell to create a mechanical ally that helps in combat
public class ClockworkCompanion extends TargetedArtificerSpell {

    public static final ClockworkCompanion INSTANCE = new ClockworkCompanion();

    @Override
    public int icon() {
        return HeroIcon.CHALLENGE; // Placeholder icon
    }

    @Override
    public int targetingFlags() {
        return Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
    }

    @Override
    protected String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void onTargetSelected(PocketWorkshop workshop, Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        if (Actor.findChar(target) != null || !Dungeon.level.passable[target]) {
            GLog.w(Messages.get(this, "no_space"));
            return;
        }

        // Check if talent is available
        if (!hero.hasTalent(Talent.MECHANICAL_ASSISTANT)) {
            GLog.w(Messages.get(this, "no_talent"));
            return;
        }

        hero.busy();
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
        hero.sprite.zap(target);

        // Create visual effect
        MagicMissile.boltFromChar(hero.sprite.parent, MagicMissile.FORCE, hero.sprite, target, new Callback() {
            @Override
            public void call() {
                // Create and place the mechanical ally
                boolean mobile = hero.pointsInTalent(Talent.MECHANICAL_ASSISTANT) > 1;
                
                ClockworkAutomaton automaton = new ClockworkAutomaton();
                automaton.pos = target;
                automaton.activate(hero, mobile);
                GameScene.add(automaton);
                
                CellEmitter.get(target).burst(Speck.factory(Speck.FORGE), 6);
                Sample.INSTANCE.play(Assets.Sounds.PUFF);
                
                GLog.i(Messages.get(ClockworkCompanion.class, 
                        mobile ? "companion_mobile" : "companion_stationary"));
                
                // Spend the charge
                onSpellCast(workshop, hero);
                
                hero.spend(1f);
                hero.busy();
                hero.next();
            }
        });
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3; // Uses 3 crafting charges
    }

    public String desc() {
        int talentLevel = Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.MECHANICAL_ASSISTANT) : 0;
        
        String baseDesc = Messages.get(this, "desc");
        String talentInfo = "\n\n" + Messages.get(this, talentLevel == 2 ? "desc_enhanced" : "desc_basic");
        
        return baseDesc + talentInfo + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }
}