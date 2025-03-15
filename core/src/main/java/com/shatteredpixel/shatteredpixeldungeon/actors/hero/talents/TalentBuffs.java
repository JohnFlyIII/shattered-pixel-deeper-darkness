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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

/**
 * Buffs used by the Talent system.
 */
public class TalentBuffs {

    public static class ImprovisedProjectileCooldown extends FlavourBuff{
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
    }
    public static class LethalMomentumTracker extends FlavourBuff{}
    public static class StrikingWaveTracker extends FlavourBuff{}
    public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}}
    public static class EmpoweredStrikeTracker extends FlavourBuff{
        //blast wave on-hit doesn't resolve instantly, so we delay detaching for it
        public boolean delayedDetach = false;
    }
    public static class ProtectiveShadowsTracker extends Buff {
        float barrierInc = 0.5f;

        @Override
        public boolean act() {
            //barrier every 2/1 turns, to a max of 3/5
            if (((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS) && target.invisible > 0){
                Barrier barrier = Buff.affect(target, Barrier.class);
                if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.PROTECTIVE_SHADOWS)) {
                    barrierInc += 0.5f * ((Hero) target).pointsInTalent(Talent.PROTECTIVE_SHADOWS);
                }
                if (barrierInc >= 1){
                    barrierInc = 0;
                    barrier.incShield(1);
                } else {
                    barrier.incShield(0); //resets barrier decay
                }
            } else {
                detach();
            }
            spend( TICK );
            return true;
        }

        private static final String BARRIER_INC = "barrier_inc";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( BARRIER_INC, barrierInc);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            barrierInc = bundle.getFloat( BARRIER_INC );
        }
    }
    public static class BountyHunterTracker extends FlavourBuff{}
    public static class RejuvenatingStepsCooldown extends FlavourBuff{
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
        public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS)), 1); }
    }
    public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}}
    public static class SeerShotCooldown extends FlavourBuff{
        public int icon() { return target.buff(com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
    }
    public static class SpiritBladesTracker extends FlavourBuff{}
    public static class PatientStrikeTracker extends Buff {
        public int pos;
        { type = Buff.buffType.POSITIVE; }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
        @Override
        public boolean act() {
            if (pos != target.pos) {
                detach();
            } else {
                spend(TICK);
            }
            return true;
        }
        private static final String POS = "pos";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
        }
    }
    public static class AggressiveBarrierCooldown extends FlavourBuff{
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
    }
    public static class LiquidAgilEVATracker extends FlavourBuff{}
    public static class LiquidAgilACCTracker extends FlavourBuff{
        public int uses;

        { type = buffType.POSITIVE; }
        public int icon() { return BuffIndicator.INVERT_MARK; }
        public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

        private static final String USES = "uses";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(USES, uses);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            uses = bundle.getInt(USES);
        }
    }
    public static class LethalHasteCooldown extends FlavourBuff{
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
    }
    public static class SwiftEquipCooldown extends FlavourBuff{
        public boolean secondUse;
        public boolean hasSecondUse(){
            return secondUse;
        }

        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) {
            if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
            else                icon.hardlight(0.35f, 0f, 0.7f);
        }
        public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

        private static final String SECOND_USE = "second_use";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SECOND_USE, secondUse);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            secondUse = bundle.getBoolean(SECOND_USE);
        }
    }
    public static class DeadlyFollowupTracker extends FlavourBuff{
        public int object;
        { type = Buff.buffType.POSITIVE; }
        public int icon() { return BuffIndicator.INVERT_MARK; }
        public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
        private static final String OBJECT    = "object";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(OBJECT, object);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            object = bundle.getInt(OBJECT);
        }
    }
    public static class PreciseAssaultTracker extends FlavourBuff{
        { type = buffType.POSITIVE; }
        public int icon() { return BuffIndicator.INVERT_MARK; }
        public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
    }
    public static class VariedChargeTracker extends Buff{
        public Class weapon;

        private static final String WEAPON    = "weapon";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(WEAPON, weapon);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            weapon = bundle.getClass(WEAPON);
        }
    }
    public static class CombinedLethalityAbilityTracker extends FlavourBuff{
        public MeleeWeapon weapon;
    }
    public static class CombinedEnergyAbilityTracker extends FlavourBuff{
        public boolean monkAbilused = false;
        public boolean wepAbilUsed = false;

        private static final String MONK_ABIL_USED  = "monk_abil_used";
        private static final String WEP_ABIL_USED   = "wep_abil_used";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(MONK_ABIL_USED, monkAbilused);
            bundle.put(WEP_ABIL_USED, wepAbilUsed);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
            wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
        }
    }
    public static class CounterAbilityTacker extends FlavourBuff{}
    public static class SatiatedSpellsTracker extends Buff{
        @Override
        public int icon() {
            return BuffIndicator.SPELL_FOOD;
        }
    }
    //used for metamorphed searing light
    public static class SearingLightCooldown extends FlavourBuff{
        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }
        public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
    }

    public static class ProvokedAngerTracker extends FlavourBuff{
        { type = Buff.buffType.POSITIVE; }
        public int icon() { return BuffIndicator.WEAPON; }
        public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
    }
    public static class LingeringMagicTracker extends FlavourBuff{
        { type = Buff.buffType.POSITIVE; }
        public int icon() { return BuffIndicator.WEAPON; }
        public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
    }
    public static class SuckerPunchTracker extends Buff{}
    public static class FollowupStrikeTracker extends FlavourBuff{
        public int object;
        { type = Buff.buffType.POSITIVE; }
        public int icon() { return BuffIndicator.INVERT_MARK; }
        public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
        private static final String OBJECT    = "object";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(OBJECT, object);
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            object = bundle.getInt(OBJECT);
        }
    }

    public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}}
    public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}}

    public static class WarriorFoodImmunity extends FlavourBuff{
        { actPriority = HERO_PRIO+1; }
    }
}