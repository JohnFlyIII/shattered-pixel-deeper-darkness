# Artificer Class Implementation Notes

## Key Code Locations

- Hero class definitions: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/HeroClass.java`
- Hero class implementations: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/classes/`
- Talents system: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/Talent.java`
- Buffs implementation: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/buffs/`
- Artificer spells/crafts: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/actors/hero/spells/artificerspells/`
- UI components: `/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/windows/artificer/`

## Implementation Strategy

1. The Artificer class should be modeled after the Cleric in terms of mechanical structure
2. "Crafts" should be implemented as spells mechanically, similar to Cleric spells
3. Artificer uses PocketWorkshop artifact (similar to Cleric's HolyTome)
4. Class-specific talents should be defined in the Talent.java enum
5. Class-specific buffs should be added to the buffs directory

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
