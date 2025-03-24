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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class AethericScanner extends ArtificerSpell {

    public static final AethericScanner INSTANCE = new AethericScanner();

    @Override
    public int icon() {
        return HeroIcon.MIND_FORM; // Using mind vision icon as a placeholder
    }

    @Override
    public void onCast(PocketWorkshop workshop, Hero hero) {
        // Check if talent is available
        if (!hero.hasTalent(Talent.AETHERIC_SCANNER)) {
            GLog.w(Messages.get(this, "no_talent"));
            return;
        }

        int talentLevel = hero.pointsInTalent(Talent.AETHERIC_SCANNER);
        int workshopLevel = workshop != null ? workshop.level() : 0;
        
        // Calculate effect duration based on talent level
        int duration;
        if (talentLevel == 1) {
            duration = 5 + workshopLevel;
        } else {
            duration = 10 + (2 * workshopLevel);
        }
        
        // Calculate vision radius based on talent level
        int visionRadius = talentLevel == 1 ? 2 : 3;

        // Apply the effect
        Buff.affect(hero, WallScanner.class, duration).radius = visionRadius;
        hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(duration), BuffIndicator.MIND_VISION);
        
        Sample.INSTANCE.play(Assets.Sounds.SCAN);
        SpellSprite.show(hero, SpellSprite.VISION);
        GameScene.updateFog();
        
        GLog.i(Messages.get(this, "activated", visionRadius, duration));
        
        onSpellCast(workshop, hero);
        return;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2; // Uses 2 crafting charges
    }

    public String desc() {
        int talentLevel = Dungeon.hero != null ? Dungeon.hero.pointsInTalent(Talent.AETHERIC_SCANNER) : 0;
        int workshopLevel = 0;
        
        for (PocketWorkshop workshop : getWorkshopsForHero(Dungeon.hero)) {
            if (workshop != null) {
                workshopLevel = workshop.level();
                break;
            }
        }
        
        int duration = talentLevel == 2 ? (10 + (2 * workshopLevel)) : (5 + workshopLevel);
        int radius = talentLevel == 2 ? 3 : 2;
        
        String baseDesc = Messages.get(this, "desc");
        String currentEffect = "\n\n" + Messages.get(this, "current_effect", radius, duration);
        
        return baseDesc + currentEffect + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }
    
    // Helper method to get all workshops the hero has
    private PocketWorkshop[] getWorkshopsForHero(Hero hero) {
        if (hero == null) return new PocketWorkshop[0];
        
        java.util.List<PocketWorkshop> workshops = new java.util.ArrayList<>();
        
        // Check equipped artifact
        if (hero.belongings.artifact instanceof PocketWorkshop) {
            workshops.add((PocketWorkshop) hero.belongings.artifact);
        }
        
        // Check inventory
        for (com.shatteredpixel.shatteredpixeldungeon.items.Item item : hero.belongings.backpack) {
            if (item instanceof PocketWorkshop) {
                workshops.add((PocketWorkshop) item);
            }
        }
        
        return workshops.toArray(new PocketWorkshop[0]);
    }
    
    // Buff that provides vision through walls
    public static class WallScanner extends FlavourBuff {
        public int radius = 2;
        
        @Override
        public int icon() {
            return BuffIndicator.MIND_VISION;
        }
        
        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 0.8f, 1.0f); // Blue tint
        }
        
        @Override
        public String toString() {
            return Messages.get(this, "name");
        }
        
        @Override
        public String desc() {
            return Messages.get(this, "desc", radius, dispTurns());
        }
        
        @Override
        public void detach() {
            super.detach();
            Dungeon.observe();
            GameScene.updateFog();
        }
    }
}