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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FullTank;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallInscription;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

/**
 * Handles various talent effects and interactions with game mechanics
 */
public class TalentEffects {

    /**
     * Called when a talent is upgraded to handle special effects
     */
    public static void onTalentUpgraded(Hero hero, Talent talent) {
        //for metamorphosis
        if (talent == Talent.IRON_WILL && hero.heroClass != HeroClass.WARRIOR) {
            Buff.affect(hero, BrokenSeal.WarriorShield.class);
        }

        if (talent == Talent.VETERANS_INTUITION && hero.pointsInTalent(Talent.VETERANS_INTUITION) == 2) {
            if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.armor.identify();
            }
        }
        if (talent == Talent.THIEFS_INTUITION && hero.pointsInTalent(Talent.THIEFS_INTUITION) == 2) {
            if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.ring.identify();
            }
            if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.misc.identify();
            }
            for (Item item : Dungeon.hero.belongings) {
                if (item instanceof Ring) {
                    ((Ring) item).setKnown();
                }
            }
        }
        if (talent == Talent.THIEFS_INTUITION && hero.pointsInTalent(Talent.THIEFS_INTUITION) == 1) {
            if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
            if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
        }
        if (talent == Talent.ADVENTURERS_INTUITION && hero.pointsInTalent(Talent.ADVENTURERS_INTUITION) == 2) {
            if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()) {
                hero.belongings.weapon().identify();
            }
        }

        if (talent == Talent.PROTECTIVE_SHADOWS && hero.invisible > 0) {
            Buff.affect(hero, TalentBuffs.ProtectiveShadowsTracker.class);
        }

        if (talent == Talent.LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE) {
            for (Item item : Dungeon.hero.belongings.backpack) {
                if (item instanceof CloakOfShadows) {
                    if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
                        ((CloakOfShadows) item).activate(Dungeon.hero);
                    }
                }
            }
        }

        if (talent == Talent.HEIGHTENED_SENSES || talent == Talent.FARSIGHT || talent == Talent.DIVINE_SENSE) {
            Dungeon.observe();
        }

        if (talent == Talent.TWIN_UPGRADES || talent == Talent.DESPERATE_POWER
                || talent == Talent.STRONGMAN || talent == Talent.DURABLE_PROJECTILES) {
            Item.updateQuickslot();
        }

        if (talent == Talent.UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3) {
            Item toGive = new ClothArmor().identify();
            if (!toGive.collect()) {
                Dungeon.level.drop(toGive, hero.pos).sprite.drop();
            }
            toGive = new Gloves().identify();
            if (!toGive.collect()) {
                Dungeon.level.drop(toGive, hero.pos).sprite.drop();
            }
        }

        if (talent == Talent.LIGHT_READING && hero.heroClass == HeroClass.CLERIC) {
            for (Item item : Dungeon.hero.belongings.backpack) {
                if (item instanceof HolyTome) {
                    if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
                        ((HolyTome) item).activate(Dungeon.hero);
                    }
                }
            }
        }

        if (talent == Talent.AETHERIC_EXPANSION && hero.heroClass == HeroClass.ARTIFICER) {
            for (Item item : Dungeon.hero.belongings.backpack) {
                if (item instanceof PocketWorkshop) {
                    if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
                        ((PocketWorkshop) item).activate(Dungeon.hero);
                    }
                }
            }
        }

        // Handle Aetheric Capacitor talent upgrade
        if (talent == Talent.AETHERIC_CAPACITOR && hero.heroClass == HeroClass.ARTIFICER) {
            // Check equipped artifact
            if (hero.belongings.artifact instanceof PocketWorkshop) {
                ((PocketWorkshop) hero.belongings.artifact).recalculateChargeCap();
            }

            // Check backpack
            for (Item item : hero.belongings.backpack) {
                if (item instanceof PocketWorkshop) {
                    ((PocketWorkshop) item).recalculateChargeCap();
                }
            }
        }

        //if we happen to have spirit form applied with a ring of might
        if (talent == Talent.SPIRIT_FORM) {
            Dungeon.hero.updateHT(false);
        }
    }

    /**
     * Calculate the item identification speed factor
     */
    public static float itemIDSpeedFactor(Hero hero, Item item) {
        // 1.75x/2.5x speed with Huntress talent
        float factor = 1f + 0.75f * hero.pointsInTalent(Talent.SURVIVALISTS_INTUITION);

        // Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
        if (item instanceof MeleeWeapon) {
            factor *= 1f + 1.5f * hero.pointsInTalent(Talent.ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
            factor *= 1f + 0.75f * hero.pointsInTalent(Talent.VETERANS_INTUITION);
        }
        // Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
        if (item instanceof Armor) {
            factor *= 1f + 0.75f * hero.pointsInTalent(Talent.ADVENTURERS_INTUITION);
            factor *= 1f + hero.pointsInTalent(Talent.VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
        }
        // 3x/instant for Mage (see Wand.wandUsed())
        if (item instanceof com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand) {
            factor *= 1f + 2.0f * hero.pointsInTalent(Talent.SCHOLARS_INTUITION);
        }
        // 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
        if (item instanceof Ring) {
            factor *= 1f + hero.pointsInTalent(Talent.THIEFS_INTUITION);
        }
        return factor;
    }

    /**
     * Effects that trigger when food is eaten
     */
    public static void onFoodEaten(Hero hero, float foodVal, Item foodSource) {
        if (hero.hasTalent(Talent.HEARTY_MEAL)) {
            //3/5 HP healed, when hero is below 30% health
            if (hero.HP / (float) hero.HT <= 0.3f) {
                int healing = 1 + 2 * hero.pointsInTalent(Talent.HEARTY_MEAL);
                hero.HP = Math.min(hero.HP + healing, hero.HT);
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
            }
        }
        if (hero.hasTalent(Talent.FULL_TANK)) {
            // Add charges for magic or equipment effects
            int charges = hero.pointsInTalent(Talent.FULL_TANK);
            if (charges > 0) {
                Buff.affect(hero, FullTank.class).add(charges);
                GLog.p(Messages.get(Talent.class, "full_tank_proc", charges));
            }
        }
        if (hero.hasTalent(Talent.IRON_STOMACH)) {
            if (hero.cooldown() > 0) {
                Buff.affect(hero, TalentBuffs.WarriorFoodImmunity.class, hero.cooldown());
            }
        }
        if (hero.hasTalent(Talent.EMPOWERING_MEAL)) {
            //2/3 bonus wand damage for next 3 zaps
            Buff.affect(hero, WandEmpower.class).set(1 + hero.pointsInTalent(Talent.EMPOWERING_MEAL), 3);
            ScrollOfRecharging.charge(hero);
        }
        if (hero.hasTalent(Talent.ENERGIZING_MEAL)) {
            //5/8 turns of recharging
            Buff.prolong(hero, Recharging.class, 2 + 3 * (hero.pointsInTalent(Talent.ENERGIZING_MEAL)));
            ScrollOfRecharging.charge(hero);
            SpellSprite.show(hero, SpellSprite.CHARGE);
        }
        if (hero.hasTalent(Talent.MYSTICAL_MEAL)) {
            //3/5 turns of recharging
            ArtifactRecharge buff = Buff.affect(hero, ArtifactRecharge.class);
            if (buff.left() < 1 + 2 * (hero.pointsInTalent(Talent.MYSTICAL_MEAL))) {
                Buff.affect(hero, ArtifactRecharge.class).set(1 + 2 * (hero.pointsInTalent(Talent.MYSTICAL_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
            }
            ScrollOfRecharging.charge(hero);
            SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
        }
        if (hero.hasTalent(Talent.INVIGORATING_MEAL)) {
            //effectively 1/2 turns of haste
            Buff.prolong(hero, Haste.class, 0.67f + hero.pointsInTalent(Talent.INVIGORATING_MEAL));
        }
        if (hero.hasTalent(Talent.STRENGTHENING_MEAL)) {
            //3 bonus physical damage for next 2/3 attacks
            Buff.affect(hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(Talent.STRENGTHENING_MEAL));
        }
        if (hero.hasTalent(Talent.FOCUSED_MEAL)) {
            if (hero.heroClass == HeroClass.DUELIST) {
                //0.67/1 charge for the duelist
                Buff.affect(hero, MeleeWeapon.Charger.class).gainCharge((hero.pointsInTalent(Talent.FOCUSED_MEAL) + 1) / 3f);
                ScrollOfRecharging.charge(hero);
            } else {
                // lvl/3 / lvl/2 bonus dmg on next hit for other classes
                Buff.affect(hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(Talent.FOCUSED_MEAL))), 1);
            }
        }
        if (hero.hasTalent(Talent.SATIATED_SPELLS)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.affect(hero, TalentBuffs.SatiatedSpellsTracker.class);
            } else {
                //3/5 shielding, delayed up to 10 turns
                int amount = 1 + 2 * hero.pointsInTalent(Talent.SATIATED_SPELLS);
                Barrier b = Buff.affect(hero, Barrier.class);
                if (b.shielding() <= amount) {
                    b.setShield(amount);
                    b.delay(Math.max(10 - b.cooldown(), 0));
                }
            }
        }
        if (hero.hasTalent(Talent.ENLIGHTENING_MEAL)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                HolyTome tome = hero.belongings.getItem(HolyTome.class);
                if (tome != null) {
                    tome.directCharge(0.5f * (1 + hero.pointsInTalent(Talent.ENLIGHTENING_MEAL)));
                    ScrollOfRecharging.charge(hero);
                }
            } else {
                //2/3 turns of recharging
                ArtifactRecharge buff = Buff.affect(hero, ArtifactRecharge.class);
                if (buff.left() < 1 + (hero.pointsInTalent(Talent.ENLIGHTENING_MEAL))) {
                    Buff.affect(hero, ArtifactRecharge.class).set(1 + (hero.pointsInTalent(Talent.ENLIGHTENING_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
                }
                Buff.prolong(hero, Recharging.class, 1 + (hero.pointsInTalent(Talent.ENLIGHTENING_MEAL)));
                ScrollOfRecharging.charge(hero);
                SpellSprite.show(hero, SpellSprite.CHARGE);
            }
        }
    }

    /**
     * Effects that trigger when potions are used
     */
    public static void onPotionUsed(Hero hero, int cell, float factor) {
        if (hero.hasTalent(Talent.LIQUID_WILLPOWER)) {
            if (hero.heroClass == HeroClass.WARRIOR) {
                BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
                if (shield != null) {
                    // 50/75% of total shield
                    int shieldToGive = Math.round(factor * shield.maxShield() * 0.25f * (1 + hero.pointsInTalent(Talent.LIQUID_WILLPOWER)));
                    hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
                    shield.supercharge(shieldToGive);
                }
            } else {
                // 5/7.5% of max HP
                int shieldToGive = Math.round(factor * hero.HT * (0.025f * (1 + hero.pointsInTalent(Talent.LIQUID_WILLPOWER))));
                hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
                Buff.affect(hero, Barrier.class).setShield(shieldToGive);
            }
        }
        if (hero.hasTalent(Talent.LIQUID_NATURE)) {
            ArrayList<Integer> grassCells = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS9) {
                grassCells.add(cell + i);
            }
            Random.shuffle(grassCells);
            for (int grassCell : grassCells) {
                Char ch = Actor.findChar(grassCell);
                if (ch != null && ch.alignment == Char.Alignment.ENEMY) {
                    //1/2 turns of roots
                    Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(Talent.LIQUID_NATURE));
                }
                if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
                        Dungeon.level.map[grassCell] == Terrain.EMBERS ||
                        Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO) {
                    Level.set(grassCell, Terrain.GRASS);
                    GameScene.updateMap(grassCell);
                }
                CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            }
            // 4/6 cells total
            int totalGrassCells = (int) (factor * (2 + 2 * hero.pointsInTalent(Talent.LIQUID_NATURE)));
            while (grassCells.size() > totalGrassCells) {
                grassCells.remove(0);
            }
            for (int grassCell : grassCells) {
                int t = Dungeon.level.map[grassCell];
                if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
                        || t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
                        && Dungeon.level.plants.get(grassCell) == null) {
                    Level.set(grassCell, Terrain.HIGH_GRASS);
                    GameScene.updateMap(grassCell);
                }
            }
            Dungeon.observe();
        }
        if (hero.hasTalent(Talent.LIQUID_AGILITY)) {
            Buff.prolong(hero, TalentBuffs.LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor - 1));
            if (factor >= 0.5f) {
                Buff.prolong(hero, TalentBuffs.LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
            }
        }
    }

    /**
     * Effects that trigger when scrolls are used
     */
    public static void onScrollUsed(Hero hero, int pos, float factor, Class<?extends Item> cls) {
        if (hero.hasTalent(Talent.INSCRIBED_POWER)) {
            // 2/3 empowered wand zaps
            Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(Talent.INSCRIBED_POWER))));
        }
        if (hero.hasTalent(Talent.INSCRIBED_STEALTH)) {
            // 3/5 turns of stealth
            Buff.affect(hero, Invisibility.class, factor * (1 + 2 * hero.pointsInTalent(Talent.INSCRIBED_STEALTH)));
            Sample.INSTANCE.play(Assets.Sounds.MELD);
        }
        if (hero.hasTalent(Talent.RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
            } else {
                // 10/15%
                if (Random.Int(20) < 1 + hero.pointsInTalent(Talent.RECALL_INSCRIPTION)) {
                    Reflection.newInstance(cls).collect();
                    GLog.p("refunded!");
                }
            }
        }
    }

    /**
     * Effects that trigger when runestones are used
     */
    public static void onRunestoneUsed(Hero hero, int pos, Class<?extends Item> cls) {
        if (hero.hasTalent(Talent.RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)) {
            if (hero.heroClass == HeroClass.CLERIC) {
                Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(Talent.RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
            } else {

                //don't trigger on 1st intuition use
                if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null) {
                    return;
                }
                // 10/15%
                if (Random.Int(20) < 1 + hero.pointsInTalent(Talent.RECALL_INSCRIPTION)) {
                    Reflection.newInstance(cls).collect();
                    GLog.p("refunded!");
                }
            }
        }
    }

    /**
     * Effects that trigger when artifacts are used
     */
    public static void onArtifactUsed(Hero hero) {
        if (hero.hasTalent(Talent.ENHANCED_RINGS)) {
            Buff.prolong(hero, EnhancedRings.class, 3f * hero.pointsInTalent(Talent.ENHANCED_RINGS));
        }

        if (Dungeon.hero.heroClass != HeroClass.CLERIC
                && Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)) {
            Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown() + 1);
        }

        // 10/20/30%
        if (Dungeon.hero.heroClass != HeroClass.CLERIC
                && Dungeon.hero.hasTalent(Talent.CLEANSE)
                && Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)) {
            boolean removed = false;
            for (Buff b : Dungeon.hero.buffs()) {
                if (b.type == Buff.buffType.NEGATIVE) {
                    b.detach();
                    removed = true;
                }
            }
            if (removed) new Flare(6, 32).color(0xFF4CD2, true).show(Dungeon.hero.sprite, 2f);
        }
    }

    /**
     * Effects that trigger when items are equipped
     */
    public static void onItemEquipped(Hero hero, Item item) {
        boolean identify = false;
        if (hero.pointsInTalent(Talent.VETERANS_INTUITION) == 2 && item instanceof Armor) {
            identify = true;
        }
        if (hero.hasTalent(Talent.THIEFS_INTUITION) && item instanceof Ring) {
            if (hero.pointsInTalent(Talent.THIEFS_INTUITION) == 2) {
                identify = true;
            }
            ((Ring) item).setKnown();
        }
        if (hero.pointsInTalent(Talent.ADVENTURERS_INTUITION) == 2 && item instanceof Weapon) {
            identify = true;
        }

        if (identify && !ShardOfOblivion.passiveIDDisabled()) {
            item.identify();
        }
    }

    /**
     * Effects that trigger when items are collected
     */
    public static void onItemCollected(Hero hero, Item item) {
        if (hero.pointsInTalent(Talent.THIEFS_INTUITION) == 2) {
            if (item instanceof Ring) ((Ring) item).setKnown();
        }
    }

    /**
     * Effects that happen when attacking
     */
    public static int onAttackProc(Hero hero, Char enemy, int dmg) {

        if (hero.hasTalent(Talent.PROVOKED_ANGER)
            && hero.buff(TalentBuffs.ProvokedAngerTracker.class) != null) {
            dmg += 1 + hero.pointsInTalent(Talent.PROVOKED_ANGER);
            hero.buff(TalentBuffs.ProvokedAngerTracker.class).detach();
        }

        if (hero.hasTalent(Talent.LINGERING_MAGIC)
                && hero.buff(TalentBuffs.LingeringMagicTracker.class) != null) {
            dmg += Random.IntRange(hero.pointsInTalent(Talent.LINGERING_MAGIC), 2);
            hero.buff(TalentBuffs.LingeringMagicTracker.class).detach();
        }

        if (hero.hasTalent(Talent.SUCKER_PUNCH)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.buff(TalentBuffs.SuckerPunchTracker.class) == null) {
            dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH), 2);
            Buff.affect(enemy, TalentBuffs.SuckerPunchTracker.class);
        }

        if (hero.hasTalent(Talent.FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
            if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                Buff.prolong(hero, TalentBuffs.FollowupStrikeTracker.class, 5f).object = enemy.id();
            } else if (hero.buff(TalentBuffs.FollowupStrikeTracker.class) != null
                    && hero.buff(TalentBuffs.FollowupStrikeTracker.class).object == enemy.id()) {
                dmg += 1 + hero.pointsInTalent(Talent.FOLLOWUP_STRIKE);
                hero.buff(TalentBuffs.FollowupStrikeTracker.class).detach();
            }
        }

        if (hero.buff(TalentBuffs.SpiritBladesTracker.class) != null
                && Random.Int(10) < 3 * hero.pointsInTalent(Talent.SPIRIT_BLADES)) {
            SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
            if (bow != null) dmg = bow.proc(hero, enemy, dmg);
            hero.buff(TalentBuffs.SpiritBladesTracker.class).detach();
        }

        if (hero.hasTalent(Talent.PATIENT_STRIKE)) {
            if (hero.buff(TalentBuffs.PatientStrikeTracker.class) != null
                    && !(hero.belongings.attackingWeapon() instanceof MissileWeapon)) {
                hero.buff(TalentBuffs.PatientStrikeTracker.class).detach();
                dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
            }
        }

        if (hero.hasTalent(Talent.DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
            if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
                if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
                    Buff.prolong(hero, TalentBuffs.DeadlyFollowupTracker.class, 5f).object = enemy.id();
                }
            } else if (hero.buff(TalentBuffs.DeadlyFollowupTracker.class) != null
                    && hero.buff(TalentBuffs.DeadlyFollowupTracker.class).object == enemy.id()) {
                dmg = Math.round(dmg * (1.0f + .1f * hero.pointsInTalent(Talent.DEADLY_FOLLOWUP)));
            }
        }

        return dmg;
    }
}