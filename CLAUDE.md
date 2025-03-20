# Artificer Class Implementation Notes

## Installed CLI Tools

- `ripgrep` (`rg`) - Fast, modern alternative to grep for searching code
- `bat` - Cat replacement with syntax highlighting
- `fd` - More user-friendly alternative to find
- `jq` - JSON processor for handling data files

## Key Code Locations

- Hero class definitions: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`
- Hero class implementations: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/classes/`
- Talents system: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/talents/Talent.java`
- Additional talent files: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/talents/` (TalentInit.java, TalentBuffs.java, TalentEffects.java, TalentSerialization.java)
- Buffs implementation: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/`
- Artificer spells/crafts: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/artificerspells/`
- UI components: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/artificer/`
- Weapons implementation: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/`
- Text/message translations: `/core/src/main/assets/messages/`
- Sprites and images: `/core/src/main/assets/interfaces/` and `/core/src/main/assets/sprites/`
- Item sprite definitions: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/sprites/ItemSpriteSheet.java`
- Asset references: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/Assets.java`

## Implementation Strategy

1. The Artificer class should be modeled after the Cleric in terms of mechanical structure
2. "Crafts" should be implemented as spells mechanically, similar to Cleric spells
3. Artificer uses PocketWorkshop artifact (similar to Cleric's HolyTome)
4. Class-specific talents should be defined in the Talent.java enum
5. Class-specific buffs should be added to the buffs directory
6. ModularInfusionRelay weapon similar to Huntress's SpiritBow for ranged combat

### Craft Implementation Guidelines

1. **All abilities should be implemented as craftable items** in the Craft menu, not as direct buttons on the PocketWorkshop
2. To add a new ability:
   - Create a new class extending ArtificerSpell in the artificerspells package
   - Implement the INSTANCE singleton pattern with the required methods
   - Add the ability to the getSpellList and getAllSpells methods in ArtificerSpell.java
   - Add appropriate talent checks if the ability requires a talent
3. **Never add new action buttons to the PocketWorkshop itself** - all functionality should go through the crafting system
4. Each craft should:
   - Have a proper icon and description
   - Specify charge cost via chargeUse() method
   - Implement onCast() for its behavior
   - Call onSpellCast() to handle charge consumption and other standard behaviors

## Development Steps

1. Define Artificer class abilities (already started in Artificer.java)
2. Complete ArtificerSpell base class and spells under artificerspells/ directory
3. Create class-specific talents in Talent.java
4. Implement buffs for talents and abilities
5. Finalize UI components for craft selection/management
6. Add sprites and visual assets
7. Implement subclasses and their mechanics

## Reference Points

- Use Cleric's implementation as a template:
  - Cleric.java -> Artificer.java
  - ClericSpell.java -> ArtificerSpell.java
  - Cleric's spells -> Artificer's crafts
  - HolyTome artifact -> PocketWorkshop artifact
- Follow similar UI patterns in WndArtificerSpells.java
- Huntress SpiritBow -> ModularInfusionRelay for ranged weapon implementation

## Implemented Components

1. **PocketWorkshop**: Artificer's primary artifact for crafting, based on Cleric's HolyTome
2. **ArtificerSpell/TargetedArtificerSpell**: Base classes for implementing crafting abilities
3. **ModularInfusionRelay**: Artificer's special weapon that fires energy bolts (based on SpiritBow)
4. **SeekingMine**: First crafting ability - a magical tracking explosive that deals 2-6 damage
5. **MechanistsDisassembly**: Crafting ability that breaks down traps into spare parts
6. **TrapCloaker**: Crafting ability that allows the hero to pass through traps safely
7. **InfuseEssence**: Crafting ability that transfers hero levels to upgrade equipment
8. **Aetheric Capacitor**: Tier 1 talent that increases PocketWorkshop maximum charge capacity
9. **Aetheric Cloaking**: Tier 1 talent that allows the Artificer to be invisible to traps
10. **Infuse Essence**: Tier 2 talent that allows spending hero levels and spare parts to upgrade equipment

### Hero Level Reduction System

The `loseLevel(int levels)` method in Hero.java provides a robust implementation for reducing hero levels:

1. It properly reduces level count and resets experience to zero
2. Reduces max HP by 5 points per level lost
3. Reduces attackSkill and defenseSkill stats
4. Provides appropriate visual feedback to the player
5. Safety checks to prevent reducing below level 1

This system can be used whenever an ability should cost hero levels. To use it:

```java
// Check if hero can lose levels first
if (hero.lvl <= levelCost) {
    return false; // Cannot afford the level cost
}

// Apply the level reduction
hero.loseLevel(levelCost);

// Now the hero is at a lower level with reduced stats
```

## Message Locations
- Item descriptions: `/core/src/main/assets/messages/items/items.properties`
- Hero/spell descriptions: `/core/src/main/assets/messages/actors/actors.properties`
- Add new entries for each newly created class and ability
- Message format keys follow the Java class path structure

## Key Game Mechanics Classes

### Traps
- Base class: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/traps/Trap.java`
- Trap trigger logic: Found in `Level.java` in the `pressCell()` method
- Targeted traps: Implement `canTarget(Char ch)` method to determine eligibility
- Example traps: `PoisonDartTrap.java`, `GrimTrap.java`, etc.

### Buffs
- Base classes: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/Buff.java`
- FlavourBuff: Temporary buffs with duration (most common for talents)
- PassiveBuff: Persistent buffs that remain until conditions change
- BuffIndicator: Icon display for active buffs

### Important Base Classes
- Hero: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Hero.java`
- Item: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/Item.java`
- Artifact: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/artifacts/Artifact.java`
- Weapon: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/weapon/Weapon.java`
- Level: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/levels/Level.java`

## Adding Art Assets and Visual Effects

### Sprites and Images
- Item sprites: `/core/src/main/assets/items`
- Character sprites: `/core/src/main/assets/sprites`
- Interface elements: `/core/src/main/assets/interfaces`
- Icon references are in `ItemSpriteSheet.java` and similar classes

### Visual Effects
- Particle effects: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects/particles`
- Common effects: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/effects`
- Most effects have static factory methods or constructors

### Sound Effects
- Sound references: `Assets.Sounds` class
- Play sounds with: `Sample.INSTANCE.play(Assets.Sounds.SOUND_NAME);`
- Add volume/pitch variation with: `Sample.INSTANCE.play(Assets.Sounds.SOUND_NAME, 1, Random.Float(0.87f, 1.15f));`

## Talent Implementation Guide

### Key Files for Talent Implementation

1. **Talent.java**: (`/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/talents/Talent.java`)
   - Main enum where all talents are defined
   - Defines talent icon IDs and max points (2 for T1/T2, 3 for T3, 4 for T4)
   - Default constructor is used for T1/T2 talents (max 2 points)
   - Alternative constructor for T3/T4 talents: `Talent(int icon, int maxPoints)`

2. **TalentInit.java**: (`/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/talents/TalentInit.java`)
   - Defines which talents are available to each class
   - Assigns talents to specific tiers
   - Initializes class, subclass, and armor talents

3. **TalentEffects.java**: (`/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/talents/TalentEffects.java`)
   - Handles the effects of talents
   - `onTalentUpgraded(Hero hero, Talent talent)`: Called when a talent is upgraded
   - Contains specialized methods for different effect types (food, potions, etc.)

4. **TalentIcon.java**: (`/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/TalentIcon.java`)
   - Handles rendering of talent icons from the talent sprite sheet
   - Used in talent selection screens and talent buttons

5. **talent_icons.png**: (`/core/src/main/assets/interfaces/talent_icons.png`)
   - Sprite sheet containing all talent icons organized in a grid
   - Icons are 16×16 pixels each, arranged in rows of 16 icons

6. **actors.properties**: (`/core/src/main/assets/messages/actors/actors.properties`)
   - Contains text for talent names and descriptions
   - Format: `talent.TALENT_NAME.title=Talent Name`
   - Format: `talent.TALENT_NAME.desc=Talent description with details about each rank`

### Step-by-Step Talent Implementation

1. **Add to Talent Enum**:
   ```java
   //Artificer T1
   FULL_TANK(192), EFFICIENT_CRAFTING(194), AETHERIC_CAPACITOR(195),
   ```
   - Choose an unused icon ID (usually sequential)
   - Use default constructor for T1/T2 talents, or specify max points for T3/T4

2. **Add to Initialization**:
   ```java
   case ARTIFICER:
       Collections.addAll(tierTalents, Talent.FULL_TANK, Talent.EFFICIENT_CRAFTING, 
           Talent.AETHERIC_CAPACITOR, Talent.IRON_WILL);
       break;
   ```
   - Found in TalentInit.java in the appropriate tier section

3. **Add Text to Properties**:
   ```
   talent.AETHERIC_CAPACITOR.title=Aetheric Capacitor
   talent.AETHERIC_CAPACITOR.desc=Increase the maximum charges of your Pocket Workshop by 1/2. By installing specialized aetheric crystals, you've expanded your workshop's energy storage capacity.
   ```
   - Add to `/core/src/main/assets/messages/actors/actors.properties`
   - Use _+1:_ and _+2:_ for details about each rank if needed

4. **Implement Effects**:
   - Add a case in `onTalentUpgraded` in TalentEffects.java for talents that need action on upgrade
   - Implement methods in affected classes to handle the talent effects
   - For passive stat increases, add calculation methods that check talent levels

5. **Special Effect Implementation**:
   - Create helper methods for complex talent effects
   - Use `hero.pointsInTalent(Talent.X)` to get current talent level (0-max)
   - Use `hero.hasTalent(Talent.X)` for simple existence checks

### Example: Aetheric Capacitor Talent

1. **In Talent.java**:
   ```java
   AETHERIC_CAPACITOR(195),
   ```

2. **In TalentInit.java**:
   ```java
   case ARTIFICER:
       Collections.addAll(tierTalents, Talent.FULL_TANK, Talent.EFFICIENT_CRAFTING, 
           Talent.AETHERIC_CAPACITOR, Talent.IRON_WILL);
       break;
   ```

3. **In actors.properties**:
   ```
   talent.AETHERIC_CAPACITOR.title=Aetheric Capacitor
   talent.AETHERIC_CAPACITOR.desc=Increase the maximum charges of your Pocket Workshop by 1/2. By installing specialized aetheric crystals, you've expanded your workshop's energy storage capacity.
   ```

4. **In PocketWorkshop.java**:
   ```java
   private int getMaxChargeCap() {
       int maxCap = 10;
       if (Dungeon.hero != null && Dungeon.hero.hasTalent(Talent.AETHERIC_CAPACITOR)) {
           maxCap += Dungeon.hero.pointsInTalent(Talent.AETHERIC_CAPACITOR);
       }
       return maxCap;
   }
   ```

5. **In TalentEffects.java**:
   ```java
   if (talent == Talent.AETHERIC_CAPACITOR && hero.heroClass == HeroClass.ARTIFICER) {
       for (Item item : Dungeon.hero.belongings.backpack) {
           if (item instanceof PocketWorkshop) {
               if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
                   ((PocketWorkshop) item).recalculateChargeCap();
               }
           }
       }
   }
   ```

### Talent Icon IDs
- Artificer talents use icon IDs in the 190-195 range
- Ensure new talents use unique IDs that don't conflict with existing ones
- Icon IDs correspond to the actual sprites used in the game UI

### Talent Points and Tiers
- Tier 1: Available at hero level 2, max 2 points
- Tier 2: Available at hero level 7, max 2 points
- Tier 3: Available at hero level 13, max 3 points
- Tier 4: Available at hero level 21, max 4 points

### Implementation Example: Trap Avoidance with Aetheric Cloaking

This shows how to modify game mechanics to work with Artificer talents:

1. **Create a buff class** (`AethericCloaking.java`):
   ```java
   public class AethericCloaking extends FlavourBuff {
       // Helper method to check if hero is cloaked
       public static boolean isCloakedFromTraps(Hero hero) {
           return hero != null && hero.buff(AethericCloaking.class) != null;
       }
       
       // Static method to activate the cloaking
       public static void activate(Hero hero) {
           if (hero.hasTalent(Talent.AETHERIC_CLOAKING)) {
               // Duration based on talent level: 4/8 turns
               int duration = 4 * hero.pointsInTalent(Talent.AETHERIC_CLOAKING);
               Buff.affect(hero, AethericCloaking.class, duration);
               Buff.affect(hero, AethericCloakingCooldown.class, COOLDOWN);
           }
       }
   }
   ```

2. **Add activation to PocketWorkshop**:
   ```java
   public static final String AC_CLOAK = "CLOAK";
   
   @Override
   public ArrayList<String> actions(Hero hero) {
       // Add cloak action if hero has talent and it's not on cooldown
       if (hero.hasTalent(Talent.AETHERIC_CLOAKING) 
           && hero.buff(AethericCloaking.AethericCloakingCooldown.class) == null
           && charge >= 1) {
           actions.add(AC_CLOAK);
       }
   }
   
   @Override
   public void execute(Hero hero, String action) {
       if (action.equals(AC_CLOAK)) {
           // Activate cloaking
           AethericCloaking.activate(hero);
           spendCharge(1);
       }
   }
   ```

3. **Modify game mechanics** (in `Level.java`):
   ```java
   if (trap != null) {
       if (Dungeon.hero.pos == cell && Dungeon.hero.buff(AethericCloaking.class) != null) {
           // Hero has Aetheric Cloaking active, don't trigger the trap
           Sample.INSTANCE.play(Assets.Sounds.MELD);
           discover(cell);
           GLog.i(Messages.get(AethericCloaking.class, "trap_bypass"));
       } else {
           trap.trigger();
       }
   }
   ```

4. **Modify existing trap classes** (in `PoisonDartTrap.java`):
   ```java
   protected boolean canTarget(Char ch) {
       // Skip targeting if character has AethericCloaking
       if (ch instanceof Hero && ((Hero)ch).buff(AethericCloaking.class) != null) {
           return false;
       }
       return true;
   }
   ```

This implementation creates a comprehensive system where:
- The talent is defined in Talent.java and TalentInit.java
- A new action is added to the PocketWorkshop artifact
- A buff system tracks the active state and cooldown
- Core game mechanics are modified to respect the buff state
- Proper messages are added for all user interactions

## Common Implementation Patterns

### Adding an Action to Artifacts (like PocketWorkshop)
1. Define a constant for the action: `public static final String AC_CLOAK = "CLOAK";`
2. Add the action in the `actions()` method with appropriate conditions
3. Implement the action in the `execute()` method
4. Add translations for button text in `items.properties`

### Interacting with Game Mechanics
- Most game mechanics are isolated in appropriate classes (Level.java for level interactions, etc.)
- Many mechanics use callbacks or listeners that can be intercepted
- When adding new mechanics, look for similar existing patterns to follow
- Most behaviors can be modified by adding conditions that check for specific buffs

### Working with Buffs
1. Create a new buff class extending FlavourBuff (temporary) or Buff (persistent)
2. Override necessary methods: icon(), tintIcon(), desc(), etc.
3. To apply: `Buff.affect(target, MyBuff.class, duration);`
4. To check: `target.buff(MyBuff.class) != null`
5. To remove: `target.buff(MyBuff.class).detach();`

### Special Considerations
- Use `hero.spend(1f)` when actions should take game time
- Use `hero.busy()` to prevent other actions during animations
- Use `hero.next()` to proceed to the next turn
- Add feedback with GLog.i/w/n for user visibility
- Use appropriate sound effects with `Sample.INSTANCE.play()`

## UI Components and Windows

### Important UI Paths
- Base UI components: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/ui/`
- Window implementations: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/`
- Artificer-specific windows: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/artificer/`
- Interface assets: `/core/src/main/assets/interfaces/`

### Common UI Components
- **Window**: Base class for all popup windows
- **IconTitle**: Title bar with icon and text, used at the top of most windows
- **RedButton**: Standard button with red highlight 
- **RenderedTextBlock**: Text block that supports formatting and wrapping
- **ScrollPane**: Container that allows scrolling through content
- **ScrollingListPane**: Specialized list with scrolling support
- **ItemSlot**: UI element for displaying items with their icons
- **InventoryPane**: Grid display of inventory items

### Window Implementation Patterns
- Most windows extend the `Window` class
- Use `IconTitle` for consistent title bars
- For lists, use `ScrollingListPane` with custom `ListItem` implementations
- For confirmation dialogs, show a second window with yes/no options
- Message text should use `Messages.get()` for localization
- Use `layout()` method to position components and set window size

### Window Examples
- **WndInfuseEssence**: Artificer window for upgrading items using hero levels
- **WndUpgrade**: General upgrade window for template reference
- **WndArtificerSpells**: Lists available crafts/spells for the Artificer
- **WndTitledMessage**: Simple titled message window with text content
- **WndOptions**: Dialog window with multiple options to choose from

### Common Window Structure
```java
public class WndExample extends Window {
    
    private static final int WIDTH = 120;        // Standard window width
    private static final int MARGIN = 2;         // Standard margin
    private static final int BUTTON_HEIGHT = 16; // Standard button height
    
    public WndExample(Item item) {
        super();
        
        // Create title section with item icon
        IconTitle titlebar = new IconTitle(item);
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);
        
        // Add descriptive text
        RenderedTextBlock text = PixelScene.renderTextBlock(
            Messages.get(this, "text"), 6);
        text.maxWidth(WIDTH);
        text.setPos(0, titlebar.bottom() + MARGIN);
        add(text);
        
        // Add a button
        RedButton button = new RedButton(Messages.get(this, "button")) {
            @Override
            protected void onClick() {
                // Handle button click
                hide();
            }
        };
        button.setRect(0, text.bottom() + MARGIN, WIDTH, BUTTON_HEIGHT);
        add(button);
        
        // Resize window to fit content
        resize(WIDTH, (int)button.bottom());
    }
}
