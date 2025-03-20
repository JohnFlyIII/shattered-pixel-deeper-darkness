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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.artificer.WndInfuseEssence;
import com.watabou.noosa.audio.Sample;

public class InfuseEssence extends ArtificerSpell {

    public static final InfuseEssence INSTANCE = new InfuseEssence();

    @Override
    public int icon() {
        return HeroIcon.NONE; // Using upgrade icon for Infuse Essence
    }

    @Override
    public void onCast(PocketWorkshop workshop, Hero hero) {
        // Check if the hero has the talent
        if (!hero.hasTalent(Talent.INFUSE_ESSENCE)) {
            return;
        }
        
        // Play upgrade sound
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        
        // Show the infuse essence window
        GameScene.show(new WndInfuseEssence(hero));
        
        // Don't consume any charges just for showing the window
        // The actual charge consumption will happen in WndInfuseEssence
        // on successful infusion
    }

    @Override
    public float chargeUse(Hero hero) {
        // No charge is used just for opening the window
        // The actual charge is used when an item is infused
        return 0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        // Can only cast if hero has the INFUSE_ESSENCE talent
        return hero.hasTalent(Talent.INFUSE_ESSENCE);
    }
}