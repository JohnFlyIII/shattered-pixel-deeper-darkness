/*
 * Pocket Workshop
 * An artifact for artificers that contains a folding workshop
 * Based on the Holy Tome mechanics from Shattered Pixel Dungeon
 */

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
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
import com.watabou.utils.Random;
import com.shatteredpixel.shatteredpixeldungeon.windows.artificer.WndArtificerSpells;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class PocketWorkshop extends Artifact {

    {
        image = ItemSpriteSheet.ARTIFACT_WORKSHOP;

        exp = 0;
        levelCap = 10;

        charge = Math.min(level()+3, 10);
        partialCharge = 0;
        chargeCap = Math.min(level()+3, 10);

        defaultAction = AC_CRAFT;

        unique = true;
        bones = false;
    }

    public static final String AC_CRAFT = "CRAFT";

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
                GameScene.show(new WndArtificerSpells(this, hero, false));
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
        return (isEquipped(hero) || (Dungeon.hero.hasTalent(Talent.AETHERIC_EXPANSION) && hero.belongings.contains(this)))
                && hero.buff(MagicImmune.class) == null
                && charge >= ability.chargeUse(hero)
                && ability.canCast(hero);
    }

    public void spendCharge(float chargesSpent) {
        // Check for Efficient Crafting talent
        Hero hero = Dungeon.hero;
        if (hero != null && hero.hasTalent(Talent.EFFICIENT_CRAFTING)) {
            // 33% chance at level 1, 66% chance at level 2
            float conserveChance = 0.33f * hero.pointsInTalent(Talent.EFFICIENT_CRAFTING);
            if (Random.Float() < conserveChance) {
                // Efficient crafting triggered - conserve 1 charge
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

        if (lvlDiffFromTarget >= 0) {
            exp += Math.round(chargesSpent * 10f * Math.pow(1.1f, lvlDiffFromTarget));
        } else {
            exp += Math.round(chargesSpent * 10f * Math.pow(0.75f, -lvlDiffFromTarget));
        }

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

    @Override
    public Item upgrade() {
        chargeCap = Math.min(chargeCap + 1, 10);
        return super.upgrade();
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new WorkshopRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (cursed || target.buff(MagicImmune.class) != null) return;

        if (charge < chargeCap) {
            if (!isEquipped(target)) amount *= 0.75f*target.pointsInTalent(Talent.AETHERIC_EXPANSION)/3f;
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

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (quickAbility != null) {
            bundle.put(QUICK_CLS, quickAbility.getClass());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(QUICK_CLS)) {
            Class quickCls = bundle.getClass(QUICK_CLS);
            for (ArtificerSpell ability : ArtificerSpell.getAllSpells()) {
                if (ability.getClass() == quickCls) {
                    quickAbility = ability;
                }
            }
        }
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
                    if (!isEquipped(Dungeon.hero)) {
                        chargeToGain *= 0.75f*Dungeon.hero.pointsInTalent(Talent.AETHERIC_EXPANSION)/3f;
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
            return quickAbility.name();
        }

        @Override
        public int actionIcon() {
            return quickAbility.icon() + HeroIcon.SPELL_ACTION_OFFSET;
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
                    GameScene.handleCell(QuickSlotButton.lastTarget.pos);
                }
            } else {
                quickAbility.onCast(PocketWorkshop.this, Dungeon.hero);

                if (quickAbility.targetingFlags() != -1 && Dungeon.quickslot.contains(PocketWorkshop.this)) {
                    targetingSpell = quickAbility;
                    QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(PocketWorkshop.this));
                }
            }
        }
    }
}