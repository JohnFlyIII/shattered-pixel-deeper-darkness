/*
 * Pocket Workshop
 * An artifact for artificers that contains a folding workshop
 * Based on the Holy Tome mechanics from Shattered Pixel Dungeon
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AethericCloaking;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.TrapCloaker;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.artificerspells.ArtificerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.windows.artificer.WndArtificerSpells;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class PocketWorkshop extends Artifact {

    // Base stats
    private static final int BASE_MAX_CHARGES = 10;  // Maximum possible charges without talents
    private static final int CHARGES_PER_LEVEL = 1;  // How many charges each level adds
    private static final int STARTING_CHARGES = 3;   // Charges at level 0

    // Transient properties that are recalculated
    private int baseCharges;       // Charges from artifact level (STARTING_CHARGES + level*CHARGES_PER_LEVEL)
    private int bonusCharges;      // Extra charges from talents and other effects

    {
        image = ItemSpriteSheet.ARTIFACT_WORKSHOP;

        exp = 0;
        levelCap = 10;

        // Initialize with proper values
        updateChargeValues();

        defaultAction = AC_CRAFT;

        unique = true;
        bones = false;
    }

    private int spareParts = 0;

    public int getSpareParts() {
        return spareParts;
    }

    public int addSpareParts(int amount) {
        spareParts += amount;
        updateQuickslot();
        return spareParts;
    }

    public int spendSpareParts(int amount) {
        spareParts = Math.max(0, spareParts - amount);
        updateQuickslot();
        return spareParts;
    }

    // Updates the charge-related values based on current level and talents
    private void updateChargeValues() {
        // Calculate base charges from artifact level
        baseCharges = STARTING_CHARGES + level() * CHARGES_PER_LEVEL;

        // Calculate bonus charges from talents
        bonusCharges = calculateBonusCharges();

        // Update the actual charge cap
        chargeCap = Math.min(baseCharges + bonusCharges, BASE_MAX_CHARGES + bonusCharges);

        // Make sure current charge doesn't exceed the cap
        charge = Math.min(charge, chargeCap);
    }

    // Calculate extra charges from talents and effects
    private int calculateBonusCharges() {
        int bonus = 0;

        // Add charges from Aetheric Capacitor talent
        if (Dungeon.hero != null && Dungeon.hero.belongings != null) {
            bonus += Dungeon.hero.pointsInTalent(Talent.AETHERIC_CAPACITOR);
        }

        // Could add other sources of bonus charges here

        return bonus;
    }

    // Public getter for the total maximum charge capacity
    public int getMaxCharges() {
        return chargeCap;
    }

    // Public getter for the base charges (from level only)
    public int getBaseCharges() {
        return baseCharges;
    }

    // Public getter for bonus charges (from talents)
    public int getBonusCharges() {
        return bonusCharges;
    }

    public static final String AC_CRAFT = "CRAFT";
    public static final String AC_CLOAK = "CLOAK";

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if ((isEquipped(hero) || hero.hasTalent(Talent.AETHERIC_EXPANSION))
                && !cursed
                && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_CRAFT);

       }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals(AC_CRAFT)) {
            if (!isEquipped(hero) && !hero.hasTalent(Talent.AETHERIC_EXPANSION)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            } else if (cursed) {
                GLog.i(Messages.get(this, "cursed"));
            } else {
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                GameScene.show(new WndArtificerSpells(this, hero, false));
            }
        } else if (action.equals(AC_CLOAK)) {
            if (!isEquipped(hero) && !hero.hasTalent(Talent.AETHERIC_EXPANSION)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            } else if (cursed) {
                GLog.i(Messages.get(this, "cursed"));
            } else {
                // Use the TrapCloaker spell if available
                ArtificerSpell cloaker = TrapCloaker.INSTANCE;
                if (cloaker != null && cloaker.canCast(hero) && canCast(hero, cloaker)) {
                    Sample.INSTANCE.play(Assets.Sounds.MELD);
                    cloaker.onCast(this, hero);
                }
            }
        }
    }

    // Used to ensure workshop has variable targeting logic for whatever ability is being used
    public ArtificerSpell targetingSpell = null;

    @Override
    public int targetingPos(Hero user, int dst) {
        if (targetingSpell == null || targetingSpell.targetingFlags() == -1) {
            return super.targetingPos(user, dst);
        } else {
            return new Ballistica(user.pos, dst, targetingSpell.targetingFlags()).collisionPos;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (collect && hero.hasTalent(Talent.AETHERIC_EXPANSION)) {
                activate(hero);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner instanceof Hero
                    && passiveBuff == null
                    && ((Hero) container.owner).hasTalent(Talent.AETHERIC_EXPANSION)) {
                activate((Hero) container.owner);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDetach() {
        if (passiveBuff != null) {
            passiveBuff.detach();
            passiveBuff = null;
        }
    }

    public boolean canCast(Hero hero, ArtificerSpell ability) {
        return (isEquipped(hero) || (hero.hasTalent(Talent.AETHERIC_EXPANSION) && hero.belongings.contains(this)))
                && hero.buff(MagicImmune.class) == null
                && charge >= ability.chargeUse(hero)
                && ability.canCast(hero);
    }

    public void spendCharge(float chargesSpent) {
        // Check for Efficient Crafting talent
        Hero hero = Dungeon.hero;
        if (hero != null && hero.hasTalent(Talent.EFFICIENT_CRAFTING)) {
            // 33% chance at level 1, 66% chance at level 2
            float conserveChance = 0.33f * Math.min(hero.pointsInTalent(Talent.EFFICIENT_CRAFTING), 2);
            if (Random.Float() < conserveChance) {
                // Efficient crafting triggered - conserve 1 charge
                GLog.p(Messages.get(this, "efficient_crafting_proc"));
                chargesSpent = Math.max(0, chargesSpent - 1);
            }
        }

        partialCharge -= chargesSpent;
        while (partialCharge < 0) {
            charge--;
            partialCharge++;
        }

        // Target hero level is 1 + 2*workshop level
        int lvlDiffFromTarget = Dungeon.hero.lvl - (1+level()*2);
        // Plus an extra one for each level after 6
        if (level() >= 7) {
            lvlDiffFromTarget -= level()-6;
        }

        float expGain;
        if (lvlDiffFromTarget >= 0) {
            expGain = chargesSpent * 10f * (float)Math.pow(1.1f, lvlDiffFromTarget);
        } else {
            expGain = chargesSpent * 10f * (float)Math.pow(0.75f, -lvlDiffFromTarget);
        }
        exp += (int)Math.round(expGain);

        if (exp >= (level() + 1) * 50 && level() < levelCap) {
            upgrade();
            Catalog.countUse(PocketWorkshop.class);
            exp -= level() * 50;
            GLog.p(Messages.get(this, "levelup"));
        }

        updateQuickslot();
    }

    public void directCharge(float amount) {
        if (charge < chargeCap) {
            partialCharge += amount;
            while (partialCharge >= 1f) {
                charge++;
                partialCharge--;
            }
            if (charge >= chargeCap) {
                partialCharge = 0;
                charge = chargeCap;
            }
            updateQuickslot();
        }
    }

    // Called when the Aetheric Capacitor talent is upgraded
    public void recalculateChargeCap() {
        int oldCap = chargeCap;

        // Update all charge-related values
        updateChargeValues();

        int newCap = chargeCap;

        // Log detailed debug info
        GLog.d("Workshop recalc: old=%d, new=%d, base=%d, bonus=%d",
                oldCap, newCap, baseCharges, bonusCharges);

        // Show feedback if capacity increased
        if (newCap > oldCap) {
            int gained = newCap - oldCap;
            charge += gained;  // Add the extra charges
            GLog.p(Messages.get(this, "aetheric_capacitor_proc"), gained);

            // Show visual feedback
            if (Dungeon.hero != null && Dungeon.hero.sprite != null) {
                Dungeon.hero.sprite.showStatus(0x44CCFF, "+%d charges", gained);
            }
        }

        updateQuickslot();
    }

    @Override
    public Item upgrade() {
        // Store old level
        int oldLevel = level();

        // Call parent upgrade which increments level
        super.upgrade();

        // Update charge values based on new level
        updateChargeValues();

        return this;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new WorkshopRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (cursed || target.buff(MagicImmune.class) != null) return;

        if (charge < chargeCap) {
            if (!isEquipped(target)) {
                int talentLevel = Math.max(0, target.pointsInTalent(Talent.AETHERIC_EXPANSION));
                amount *= 0.75f * talentLevel / 3f;
            }
            partialCharge += 0.25f*amount;
            while (partialCharge >= 1f) {
                charge++;
                partialCharge--;
            }
            if (charge >= chargeCap) {
                partialCharge = 0;
                charge = chargeCap;
            }
            updateQuickslot();
        }
    }

    private ArtificerSpell quickAbility = null;

    public void setQuickSpell(ArtificerSpell ability) {
        if (quickAbility == ability) {
            quickAbility = null; // Re-assigning the same ability clears the quick ability
            if (passiveBuff != null) {
                ActionIndicator.clearAction((ActionIndicator.Action) passiveBuff);
            }
        } else {
            quickAbility = ability;
            if (passiveBuff != null) {
                ActionIndicator.setAction((ActionIndicator.Action) passiveBuff);
            }
        }
    }

    private static final String QUICK_CLS = "quick_cls";
    private static final String SPARE_PARTS = "spare_parts";
    private static final String BASE_CHARGES = "base_charges";
    private static final String BONUS_CHARGES = "bonus_charges";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (quickAbility != null) {
            bundle.put(QUICK_CLS, quickAbility.getClass());
        }
        bundle.put(SPARE_PARTS, spareParts);
        bundle.put(BASE_CHARGES, baseCharges);
        bundle.put(BONUS_CHARGES, bonusCharges);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(QUICK_CLS)) {
            Class<?> quickCls = bundle.getClass(QUICK_CLS);
            for (ArtificerSpell ability : ArtificerSpell.getAllSpells()) {
                if (ability.getClass() == quickCls) {
                    quickAbility = ability;
                }
            }
        }
        if (bundle.contains(SPARE_PARTS)) {
            spareParts = bundle.getInt(SPARE_PARTS);
        }

        // Restore saved charge values if available
        if (bundle.contains(BASE_CHARGES)) {
            baseCharges = bundle.getInt(BASE_CHARGES);
        } else {
            // Otherwise recalculate them
            baseCharges = STARTING_CHARGES + level() * CHARGES_PER_LEVEL;
        }

        if (bundle.contains(BONUS_CHARGES)) {
            bonusCharges = bundle.getInt(BONUS_CHARGES);
        } else {
            // Otherwise recalculate them
            bonusCharges = calculateBonusCharges();
        }

        // Make sure charge cap is consistent
        chargeCap = Math.min(baseCharges + bonusCharges, BASE_MAX_CHARGES + bonusCharges);
    }

    public class WorkshopRecharge extends ArtifactBuff implements ActionIndicator.Action {

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (quickAbility != null) ActionIndicator.setAction(this);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            super.detach();
            ActionIndicator.clearAction(this);
        }

        @Override
        public boolean act() {
            if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null) {
                if (Regeneration.regenOn()) {
                    float missing = (chargeCap - charge);
                    if (level() > 7) missing += 5*(level() - 7)/3f;
                    float turnsToCharge = (45 - missing);
                    turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(target);
                    float chargeToGain = (1f / turnsToCharge);
                    if (!isEquipped(Dungeon.hero) && Dungeon.hero != null) {
                        int talentLevel = Math.max(0, Dungeon.hero.pointsInTalent(Talent.AETHERIC_EXPANSION));
                        chargeToGain *= 0.75f * talentLevel / 3f;
                    }
                    partialCharge += chargeToGain;
                }

                while (partialCharge >= 1) {
                    charge++;
                    partialCharge -= 1;
                    if (charge == chargeCap) {
                        partialCharge = 0;
                    }
                }
            } else {
                partialCharge = 0;
            }

            updateQuickslot();

            spend(TICK);

            return true;
        }

        @Override
        public String actionName() {
            return quickAbility != null ? quickAbility.name() : "";
        }

        @Override
        public int actionIcon() {
            return quickAbility != null ? quickAbility.icon() + HeroIcon.SPELL_ACTION_OFFSET : 0;
        }

        @Override
        public int indicatorColor() {
            return 0x8B4513; // Brown color for workshop theme
        }

        @Override
        public void doAction() {
            if (cursed) {
                GLog.w(Messages.get(PocketWorkshop.this, "cursed"));
                return;
            }

            if (quickAbility == null) {
                return;
            }

            if (!canCast(Dungeon.hero, quickAbility)) {
                GLog.w(Messages.get(PocketWorkshop.this, "no_ability"));
                return;
            }

            if (QuickSlotButton.targetingSlot != -1 &&
                    Dungeon.quickslot.getItem(QuickSlotButton.targetingSlot) == PocketWorkshop.this) {
                targetingSpell = quickAbility;
                int cell = QuickSlotButton.autoAim(QuickSlotButton.lastTarget, PocketWorkshop.this);

                if (cell != -1) {
                    GameScene.handleCell(cell);
                } else {
                    // Couldn't auto-aim, just target the position and hope for the best.
                    if (QuickSlotButton.lastTarget != null) {
                        GameScene.handleCell(QuickSlotButton.lastTarget.pos);
                    }
                }
            } else {
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK); // Casting sound
                quickAbility.onCast(PocketWorkshop.this, Dungeon.hero);

                if (quickAbility.targetingFlags() != -1 && Dungeon.quickslot.contains(PocketWorkshop.this)) {
                    targetingSpell = quickAbility;
                    QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(PocketWorkshop.this));
                }
            }
        }
    }
}