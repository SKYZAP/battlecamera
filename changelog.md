# Battle Camera Mod - Changelog

## Overview

This NeoForge 1.21.1 mod customizes the camera angle during Pixelmon battles. It listens to server-side battle events and sends packets to clients to adjust their camera position.

---

## Version 1.0.0 (Initial Release)

### Features

- **Server-Side Battle Event Listener**: Listens to `BattleStartedEvent.Post` from Pixelmon API
- **Custom Network Packet**: `SetCameraAnglePacket` sends camera parameters from server to client
- **Client-Side Camera Manipulation**: Uses reflection to modify Pixelmon's `PlayerControlledMovement` camera fields
- **Camera Presets**: Includes preset camera angles (overhead, close-up, wide angle, side view, etc.)

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        SERVER SIDE                          │
├─────────────────────────────────────────────────────────────┤
│  BattleEventListener                                        │
│  └── Subscribes to BattleStartedEvent.Post                  │
│  └── Extracts PlayerParticipant from battle                 │
│  └── Sends SetCameraAnglePacket to player                   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼ Network Packet
┌─────────────────────────────────────────────────────────────┐
│                        CLIENT SIDE                          │
├─────────────────────────────────────────────────────────────┤
│  SetCameraAnglePacket.handleOnClient()                      │
│  └── Calls ClientCameraHandler.setCameraAngle()             │
│                                                             │
│  ClientCameraHandler                                        │
│  └── Accesses Pixelmon's ClientProxy.camera                 │
│  └── Uses reflection to set radius, theta, phi fields       │
│  └── Calls movement.updatePosition() to apply changes       │
└─────────────────────────────────────────────────────────────┘
```

### Camera Coordinate System

The camera uses **spherical coordinates** orbiting around a target entity:

| Parameter | Description | Valid Range |
|-----------|-------------|-------------|
| `radius` | Distance from target | 1.0 - 30.0 |
| `theta` | Vertical angle (radians) | 0.1 - 1.8 |
| `phi` | Horizontal angle (radians) | Any value |

- **Lower theta** = more overhead view (looking down)
- **Higher theta** = more horizontal view (eye level)
- **Phi** controls rotation around the target

### Files Created

| File | Purpose |
|------|---------|
| `BattleCameraMod.java` | Main mod entry point, registers events and network |
| `BattleEventListener.java` | Server-side Pixelmon event listener |
| `NetworkHandler.java` | NeoForge network packet registration |
| `SetCameraAnglePacket.java` | Custom packet for camera angle data |
| `ClientCameraHandler.java` | Client-side camera manipulation using reflection |
| `ClientBattleEventListener.java` | Alternative client-only event listener |
| `neoforge.mods.toml` | Mod metadata and dependencies |

### Two Approaches

**Approach 1: Server + Client (Recommended for multiplayer)**
- Server listens to `BattleStartedEvent.Post`
- Server sends packet to client
- Client applies camera change
- Requires mod on both server and client

**Approach 2: Client-only (Simpler)**
- Client listens to `BattleStartedEvent.Post` 
- Only works in singleplayer (events don't fire on client in multiplayer)
- Use `ClientBattleEventListener` instead

### Dependencies

- **NeoForge**: 21.1.172+
- **Minecraft**: 1.21.1
- **Pixelmon**: 9.3.9+ (1.21.1)
- **Java**: 21

### Installation

1. Copy the Pixelmon JAR to `libs/` folder (or configure maven repository)
2. Run `./gradlew build`
3. Find the compiled JAR in `build/libs/`
4. Install on both server AND client (requires client-side component for camera)

### Important Notes

1. **Not purely server-side**: While the event detection is server-side, camera manipulation MUST happen on the client. This mod requires installation on both server and client.

2. **Reflection usage**: The mod uses reflection to access Pixelmon's private camera fields. This may break if Pixelmon changes its internal structure.

3. **Timing considerations**: The camera may not be initialized immediately when the battle starts. You may need to add a small delay.

---

## Known Limitations

- Camera changes only apply to the default `PlayerControlledMovement` mode
- Cannot modify camera during spectator mode without additional code
- Reflection may fail if Pixelmon obfuscates field names differently

---

## Future Improvements

- [ ] Add configuration file for camera angles
- [ ] Support per-Pokemon camera angles
- [ ] Add smooth camera transitions
- [ ] Add command to change camera angle mid-battle
- [ ] Support for spectator camera customization

