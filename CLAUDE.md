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

## Implementation Strategy

1. The Artificer class should be modeled after the Cleric in terms of mechanical structure
2. "Crafts" should be implemented as spells mechanically, similar to Cleric spells
3. Artificer uses PocketWorkshop artifact (similar to Cleric's HolyTome)
4. Class-specific talents should be defined in the Talent.java enum
5. Class-specific buffs should be added to the buffs directory
6. ModularInfusionRelay weapon similar to Huntress's SpiritBow for ranged combat

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

## Message Locations
- Item descriptions: `/core/src/main/assets/messages/items/items.properties`
- Hero/spell descriptions: `/core/src/main/assets/messages/actors/actors.properties`
- Add new entries for each newly created class and ability
- Message format keys follow the Java class path structure
