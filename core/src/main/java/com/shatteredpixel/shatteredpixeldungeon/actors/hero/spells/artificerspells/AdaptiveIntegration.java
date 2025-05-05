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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class AdaptiveIntegration extends ArtificerSpell {

    public static final AdaptiveIntegration INSTANCE = new AdaptiveIntegration();
    
    private int imageIndex = ItemSpriteSheet.ARTIFACT_TOOLKIT;
    
    @Override
    public int icon() {
        return imageIndex;
    }

    @Override
    public String name() {
        return Messages.get(this, "name");
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3; // All levels use 3 charges
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc");
        
        if (Dungeon.hero != null) {
            int talentLevel = Dungeon.hero.pointsInTalent(Talent.ADAPTIVE_INTEGRATION);
            desc += "\n\n";
            
            if (talentLevel == 0) {
                desc += Messages.get(this, "need_talent");
            } else if (talentLevel == 1) {
                desc += Messages.get(this, "desc_level1");
            } else if (talentLevel == 2) {
                desc += Messages.get(this, "desc_level2");
            } else if (talentLevel == 3) {
                desc += Messages.get(this, "desc_level3");
            }
            
            desc += "\n\n" + Messages.get(this, "cost", 3, 100);
        }
        
        return desc;
    }

    @Override
    public boolean canCast(Hero hero) {
        int talentLevel = hero.pointsInTalent(Talent.ADAPTIVE_INTEGRATION);
        if (talentLevel == 0) {
            GLog.w(Messages.get(this, "no_talent"));
            return false;
        }
        
        PocketWorkshop workshop = hero.belongings.getItem(PocketWorkshop.class);
        if (workshop == null || (workshop.getSpareParts() < 100)) {
            GLog.w(Messages.get(this, "not_enough_parts", 100, workshop != null ? workshop.getSpareParts() : 0));
            return false;
        }
        
        return super.canCast(hero);
    }

    @Override
    public void onCast(PocketWorkshop workshop, Hero hero) {
        GameScene.show(new IntegrationWindow(workshop));
    }

    private class IntegrationWindow extends Window {
        
        private static final int WIDTH      = 120;
        private static final int BTN_SIZE   = 32;
        private static final int BTN_GAP    = 5;
        private static final int GAP        = 2;
        
        private ItemButton btnPressed;
        
        private ItemButton btnItem1;
        private ItemButton btnItem2;
        private RedButton btnIntegrate;
        
        private PocketWorkshop workshop;
        
        public IntegrationWindow(PocketWorkshop workshop) {
            super();
            
            this.workshop = workshop;
            
            IconTitle titlebar = new IconTitle();
            titlebar.icon(new com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite(workshop));
            titlebar.label(Messages.get(AdaptiveIntegration.this, "window_title"));
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);
            
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(AdaptiveIntegration.this, "window_message"), 6);
            message.maxWidth(WIDTH);
            message.setPos(0, titlebar.bottom() + GAP);
            add(message);
            
            btnItem1 = new ItemButton() {
                @Override
                protected void onClick() {
                    btnPressed = btnItem1;
                    GameScene.selectItem(itemSelector);
                }
            };
            btnItem1.setRect((WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE);
            add(btnItem1);
            
            btnItem2 = new ItemButton() {
                @Override
                protected void onClick() {
                    btnPressed = btnItem2;
                    GameScene.selectItem(itemSelector);
                }
            };
            btnItem2.setRect(btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE);
            add(btnItem2);
            
            btnIntegrate = new RedButton(Messages.get(AdaptiveIntegration.this, "integrate")) {
                @Override
                protected void onClick() {
                    integrate();
                }
            };
            btnIntegrate.enable(false);
            btnIntegrate.setRect(0, btnItem1.bottom() + BTN_GAP, WIDTH, 20);
            add(btnIntegrate);
            
            resize(WIDTH, (int)btnIntegrate.bottom());
        }
        
        private void integrate() {
            Item first, second;
            if (btnItem1.item().trueLevel() >= btnItem2.item().trueLevel()) {
                first = btnItem1.item();
                second = btnItem2.item();
            } else {
                first = btnItem2.item();
                second = btnItem1.item();
            }
            
            // Check success chance based on talent level
            int talentLevel = Dungeon.hero.pointsInTalent(Talent.ADAPTIVE_INTEGRATION);
            float successChance;
            
            if (talentLevel == 1) {
                successChance = 0.5f; // 50% chance at level 1
            } else if (talentLevel == 2) {
                successChance = 0.75f; // 75% chance at level 2
            } else {
                successChance = 1.0f; // 100% chance at level 3
            }
            
            // Calculate parts cost (50% chance to use only 50 parts at level 3)
            int partsCost = 100;
            if (talentLevel == 3 && Random.Float() < 0.5f) {
                partsCost = 50;
                GLog.p(Messages.get(AdaptiveIntegration.this, "efficient_integration"));
            }
            
            // Attempt integration based on chance
            if (Random.Float() < successChance) {
                // Success
                Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                ScrollOfUpgrade.upgrade(Dungeon.hero);
                Item.evoke(Dungeon.hero);
                
                if (second.isEquipped(Dungeon.hero)) {
                    ((EquipableItem)second).doUnequip(Dungeon.hero, false);
                }
                second.detach(Dungeon.hero.belongings.backpack);
                
                if (second instanceof Armor) {
                    BrokenSeal seal = ((Armor) second).checkSeal();
                    if (seal != null) {
                        Dungeon.level.drop(seal, Dungeon.hero.pos);
                    }
                }
                
                // Preserve enchant/glyphs if present
                if (first instanceof Weapon && ((Weapon) first).hasGoodEnchant()) {
                    ((Weapon) first).upgrade(true);
                } else if (first instanceof Armor && ((Armor) first).hasGoodGlyph()) {
                    ((Armor) first).upgrade(true);
                } else {
                    first.upgrade();
                }

                Badges.validateItemLevelAquired(first);
                Item.updateQuickslot();
                
                workshop.spendCharge(chargeUse(Dungeon.hero)); // Use 3 charges
                workshop.spendSpareParts(partsCost); // Use parts
                
                GLog.p(Messages.get(AdaptiveIntegration.this, "success"));
                hide();
            } else {
                // Failure
                workshop.spendCharge(chargeUse(Dungeon.hero)); // Use 3 charges
                workshop.spendSpareParts(partsCost); // Use parts
                
                GLog.w(Messages.get(AdaptiveIntegration.this, "failure"));
                hide();
            }
        }
        
        protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {
            
            @Override
            public String textPrompt() {
                return Messages.get(AdaptiveIntegration.class, "prompt");
            }
            
            @Override
            public Class<? extends Bag> preferredBag() {
                return Belongings.Backpack.class;
            }
            
            @Override
            public boolean itemSelectable(Item item) {
                return item.isIdentified() && !item.cursed && item.isUpgradable();
            }
            
            @Override
            public void onSelect(Item item) {
                if (item != null && btnPressed.parent != null) {
                    btnPressed.item(item);
                    
                    Item item1 = btnItem1.item();
                    Item item2 = btnItem2.item();
                    
                    // Need 2 items
                    if (item1 == null || item2 == null) {
                        btnIntegrate.enable(false);
                    
                    // Both of the same type
                    } else if (item1.getClass() != item2.getClass()) {
                        btnIntegrate.enable(false);
                    
                    // And not the literal same item (unless quantity is >1)
                    } else if (item1 == item2 && item1.quantity() == 1) {
                        btnIntegrate.enable(false);
                    
                    } else {
                        btnIntegrate.enable(true);
                    }
                }
            }
        };
    }
}