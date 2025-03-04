# Hero Class Refactoring Plan

## Overview

This document outlines the plan for refactoring the hero class architecture in Shattered Pixel Dungeon. The goal is to break up the monolithic `Hero` class into smaller, more manageable components, making it easier to add new hero classes and abilities in the future.

## Current Structure

Currently, the hero system is implemented mainly in:
- `Hero.java`: A large class (>2500 lines) that handles all hero functionality
- `HeroClass.java`: An enum that defines the available hero classes
- `HeroSubClass.java`: An enum that defines the available subclasses
- `Talent.java`: An enum that defines talents for each hero class

## Proposed Structure

The refactoring will introduce a new package structure:

```
com.shatteredpixel.shatteredpixeldungeon.actors.hero.classes/
├── HeroBase.java (abstract base class extending Hero)
├── HeroFactory.java (factory for creating appropriate hero instances)
├── Warrior.java
├── Mage.java
├── Rogue.java
├── Huntress.java
├── Duelist.java
├── Cleric.java
└── Artificer.java
```

### Key Components

1. **HeroBase**: An abstract class that extends `Hero` and provides a foundation for hero-specific behaviors.

2. **Hero-specific classes**: Each concrete hero class (Warrior, Mage, etc.) will extend `HeroBase` and implement class-specific mechanics.

3. **HeroFactory**: A factory class that creates the appropriate hero class instance based on the selected `HeroClass`.

## Implementation Phases

### Phase 1: Create Basic Structure
- Create the hero.classes package structure
- Implement the HeroBase abstract class
- Create placeholder implementations for each hero class
- Implement the HeroFactory

### Phase 2: Migrate Common Functionality
- Move common hero functionality from Hero to HeroBase
- Ensure backward compatibility with existing code

### Phase 3: Implement Class-Specific Logic
- Move class-specific code from Hero to the appropriate hero class
- Implement class-specific behaviors in each hero class

### Phase 4: Integrate with Existing Systems
- Update all references to Hero to use HeroBase where appropriate
- Ensure that existing systems work with the new classes

## Benefits of the Refactoring

1. **Improved Maintainability**: Smaller, more focused classes that are easier to understand and modify.

2. **Easier Class Creation**: Adding a new hero class will be simpler, requiring only a new class file rather than modifications to a large, complex class.

3. **Better Encapsulation**: Class-specific behaviors will be encapsulated within their respective classes.

4. **Reduced Coupling**: The new structure will reduce dependencies and coupling between components.

## Special Case: Artificer Class

The Artificer class is currently being implemented with special features like the Full Tank talent. This talent allows the Artificer to gain charges when eating food, which can be used to power wands even when they have no charges.

In the new structure, this behavior will be implemented in the Artificer class directly, making it easier to understand and modify.

## Example Implementation: Full Tank Talent

```java
public class Artificer extends HeroBase {
    private int fullTankCharges = 0;
    
    public void onEatFood(Food food) {
        // If the hero has the Full Tank talent, gain charges
        if (hasTalent(Talent.FULL_TANK)) {
            int charges = pointsInTalent(Talent.FULL_TANK);
            addFullTankCharges(charges);
        }
    }
    
    public void addFullTankCharges(int amount) {
        fullTankCharges += amount;
    }
    
    public int getFullTankCharges() {
        return fullTankCharges;
    }
    
    public boolean useFullTankCharges(int amount) {
        if (fullTankCharges >= amount) {
            fullTankCharges -= amount;
            return true;
        }
        return false;
    }
}
```

Then, when attempting to use a wand, the code would check if the hero is an Artificer and has charges:

```java
// In Wand.java
if (owner instanceof Artificer && curCharges < chargesPerCast()) {
    Artificer artificer = (Artificer) owner;
    if (artificer.useFullTankCharges(1)) {
        return true;
    }
}
```

## Migration Strategy

The refactoring will be done in stages to minimize disruption to the codebase:

1. Create the new structure without changing existing code
2. Gradually move functionality from Hero to the new classes
3. Update references to Hero only after the new classes are stable
4. Run extensive tests to ensure that game mechanics are preserved

This approach will allow us to maintain a working game throughout the refactoring process.