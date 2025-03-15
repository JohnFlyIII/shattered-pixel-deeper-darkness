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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Class that handles initialization and loading of hero talents
 */
public class TalentInitializer {

    public static void initClassTalents(Hero hero) {
        initClassTalents(hero.heroClass, hero.talents, hero.metamorphedTalents);
    }

    public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        initClassTalents(cls, talents, new LinkedHashMap<>());
    }

    public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements) {
        while (talents.size() < Talent.MAX_TALENT_TIERS) {
            talents.add(new LinkedHashMap<>());
        }

        ArrayList<Talent> tierTalents = new ArrayList<>();

        //tier 1
        switch (cls) {
            case WARRIOR:
            default:
                Collections.addAll(tierTalents, Talent.HEARTY_MEAL, Talent.VETERANS_INTUITION, Talent.PROVOKED_ANGER, Talent.IRON_WILL);
                break;
            case MAGE:
                Collections.addAll(tierTalents, Talent.EMPOWERING_MEAL, Talent.SCHOLARS_INTUITION, Talent.LINGERING_MAGIC, Talent.BACKUP_BARRIER);
                break;
            case ROGUE:
                Collections.addAll(tierTalents, Talent.CACHED_RATIONS, Talent.THIEFS_INTUITION, Talent.SUCKER_PUNCH, Talent.PROTECTIVE_SHADOWS);
                break;
            case HUNTRESS:
                Collections.addAll(tierTalents, Talent.NATURES_BOUNTY, Talent.SURVIVALISTS_INTUITION, Talent.FOLLOWUP_STRIKE, Talent.NATURES_AID);
                break;
            case DUELIST:
                Collections.addAll(tierTalents, Talent.STRENGTHENING_MEAL, Talent.ADVENTURERS_INTUITION, Talent.PATIENT_STRIKE, Talent.AGGRESSIVE_BARRIER);
                break;
            case CLERIC:
                Collections.addAll(tierTalents, Talent.SATIATED_SPELLS, Talent.HOLY_INTUITION, Talent.SEARING_LIGHT, Talent.SHIELD_OF_LIGHT);
                break;
            case ARTIFICER:
                Collections.addAll(tierTalents, Talent.FULL_TANK, Talent.VETERANS_INTUITION, Talent.PROVOKED_ANGER, Talent.IRON_WILL);
                break;
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey(talent)) {
                talent = replacements.get(talent);
            }
            talents.get(0).put(talent, 0);
        }
        tierTalents.clear();

        //tier 2
        switch (cls) {
            case WARRIOR:
            default:
                Collections.addAll(tierTalents, Talent.IRON_STOMACH, Talent.LIQUID_WILLPOWER, Talent.RUNIC_TRANSFERENCE, Talent.LETHAL_MOMENTUM, Talent.IMPROVISED_PROJECTILES);
                break;
            case MAGE:
                Collections.addAll(tierTalents, Talent.ENERGIZING_MEAL, Talent.INSCRIBED_POWER, Talent.WAND_PRESERVATION, Talent.ARCANE_VISION, Talent.SHIELD_BATTERY);
                break;
            case ROGUE:
                Collections.addAll(tierTalents, Talent.MYSTICAL_MEAL, Talent.INSCRIBED_STEALTH, Talent.WIDE_SEARCH, Talent.SILENT_STEPS, Talent.ROGUES_FORESIGHT);
                break;
            case HUNTRESS:
                Collections.addAll(tierTalents, Talent.INVIGORATING_MEAL, Talent.LIQUID_NATURE, Talent.REJUVENATING_STEPS, Talent.HEIGHTENED_SENSES, Talent.DURABLE_PROJECTILES);
                break;
            case DUELIST:
                Collections.addAll(tierTalents, Talent.FOCUSED_MEAL, Talent.LIQUID_AGILITY, Talent.WEAPON_RECHARGING, Talent.LETHAL_HASTE, Talent.SWIFT_EQUIP);
                break;
            case CLERIC:
                Collections.addAll(tierTalents, Talent.ENLIGHTENING_MEAL, Talent.RECALL_INSCRIPTION, Talent.SUNRAY, Talent.DIVINE_SENSE, Talent.BLESS);
                break;
            case ARTIFICER:
                Collections.addAll(tierTalents, Talent.ENERGIZING_MEAL, Talent.REJUVENATING_STEPS, Talent.HEIGHTENED_SENSES, Talent.MYSTICAL_MEAL, Talent.SILENT_STEPS);
                break;
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey(talent)) {
                talent = replacements.get(talent);
            }
            talents.get(1).put(talent, 0);
        }
        tierTalents.clear();

        //tier 3
        switch (cls) {
            case WARRIOR:
            default:
                Collections.addAll(tierTalents, Talent.HOLD_FAST, Talent.STRONGMAN);
                break;
            case MAGE:
                Collections.addAll(tierTalents, Talent.DESPERATE_POWER, Talent.ALLY_WARP);
                break;
            case ROGUE:
                Collections.addAll(tierTalents, Talent.ENHANCED_RINGS, Talent.LIGHT_CLOAK);
                break;
            case HUNTRESS:
                Collections.addAll(tierTalents, Talent.POINT_BLANK, Talent.SEER_SHOT);
                break;
            case DUELIST:
                Collections.addAll(tierTalents, Talent.PRECISE_ASSAULT, Talent.DEADLY_FOLLOWUP);
                break;
            case CLERIC:
                Collections.addAll(tierTalents, Talent.CLEANSE, Talent.LIGHT_READING);
                break;
            case ARTIFICER:
                Collections.addAll(tierTalents, Talent.ENHANCED_RINGS, Talent.AETHERIC_EXPANSION);
                break;
        }
        for (Talent talent : tierTalents) {
            if (replacements.containsKey(talent)) {
                talent = replacements.get(talent);
            }
            talents.get(2).put(talent, 0);
        }
        tierTalents.clear();

        //tier4
        //TBD
    }

    public static void initSubclassTalents(Hero hero) {
        initSubclassTalents(hero.subClass, hero.talents);
    }

    public static void initSubclassTalents(HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        if (cls == HeroSubClass.NONE) return;

        while (talents.size() < Talent.MAX_TALENT_TIERS) {
            talents.add(new LinkedHashMap<>());
        }

        ArrayList<Talent> tierTalents = new ArrayList<>();

        //tier 3
        switch (cls) {
            case BERSERKER:
            default:
                Collections.addAll(tierTalents, Talent.ENDLESS_RAGE, Talent.DEATHLESS_FURY, Talent.ENRAGED_CATALYST);
                break;
            case GLADIATOR:
                Collections.addAll(tierTalents, Talent.CLEAVE, Talent.LETHAL_DEFENSE, Talent.ENHANCED_COMBO);
                break;
            case BATTLEMAGE:
                Collections.addAll(tierTalents, Talent.EMPOWERED_STRIKE, Talent.MYSTICAL_CHARGE, Talent.EXCESS_CHARGE);
                break;
            case WARLOCK:
                Collections.addAll(tierTalents, Talent.SOUL_EATER, Talent.SOUL_SIPHON, Talent.NECROMANCERS_MINIONS);
                break;
            case ASSASSIN:
                Collections.addAll(tierTalents, Talent.ENHANCED_LETHALITY, Talent.ASSASSINS_REACH, Talent.BOUNTY_HUNTER);
                break;
            case FREERUNNER:
                Collections.addAll(tierTalents, Talent.EVASIVE_ARMOR, Talent.PROJECTILE_MOMENTUM, Talent.SPEEDY_STEALTH);
                break;
            case SNIPER:
                Collections.addAll(tierTalents, Talent.FARSIGHT, Talent.SHARED_ENCHANTMENT, Talent.SHARED_UPGRADES);
                break;
            case WARDEN:
                Collections.addAll(tierTalents, Talent.DURABLE_TIPS, Talent.BARKSKIN, Talent.SHIELDING_DEW);
                break;
            case CHAMPION:
                Collections.addAll(tierTalents, Talent.VARIED_CHARGE, Talent.TWIN_UPGRADES, Talent.COMBINED_LETHALITY);
                break;
            case MONK:
                Collections.addAll(tierTalents, Talent.UNENCUMBERED_SPIRIT, Talent.MONASTIC_VIGOR, Talent.COMBINED_ENERGY);
                break;
            case PRIEST:
                Collections.addAll(tierTalents, Talent.HOLY_LANCE, Talent.HALLOWED_GROUND, Talent.MNEMONIC_PRAYER);
                break;
            case PALADIN:
                Collections.addAll(tierTalents, Talent.LAY_ON_HANDS, Talent.AURA_OF_PROTECTION, Talent.WALL_OF_LIGHT);
                break;
            case INVENTOR:
                Collections.addAll(tierTalents, Talent.VARIED_CHARGE, Talent.ENHANCED_RINGS, Talent.WAND_PRESERVATION);
                break;
            case MACHINIST:
                Collections.addAll(tierTalents, Talent.FARSIGHT, Talent.RUNIC_TRANSFERENCE, Talent.WIDE_SEARCH);
                break;
        }
        for (Talent talent : tierTalents) {
            talents.get(2).put(talent, 0);
        }
        tierTalents.clear();
    }

    public static void initArmorTalents(Hero hero) {
        initArmorTalents(hero.armorAbility, hero.talents);
    }

    public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        if (abil == null) return;

        while (talents.size() < Talent.MAX_TALENT_TIERS) {
            talents.add(new LinkedHashMap<>());
        }

        for (Talent t : abil.talents()) {
            talents.get(3).put(t, 0);
        }
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

    private static final String TALENT_TIER = "talents_tier_";
    private static final String REPLACEMENTS = "replacements";
    
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
        bundle.put(REPLACEMENTS, replacementsBundle);
    }

    public static void restoreTalentsFromBundle(Bundle bundle, Hero hero) {
        if (bundle.contains(REPLACEMENTS)) {
            Bundle replacements = bundle.getBundle(REPLACEMENTS);
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

        if (hero.heroClass != null) initClassTalents(hero);
        if (hero.subClass != null) initSubclassTalents(hero);
        if (hero.armorAbility != null) initArmorTalents(hero);

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