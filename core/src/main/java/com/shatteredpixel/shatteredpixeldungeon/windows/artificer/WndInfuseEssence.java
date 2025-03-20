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

package com.shatteredpixel.shatteredpixeldungeon.windows.artificer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.talents.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.PocketWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndInfuseEssence extends Window {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 140;
    private static final int ITEM_HEIGHT = 30;
    private static final int GAP = 2;
    
    private ScrollingListPane listPane;
    private Hero hero;
    
    public WndInfuseEssence(Hero hero) {
        super();
        
        this.hero = hero;
        
        resize(WIDTH, HEIGHT);
        
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(WndInfuseEssence.class, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.setPos((WIDTH - title.width()) / 2, GAP);
        add(title);
        
        // Instructions text
        RenderedTextBlock instructions = PixelScene.renderTextBlock(Messages.get(WndInfuseEssence.class, "instructions"), 7);
        instructions.maxWidth(WIDTH - 10);
        instructions.setPos(5, title.bottom() + GAP);
        add(instructions);
        
        // Calculate resource costs based on talent level
        int talentLevel = hero.pointsInTalent(Talent.INFUSE_ESSENCE);
        int levelCost = (talentLevel == 1) ? 2 : 1;
        int partsCost = (talentLevel == 1) ? 150 : 75;
        
        // Display current resources
        PocketWorkshop workshop = getWorkshop();
        RenderedTextBlock resources = PixelScene.renderTextBlock(
                Messages.get(WndInfuseEssence.class, "resources", 
                        hero.lvl, levelCost, 
                        workshop.getSpareParts(), partsCost,
                        workshop.getCharges()), 7);
        resources.maxWidth(WIDTH - 10);
        resources.setPos(5, instructions.bottom() + GAP);
        add(resources);
        
        ArrayList<Item> items = getUpgradeableItems();
        
        float contentHeight = 0;
        
        if (items.isEmpty()) {
            RenderedTextBlock noItems = PixelScene.renderTextBlock(Messages.get(WndInfuseEssence.class, "no_items"), 7);
            noItems.maxWidth(WIDTH - 10);
            noItems.setPos((WIDTH - noItems.width()) / 2, resources.bottom() + 30);
            add(noItems);
            contentHeight = noItems.bottom() + GAP;
        } else {
            // Only create the list if we have items
            add(listPane = new ScrollingListPane());
            listPane.setRect(5, resources.bottom() + GAP, WIDTH - 10, HEIGHT - resources.bottom() - 15);
            
            for (Item item : items) {
                listPane.addItem(new ItemEntry(item));
            }
            contentHeight = listPane.bottom() + GAP;
        }
    }
    
    private PocketWorkshop getWorkshop() {
        for (Item item : hero.belongings.backpack) {
            if (item instanceof PocketWorkshop) {
                return (PocketWorkshop) item;
            }
        }
        if (hero.belongings.artifact instanceof PocketWorkshop) {
            return (PocketWorkshop) hero.belongings.artifact;
        }
        return null;
    }
    
    private ArrayList<Item> getUpgradeableItems() {
        ArrayList<Item> result = new ArrayList<>();
        
        for (Item item : hero.belongings.backpack) {
            if (canBeUpgraded(item)) {
                result.add(item);
            }
        }
        
        // Equipment slots
        if (canBeUpgraded(hero.belongings.weapon)) result.add(hero.belongings.weapon);
        if (canBeUpgraded(hero.belongings.armor)) result.add(hero.belongings.armor);
        if (canBeUpgraded(hero.belongings.artifact)) result.add(hero.belongings.artifact);
        if (canBeUpgraded(hero.belongings.misc)) result.add(hero.belongings.misc);
        if (canBeUpgraded(hero.belongings.ring)) result.add(hero.belongings.ring);
        
        return result;
    }
    
    private boolean canBeUpgraded(Item item) {
        if (item == null) return false;
        
        // Only weapons, armor, rings, and wands
        if (!(item instanceof Weapon) && 
            !(item instanceof Armor) && 
            !(item instanceof Ring) && 
            !(item instanceof Wand)) {
            return false;
        }
        
        // Check if the item can be upgraded
        if (!item.isUpgradable()) {
            return false;
        }
        
        // Check if item is at max level (typically 3 for regular items, but varies)
        if (item.level() >= 10) { // Using 10 as a safe max - most items don't go this high
            return false;
        }
        
        // Check if we have enough resources to upgrade this item
        int talentLevel = hero.pointsInTalent(Talent.INFUSE_ESSENCE);
        int levelCost = (talentLevel == 1) ? 2 : 1;
        int partsCost = (talentLevel == 1) ? 150 : 75;
        
        if (hero.lvl <= levelCost) {
            return false;
        }
        
        PocketWorkshop workshop = getWorkshop();
        if (workshop == null || workshop.getSpareParts() < partsCost || workshop.getCharges() < 1) {
            return false;
        }
        
        return true;
    }
    
    // Performs the actual infusion
    private void infuseItem(Item item) {
        int talentLevel = hero.pointsInTalent(Talent.INFUSE_ESSENCE);
        int levelCost = (talentLevel == 1) ? 2 : 1;
        int partsCost = 200 - (talentLevel - 1) * 50;
        
        // Check if hero has enough levels
        if (hero.lvl < levelCost + 1) {
            GLog.w(Messages.get(Talent.class, "infuse_essence.insufficient_levels"));
            return;
        }
        
        // Check if hero has enough spare parts and charges
        PocketWorkshop workshop = getWorkshop();
        if (workshop == null) {
            return;
        }
        
        if (workshop.getSpareParts() < partsCost) {
            GLog.w(Messages.get(Talent.class, "infuse_essence.insufficient_parts", 
                   partsCost - workshop.getSpareParts()));
            return;
        }
        
        if (workshop.getCharges() < 1) {
            GLog.w(Messages.get(Talent.class, "infuse_essence.insufficient_charges"));
            return;
        }
        
        // Check if item is at max level
        if (item.level() >= 10) {
            GLog.w(Messages.get(Talent.class, "infuse_essence.cannot_upgrade"));
            return;
        }
        
        // All checks passed, perform the infusion
        hero.loseLevel(levelCost); // Use loseLevel method instead for proper stat reduction
        workshop.spendSpareParts(partsCost);
        workshop.spendCharge(1); // Consume one workshop charge
        
        item.upgrade();
        
        // Effects
        Sample.INSTANCE.play(Assets.Sounds.EVOKE);
        Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.UP), 5);
        GLog.p(Messages.get(Talent.class, "infuse_essence.infused", item.name()));
        
        hide();
    }
    
    // Row item in the list
    private class ItemEntry extends ScrollingListPane.ListItem {
        
        private Item item;
        private IconButton upgradeButton;
        
        public ItemEntry(Item item) {
            super(new ItemSprite(item), item.name());
            
            this.item = item;
            
            upgradeButton = new IconButton(Icons.get(Icons.ENTER)) {
                @Override
                protected void onClick() {
                    GameScene.show(new WndConfirmInfusion(item));
                }
            };
            add(upgradeButton);
        }
        
        @Override
        protected void layout() {
            super.layout();
            
            upgradeButton.setRect(
                width - upgradeButton.width() - 1, 
                y + (height() - upgradeButton.height())/2, 
                16, 16);
        }
        
        @Override
        public boolean onClick(float x, float y) {
            if (inside(x, y)) {
                // Let the button handle its own clicks
                if (upgradeButton.icon() != null && upgradeButton.icon().overlapsPoint(x, y)) {
                    GameScene.show(new WndConfirmInfusion(item));
                } else {
                    GameScene.show(new WndConfirmInfusion(item));
                }
                return true;
            }
            return false;
        }
    }
    
    // Confirmation window
    private class WndConfirmInfusion extends Window {
        
        private static final int WIDTH = 120;
        private static final int BTN_HEIGHT = 20;
        private static final float GAP = 2;
        
        public WndConfirmInfusion(final Item item) {
            int talentLevel = hero.pointsInTalent(Talent.INFUSE_ESSENCE);
            int levelCost = (talentLevel == 1) ? 2 : 1;
            int partsCost = 200 - (talentLevel - 1) * 50;
            
            IconTitle titlebar = new IconTitle();
            titlebar.icon(new ItemSprite(item));
            titlebar.label(Messages.titleCase(item.name()));
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);
            
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(WndInfuseEssence.class, "confirm", 
                     item.name(), levelCost, partsCost), 6);
            message.maxWidth(WIDTH);
            message.setPos(0, titlebar.bottom() + GAP);
            add(message);
            
            float pos = message.bottom() + 3*GAP;
            
            RedButton btnYes = new RedButton(Messages.get(WndInfuseEssence.class, "yes")) {
                @Override
                protected void onClick() {
                    hide();
                    WndInfuseEssence.this.infuseItem(item);
                }
            };
            btnYes.setRect(0, pos, WIDTH/2-1, BTN_HEIGHT);
            add(btnYes);
            
            RedButton btnNo = new RedButton(Messages.get(WndInfuseEssence.class, "no")) {
                @Override
                protected void onClick() {
                    hide();
                }
            };
            btnNo.setRect(WIDTH/2+1, pos, WIDTH/2-1, BTN_HEIGHT);
            add(btnNo);
            
            resize(WIDTH, (int)btnYes.bottom());
        }
    }
    
}