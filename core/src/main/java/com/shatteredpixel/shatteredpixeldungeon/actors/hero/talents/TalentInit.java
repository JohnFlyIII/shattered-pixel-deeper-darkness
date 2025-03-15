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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Class handling initialization of talents for heroes
 */
public class TalentInit {

    /**
     * Initialize talents for the hero
     */
    public static void initClassTalents(Hero hero) {
        initClassTalents(hero.heroClass, hero.talents, hero.metamorphedTalents);
    }

    /**
     * Initialize talents for a class
     */
    public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        initClassTalents(cls, talents, new LinkedHashMap<>());
    }

    /**
     * Initialize talents for a class with potential replacements (for metamorphosis)
     */
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

    /**
     * Initialize subclass talents for the hero
     */
    public static void initSubclassTalents(Hero hero) {
        initSubclassTalents(hero.subClass, hero.talents);
    }

    /**
     * Initialize talents for a subclass
     */
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

    /**
     * Initialize armor ability talents for the hero
     */
    public static void initArmorTalents(Hero hero) {
        initArmorTalents(hero.armorAbility, hero.talents);
    }

    /**
     * Initialize armor ability talents
     */
    public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
        if (abil == null) return;

        while (talents.size() < Talent.MAX_TALENT_TIERS) {
            talents.add(new LinkedHashMap<>());
        }

        for (Talent t : abil.talents()) {
            talents.get(3).put(t, 0);
        }
    }
}