# High-Resolution Graphics System

This document explains the high-resolution graphics system implemented for Shattered Pixel Deeper Darkness, specifically for the Purgatory levels (depth 30+).

## Overview

The game uses a dynamic tilemap system that switches between standard resolution (16×16 pixel) tiles and high-resolution (64×64 pixel) tiles based on the current level. This approach allows for higher visual fidelity in later game levels while maintaining the same gameplay mechanics.

## Key Components

### 1. TilemapFactory

The `TilemapFactory` class is the core of the high-resolution system. It creates the appropriate tilemap implementations based on the current level.

```java
public static boolean isHighResLevel() {
    // Check if force high-res mode is enabled (for testing)
    if (forceHighRes) {
        return true;
    }
    
    // Check if the current level is a Purgatory level (depth 30+)
    if (Dungeon.depth >= 30) {
        return true;
    }
    
    // Also check for specific Purgatory-related level classes
    Level currentLevel = Dungeon.level;
    return currentLevel instanceof PurgatoryLevel || 
           currentLevel instanceof PurgatoryBossLevel;
}
```

The factory provides methods to create the appropriate tilemaps:
- `createDungeonTilemap()`
- `createTerrainFeaturesTilemap(plants, traps)`
- `createWallsTilemap()`
- `createRaisedTerrainTilemap()`

### 2. High-Resolution Tilemap Implementations

The system includes high-resolution versions of all tilemap classes:

- `HighResDungeonTilemap`: Abstract base class for all high-resolution tilemaps
- `HighResDungeonTerrainTilemap`: Renders terrain (floors, water, chasms, etc.)
- `HighResTerrainFeaturesTilemap`: Renders plants, traps, and other features
- `HighResWallsTilemap`: Renders walls and doors
- `HighResRaisedTerrainTilemap`: Renders raised features (statues, high grass)

### 3. Asset Management

The high-resolution system uses dedicated asset paths for Purgatory levels:

```java
// Standard resolution assets
public static final String TILES_SEWERS = "environment/tiles_sewers.png";
// ... other standard assets

// High-resolution assets for Purgatory
public static final String TILES_PURGATORY = "environment/tiles_purgatory.png";
public static final String TILES_PURGATORY_FEATURES = "environment/tiles_terrain_features.png";
```

## Technical Implementation Details

### Tile Size and Scaling

The key insight is that the game's camera and rendering system already handle appropriate scaling. The high-resolution system simply uses larger textures without additional manual scaling:

```java
// Standard tile size
public static final int SIZE = 16;

// High-resolution tile size
public static final int SIZE = 64;
```

The game engine's default zoom and camera system automatically handle the visual scaling consistently across all game elements.

### Coordinate Transformations

Coordinate transformations are adjusted for the different tile sizes:

```java
// Convert screen coordinates to tile coordinates
public int screenToTile(int x, int y, boolean wallAssist) {
    // Convert from screen to world coordinates
    PointF p = camera().screenToCamera(x, y)
            .offset(this.point().negate())
            .invScale(SIZE); // Divide by tile size
    
    // ... further processing
}

// Convert tile coordinates to world coordinates
public static PointF tileToWorld(int pos) {
    return new PointF(
            (pos % Dungeon.level.width()) * SIZE,
            (pos / Dungeon.level.width()) * SIZE
    );
}
```

### Texture Initialization

High-resolution tilemaps initialize their textures similarly to standard tilemaps but with larger tile sizes:

```java
// Standard resolution
tileset = new TextureFilm(texture, 16, 16);

// High resolution
tileset = new TextureFilm(texture, 64, 64);
```

### Implementation in GameScene

The `GameScene` handles creating tilemaps and other visual elements with awareness of the high-resolution system:

```java
boolean highRes = TilemapFactory.isHighResLevel();
int tileSize = highRes ? HighResDungeonTilemap.SIZE : DungeonTilemap.SIZE;

water = new SkinnedBlock(
    Dungeon.level.width() * tileSize,
    Dungeon.level.height() * tileSize,
    Dungeon.level.waterTex()
);

tiles = TilemapFactory.createDungeonTilemap();
terrainFeatures = TilemapFactory.createTerrainFeaturesTilemap(plants, traps);
raisedTerrain = TilemapFactory.createRaisedTerrainTilemap();
walls = TilemapFactory.createWallsTilemap();
```

## Testing the High-Resolution System

### Testing on Purgatory Levels

To test the high-resolution system during normal gameplay, reach level 30 or higher (Purgatory levels).

### Forcing High-Resolution Mode

For testing purposes, you can force high-resolution mode on any level:

```java
TilemapFactory.setForceHighResMode(true);
```

This is useful for testing the high-resolution graphics on earlier levels without having to reach level 30.

## Creating High-Resolution Assets

When creating high-resolution assets:

1. Maintain the same tile layout and indexing as standard assets
2. Use 64×64 pixel tiles instead of 16×16 
3. Keep the same grid structure (usually 16 tiles per row)
4. Place them in the appropriate asset paths

## Common Issues and Solutions

### Tiles Appearing Too Small

If high-resolution tiles appear too small, check for double-scaling issues. The tilemap should not apply any additional scaling factor; the game's camera system will handle appropriate scaling.

### Texture Path Issues

Ensure texture paths are correctly set in the high-resolution tilemap constructors. For classes that inherit from tilemaps with parameterized constructors, make sure to pass the correct texture path:

```java
super(plants, traps);  // For classes with parameters
texture = TextureCache.get(Assets.Environment.TILES_PURGATORY_FEATURES);
```

### Coordinate Transformation Issues

If picking/selection doesn't work correctly, check the coordinate transformation methods (`screenToTile`, `tileToWorld`, etc.) to ensure they're using the correct tile size and not applying additional scaling.

## Conclusion

The high-resolution graphics system provides a seamless way to enhance visual fidelity for later game levels while maintaining consistent gameplay mechanics. By using the proper asset paths and tile sizes without manual scaling, the system integrates naturally with the game's existing rendering pipeline.