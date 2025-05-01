# Visual Enhancements Technical Documentation

This document describes the visual enhancements implemented in the Deeper Darkness extension, particularly for the Purgatory levels (30+).

## High Resolution Tiles for Purgatory Levels (30+)

Starting with level 30 (Purgatory levels), the game switches to higher resolution tile graphics for improved visual quality. While the standard game uses 16x16 pixel tiles, Purgatory levels use 64x64 pixel tiles for significantly enhanced detail.

### Implementation Details

1. **Resolution Increase**:
   - Standard levels (1-29): 16x16 pixel tiles (original)
   - Purgatory levels (30+): 64x64 pixel tiles (4x resolution increase)

2. **Rendering Approach**:
   - The rendered size on screen remains the same
   - Only the graphical detail increases, not the UI scale
   - Modern smartphones and desktop computers easily handle the increased texture resolution

3. **Technical Implementation**:
   - A custom `HighResDungeonTilemap` class extends the base `DungeonTilemap`
   - The game detects level depth and switches tilemap implementations accordingly
   - Level geometry and game mechanics remain unchanged

### High-Resolution Tilemap Classes

The high-resolution implementation consists of several key classes:

1. **HighResDungeonTilemap**: Base abstract class that handles scaling and coordinate transformations
   - Uses the same cell coordinates and level data
   - Maintains the same visual size on screen (scaled down from 64x64 to appear as 16x16)
   - Preserves all gameplay mechanics

2. **HighResTilemap**: Concrete implementation for dungeon floor and wall tiles
   - Handles appropriate texture selection based on level type
   - Implements the visual determination logic for different tile types

3. **HighResTerrainFeaturesTilemap**: Handles terrain features like doors, traps, and vegetation
   - Works with the same tile indices as the standard implementation
   - Uses higher resolution assets for visual enhancement

### TilemapFactory Class

A factory class that creates the appropriate tilemap based on level depth:
- Uses `isHighResLevel()` to determine when to switch to high-resolution assets
- Creates either standard or high-resolution tilemaps as needed
- Allows for seamless transitions between resolution types

### Asset Organization

High-resolution assets follow this organization pattern:

```
/core/src/main/assets/environment/
├── tiles_purgatory.png            # 64x64 tileset for Purgatory levels
├── tiles_purgatory_features.png   # 64x64 terrain features
└── [other high-res assets]
```

### Technical Considerations

- Memory usage increases due to larger textures, but remains well within limits of modern devices
- All rendering calculations account for the resolution difference
- Transition between standard and high-res levels is seamless to the player
- Visual effects and particles are scaled appropriately

### Implementation Timeline

This enhancement is implemented as part of the Purgatory levels expansion, providing visual distinction for the new endgame content while maintaining the game's pixel art style with enhanced detail.

## Future Enhancement Possibilities

- Dynamic resolution scaling based on device capabilities
- Additional visual effects leveraging the higher resolution
- Normal/bump mapping for enhanced lighting effects
- Animated tile elements for environmental storytelling