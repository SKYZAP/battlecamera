# Battle Camera Mod

A NeoForge 1.21.1 mod that customizes the camera angle during Pixelmon battles.

## Quick Start

1. **Add Pixelmon dependency**:
   - Copy `Pixelmon-1.21.1-9.3.9-server.jar` to the `libs/` folder
   - Uncomment the `compileOnly files(...)` line in `build.gradle`

2. **Build the mod**:
   ```bash
   ./gradlew build
   ```

3. **Install**:
   - Copy the JAR from `build/libs/` to your mods folder
   - **Required on both server AND client**

## How It Works

1. Server listens to `BattleStartedEvent.Post` from Pixelmon
2. Server sends a `SetCameraAnglePacket` to each player in the battle
3. Client receives packet and modifies the battle camera angle

## Customization

Edit `BattleEventListener.java` to change camera parameters:

```java
float radius = 8.0f;   // Distance from target (1.0 - 30.0)
float theta = 0.8f;    // Vertical angle (0.1 = overhead, 1.8 = ground level)
float phi = 0.0f;      // Horizontal rotation around target
```

## Camera Presets

The `ClientCameraHandler` includes presets:
- `OVERHEAD` - Bird's eye view
- `CLOSE_UP` - Dramatic close angle
- `WIDE_ANGLE` - Panoramic view
- `SIDE_VIEW` - 90 degree side angle
- `BEHIND_PLAYER` - Behind the trainer
- `DEFAULT` - Standard Pixelmon camera

