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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AethericCloaking;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
public class TrapCloaker extends ArtificerSpell {

    public static final TrapCloaker INSTANCE = new TrapCloaker();
    
    @Override
    public boolean canCast(Hero hero) {
        if (!hero.hasTalent(Talent.AETHERIC_CLOAKING)) {
            GLog.w(Messages.get(this, "no_talent"));
            return false;
        }
        
        if (hero.buff(AethericCloaking.AethericCloakingCooldown.class) != null) {
            GLog.w(Messages.get(this, "cooling_down"));
            return false;
        }
        
        return super.canCast(hero);
    }

    @Override
    public void onCast(PocketWorkshop workshop, Hero hero) {
        // Activate aetheric cloaking
        AethericCloaking.activate(hero);
        
        Item.updateQuickslot();
        
        hero.sprite.emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        Sample.INSTANCE.play(Assets.Sounds.MELD);
        
        // Use 1 turn to activate
        hero.spend(Actor.TICK);
        hero.busy();
        hero.next();
        
        onSpellCast(workshop, hero);
    }



    @Override
    public int level() {
        // Level based on talent points
        return (Dungeon.hero != null && Dungeon.hero.hasTalent(Talent.AETHERIC_CLOAKING)) 
                ? Dungeon.hero.pointsInTalent(Talent.AETHERIC_CLOAKING) 
                : 0;
    }

    @Override
    public int maxLevel() {
        return 2; // Same as talent max level
    }
    
    @Override
    public int icon() {
        return HeroIcon.SMOKE_BOMB; // Using the smoke bomb icon for now
    }
}