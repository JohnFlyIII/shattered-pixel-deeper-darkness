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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Handles saving and loading talent information from bundles
 */
public class TalentSerialization {

    private static final String TALENT_TIER = "talents_tier_";

    /**
     * Store talents in a bundle
     */
    public static void storeTalentsInBundle(Bundle bundle, Hero hero) {
        for (int i = 0; i < Talent.MAX_TALENT_TIERS; i++) {
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle tierBundle = new Bundle();

            for (Talent talent : tier.keySet()) {
                if (tier.get(talent) > 0) {
                    tierBundle.put(talent.name(), tier.get(talent));
                }
                if (tierBundle.contains(talent.name())) {
                    tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
                }
            }
            bundle.put(TALENT_TIER + (i + 1), tierBundle);
        }

        Bundle replacementsBundle = new Bundle();
        for (Talent t : hero.metamorphedTalents.keySet()) {
            replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
        }
        bundle.put("replacements", replacementsBundle);
    }

    private static final HashSet<String> removedTalents = new HashSet<>();
    static {
        //v2.4.0
        removedTalents.add("TEST_SUBJECT");
        removedTalents.add("TESTED_HYPOTHESIS");
        //v2.2.0
        removedTalents.add("EMPOWERING_SCROLLS");
    }

    private static final HashMap<String, String> renamedTalents = new HashMap<>();
    static {
        //v2.4.0
        renamedTalents.put("SECONDARY_CHARGE", "VARIED_CHARGE");

        //v2.2.0
        renamedTalents.put("RESTORED_WILLPOWER", "LIQUID_WILLPOWER");
        renamedTalents.put("ENERGIZING_UPGRADE", "INSCRIBED_POWER");
        renamedTalents.put("MYSTICAL_UPGRADE", "INSCRIBED_STEALTH");
        renamedTalents.put("RESTORED_NATURE", "LIQUID_NATURE");
        renamedTalents.put("RESTORED_AGILITY", "LIQUID_AGILITY");
        //v2.1.0
        renamedTalents.put("LIGHTWEIGHT_CHARGE", "PRECISE_ASSAULT");
        //v2.0.0 BETA
        renamedTalents.put("LIGHTLY_ARMED", "UNENCUMBERED_SPIRIT");
        //v2.0.0
        renamedTalents.put("ARMSMASTERS_INTUITION", "VETERANS_INTUITION");
    }

    /**
     * Restore talents from a bundle
     */
    public static void restoreTalentsFromBundle(Bundle bundle, Hero hero) {
        if (bundle.contains("replacements")) {
            Bundle replacements = bundle.getBundle("replacements");
            for (String key : replacements.getKeys()) {
                String value = replacements.getString(key);
                if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
                if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
                if (!removedTalents.contains(key) && !removedTalents.contains(value)) {
                    try {
                        hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
                    } catch (Exception e) {
                        ShatteredPixelDungeon.reportException(e);
                    }
                }
            }
        }

        if (hero.heroClass != null) TalentInit.initClassTalents(hero);
        if (hero.subClass != null) TalentInit.initSubclassTalents(hero);
        if (hero.armorAbility != null) TalentInit.initArmorTalents(hero);

        for (int i = 0; i < Talent.MAX_TALENT_TIERS; i++) {
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle tierBundle = bundle.contains(TALENT_TIER + (i + 1)) ? bundle.getBundle(TALENT_TIER + (i + 1)) : null;

            if (tierBundle != null) {
                for (String tName : tierBundle.getKeys()) {
                    int points = tierBundle.getInt(tName);
                    if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
                    if (!removedTalents.contains(tName)) {
                        try {
                            Talent talent = Talent.valueOf(tName);
                            if (tier.containsKey(talent)) {
                                tier.put(talent, Math.min(points, talent.maxPoints()));
                            }
                        } catch (Exception e) {
                            ShatteredPixelDungeon.reportException(e);
                        }
                    }
                }
            }
        }
    }
}