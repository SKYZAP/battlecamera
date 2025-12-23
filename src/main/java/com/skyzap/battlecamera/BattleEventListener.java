package com.skyzap.battlecamera;

import com.skyzap.battlecamera.network.NetworkHandler;
import com.skyzap.battlecamera.network.SetCameraAnglePacket;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * Server-side event listener for Pixelmon battle events.
 * Sends camera angle packets to clients when battles start.
 */
public class BattleEventListener {

    @SubscribeEvent
    public void onBattleStarted(BattleStartedEvent.Post event) {
        processTeam(event.getTeamOne());
        processTeam(event.getTeamTwo());
    }

    private void processTeam(BattleParticipant[] team) {
        if (team == null) return;

        for (BattleParticipant participant : team) {
            if (!(participant instanceof PlayerParticipant playerParticipant)) {
                continue;
            }

            ServerPlayer player = playerParticipant.player;
            if (player == null) {
                continue;
            }

            // Camera angle settings:
            // radius: distance from target (1-30)
            // theta: vertical angle (0.1=overhead, 1.4=forward facing, 1.8=ground level)
            // phi: horizontal rotation (3.14 = behind player's Pokemon facing enemy)
            float radius = 6.0f;
            float theta = 1.4f;
            float phi = 3.14f;

            sendCameraPacket(player, radius, theta, phi);
        }
    }

    private void sendCameraPacket(ServerPlayer player, float radius, float theta, float phi) {
        SetCameraAnglePacket packet = new SetCameraAnglePacket(radius, theta, phi);
        NetworkHandler.sendToPlayer(player, packet);
    }
}

