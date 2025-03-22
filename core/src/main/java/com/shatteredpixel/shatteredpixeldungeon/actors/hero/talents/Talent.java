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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * The main Talent enum holding all talent definitions.
 * For implementation details, see the helper classes:
 * - TalentBuffs: For all buff classes related to talents
 * - TalentInit: For talent initialization code
 * - TalentSerialization: For saving/loading talent data
 * - TalentEffects: For game effect implementations
 */
public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), PROVOKED_ANGER(2), IRON_WILL(3),
	//Warrior T2
	IRON_STOMACH(4), LIQUID_WILLPOWER(5), RUNIC_TRANSFERENCE(6), LETHAL_MOMENTUM(7), IMPROVISED_PROJECTILES(8),
	//Warrior T3
	HOLD_FAST(9, 3), STRONGMAN(10, 3),
	//Berserker T3
	ENDLESS_RAGE(11, 3), DEATHLESS_FURY(12, 3), ENRAGED_CATALYST(13, 3),
	//Gladiator T3
	CLEAVE(14, 3), LETHAL_DEFENSE(15, 3), ENHANCED_COMBO(16, 3),
	//Heroic Leap T4
	BODY_SLAM(17, 4), IMPACT_WAVE(18, 4), DOUBLE_JUMP(19, 4),
	//Shockwave T4
	EXPANDING_WAVE(20, 4), STRIKING_WAVE(21, 4), SHOCK_FORCE(22, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(23, 4), SHRUG_IT_OFF(24, 4), EVEN_THE_ODDS(25, 4),

	//Mage T1
	EMPOWERING_MEAL(32), SCHOLARS_INTUITION(33), LINGERING_MAGIC(34), BACKUP_BARRIER(35),
	//Mage T2
	ENERGIZING_MEAL(36), INSCRIBED_POWER(37), WAND_PRESERVATION(38), ARCANE_VISION(39), SHIELD_BATTERY(40),
	//Mage T3
	DESPERATE_POWER(41, 3), ALLY_WARP(42, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(43, 3), MYSTICAL_CHARGE(44, 3), EXCESS_CHARGE(45, 3),
	//Warlock T3
	SOUL_EATER(46, 3), SOUL_SIPHON(47, 3), NECROMANCERS_MINIONS(48, 3),
	//Elemental Blast T4
	BLAST_RADIUS(49, 4), ELEMENTAL_POWER(50, 4), REACTIVE_BARRIER(51, 4),
	//Wild Magic T4
	WILD_POWER(52, 4), FIRE_EVERYTHING(53, 4), CONSERVED_MAGIC(54, 4),
	//Warp Beacon T4
	TELEFRAG(55, 4), REMOTE_BEACON(56, 4), LONGRANGE_WARP(57, 4),

	//Rogue T1
	CACHED_RATIONS(64), THIEFS_INTUITION(65), SUCKER_PUNCH(66), PROTECTIVE_SHADOWS(67),
	//Rogue T2
	MYSTICAL_MEAL(68), INSCRIBED_STEALTH(69), WIDE_SEARCH(70), SILENT_STEPS(71), ROGUES_FORESIGHT(72),
	//Rogue T3
	ENHANCED_RINGS(73, 3), LIGHT_CLOAK(74, 3),
	//Assassin T3
	ENHANCED_LETHALITY(75, 3), ASSASSINS_REACH(76, 3), BOUNTY_HUNTER(77, 3),
	//Freerunner T3
	EVASIVE_ARMOR(78, 3), PROJECTILE_MOMENTUM(79, 3), SPEEDY_STEALTH(80, 3),
	//Smoke Bomb T4
	HASTY_RETREAT(81, 4), BODY_REPLACEMENT(82, 4), SHADOW_STEP(83, 4),
	//Death Mark T4
	FEAR_THE_REAPER(84, 4), DEATHLY_DURABILITY(85, 4), DOUBLE_MARK(86, 4),
	//Shadow Clone T4
	SHADOW_BLADE(87, 4), CLONED_ARMOR(88, 4), PERFECT_COPY(89, 4),

	//Huntress T1
	NATURES_BOUNTY(96), SURVIVALISTS_INTUITION(97), FOLLOWUP_STRIKE(98), NATURES_AID(99),
	//Huntress T2
	INVIGORATING_MEAL(100), LIQUID_NATURE(101), REJUVENATING_STEPS(102), HEIGHTENED_SENSES(103), DURABLE_PROJECTILES(104),
	//Huntress T3
	POINT_BLANK(105, 3), SEER_SHOT(106, 3),
	//Sniper T3
	FARSIGHT(107, 3), SHARED_ENCHANTMENT(108, 3), SHARED_UPGRADES(109, 3),
	//Warden T3
	DURABLE_TIPS(110, 3), BARKSKIN(111, 3), SHIELDING_DEW(112, 3),
	//Spectral Blades T4
	FAN_OF_BLADES(113, 4), PROJECTING_BLADES(114, 4), SPIRIT_BLADES(115, 4),
	//Natures Power T4
	GROWING_POWER(116, 4), NATURES_WRATH(117, 4), WILD_MOMENTUM(118, 4),
	//Spirit Hawk T4
	EAGLE_EYE(119, 4), GO_FOR_THE_EYES(120, 4), SWIFT_SPIRIT(121, 4),

	//Duelist T1
	STRENGTHENING_MEAL(128), ADVENTURERS_INTUITION(129), PATIENT_STRIKE(130), AGGRESSIVE_BARRIER(131),
	//Duelist T2
	FOCUSED_MEAL(132), LIQUID_AGILITY(133), WEAPON_RECHARGING(134), LETHAL_HASTE(135), SWIFT_EQUIP(136),
	//Duelist T3
	PRECISE_ASSAULT(137, 3), DEADLY_FOLLOWUP(138, 3),
	//Champion T3
	VARIED_CHARGE(139, 3), TWIN_UPGRADES(140, 3), COMBINED_LETHALITY(141, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(142, 3), MONASTIC_VIGOR(143, 3), COMBINED_ENERGY(144, 3),
	//Challenge T4
	CLOSE_THE_GAP(145, 4), INVIGORATING_VICTORY(146, 4), ELIMINATION_MATCH(147, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(148, 4), STRIKING_FORCE(149, 4), DIRECTED_POWER(150, 4),
	//Feint T4
	FEIGNED_RETREAT(151, 4), EXPOSE_WEAKNESS(152, 4), COUNTER_ABILITY(153, 4),

	//Cleric T1
	SATIATED_SPELLS(160), HOLY_INTUITION(161), SEARING_LIGHT(162), SHIELD_OF_LIGHT(163),
	//Cleric T2
	ENLIGHTENING_MEAL(164), RECALL_INSCRIPTION(165), SUNRAY(166), DIVINE_SENSE(167), BLESS(168),
	//Cleric T3
	CLEANSE(169, 3), LIGHT_READING(170, 3),
	//Priest T3
	HOLY_LANCE(171, 3), HALLOWED_GROUND(172, 3), MNEMONIC_PRAYER(173, 3),
	//Paladin T3
	LAY_ON_HANDS(174, 3), AURA_OF_PROTECTION(175, 3), WALL_OF_LIGHT(176, 3),
	//Ascended Form T4
	DIVINE_INTERVENTION(177, 4), JUDGEMENT(178, 4), FLASH(179, 4),
	//Trinity T4
	BODY_FORM(180, 4), MIND_FORM(181, 4), SPIRIT_FORM(182, 4),
	//Power of Many T4
	BEAMING_RAY(183, 4), LIFE_LINK(184, 4), STASIS(185, 4),

	//universal T4
	HEROIC_ENERGY(26, 4), //See icon() and title() for special logic for this one

	//Ratmogrify T4
	RATSISTANCE(215, 4), RATLOMACY(216, 4), RATFORCEMENTS(217, 4),

	//Artificer T1 (224, 225, 226, 227)
	FULL_TANK(224), EFFICIENT_CRAFTING(225), AETHERIC_CAPACITOR(226), AETHERIC_CLOAKING(227),

	//Artificer T2 (228, 229, 232, 233)
	CLOCKWORK_RECLAMATION(228), INFUSE_ESSENCE(229), MECHANICAL_ASSISTANT(232), AETHERIC_SCANNER(233),

	//Artificer T3 (230, 231)
	AETHERIC_EXPANSION(230, 3);

	//Artificer - Inventor (232, 233, 234)

	//Artificer - Machinist (235, 236, 237)



	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent(int icon) {
		this(icon, 2);
	}

	Talent(int icon, int maxPoints) {
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public int icon() {
		if (this == HEROIC_ENERGY) {
			if (Ratmogrify.useRatroicEnergy) {
				return 218;
			}
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
			switch (cls) {
				case WARRIOR:
				default:
					return 26;
				case MAGE:
					return 58;
				case ROGUE:
					return 90;
				case HUNTRESS:
					return 122;
				case DUELIST:
					return 154;
				case CLERIC:
					return 186;
			}
		} else {
			return icon;
		}
	}

	public int maxPoints() {
		return maxPoints;
	}

	public String title() {
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy) {
			return Messages.get(this, name() + ".rat_title");
		}
		return Messages.get(this, name() + ".title");
	}

	public final String desc() {
		return desc(false);
	}

	public String desc(boolean metamorphed) {
		if (metamorphed) {
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)) {
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	/**
	 * Called when a talent is upgraded
	 */
	public static void onTalentUpgraded(Hero hero, Talent talent) {
		TalentEffects.onTalentUpgraded(hero, talent);
	}

	/**
	 * Calculate item identification speed factor based on talents
	 */
	public static float itemIDSpeedFactor(Hero hero, Item item) {
		return TalentEffects.itemIDSpeedFactor(hero, item);
	}

	/**
	 * Called when food is eaten
	 */
	public static void onFoodEaten(Hero hero, float foodVal, Item foodSource) {
		TalentEffects.onFoodEaten(hero, foodVal, foodSource);
	}

	/**
	 * Called when a potion is used
	 */
	public static void onPotionUsed(Hero hero, int cell, float factor) {
		TalentEffects.onPotionUsed(hero, cell, factor);
	}

	/**
	 * Called when a scroll is used
	 */
	public static void onScrollUsed(Hero hero, int pos, float factor, Class<? extends Item> cls) {
		TalentEffects.onScrollUsed(hero, pos, factor, cls);
	}

	/**
	 * Called when a runestone is used
	 */
	public static void onRunestoneUsed(Hero hero, int pos, Class<? extends Item> cls) {
		TalentEffects.onRunestoneUsed(hero, pos, cls);
	}

	/**
	 * Called when an artifact is used
	 */
	public static void onArtifactUsed(Hero hero) {
		TalentEffects.onArtifactUsed(hero);
	}

	/**
	 * Called when an item is equipped
	 */
	public static void onItemEquipped(Hero hero, Item item) {
		TalentEffects.onItemEquipped(hero, item);
	}

	/**
	 * Called when an item is collected
	 */
	public static void onItemCollected(Hero hero, Item item) {
		TalentEffects.onItemCollected(hero, item);
	}

	/**
	 * Called when the hero attacks
	 */
	public static int onAttackProc(Hero hero, Char enemy, int dmg) {
		return TalentEffects.onAttackProc(hero, enemy, dmg);
	}

	/**
	 * Initialize talents for a hero
	 */
	public static void initClassTalents(Hero hero) {
		TalentInit.initClassTalents(hero);
	}

	/**
	 * Initialize talents for a hero class
	 */
	public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents) {
		TalentInit.initClassTalents(cls, talents);
	}

	/**
	 * Initialize talents with replacements (for metamorphosis)
	 */
	public static void initClassTalents(HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements) {
		TalentInit.initClassTalents(cls, talents, replacements);
	}

	/**
	 * Initialize subclass talents for a hero
	 */
	public static void initSubclassTalents(Hero hero) {
		TalentInit.initSubclassTalents(hero);
	}

	/**
	 * Initialize armor ability talents for a hero
	 */
	public static void initArmorTalents(Hero hero) {
		TalentInit.initArmorTalents(hero);
	}

	/**
	 * Save talent information to a bundle
	 */
	public static void storeTalentsInBundle(com.watabou.utils.Bundle bundle, Hero hero) {
		TalentSerialization.storeTalentsInBundle(bundle, hero);
	}

	/**
	 * Restore talent information from a bundle
	 */
	public static void restoreTalentsFromBundle(com.watabou.utils.Bundle bundle, Hero hero) {
		TalentSerialization.restoreTalentsFromBundle(bundle, hero);
	}
	public static final int MAX_TALENT_TIERS = 4;
}