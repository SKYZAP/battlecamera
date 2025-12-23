package com.skyzap.battlecamera.client;

import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side listener for Pixelmon battle events.
 * Sets camera angle when battles start.
 */
@OnlyIn(Dist.CLIENT)
public class ClientBattleListener {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @SubscribeEvent
    public static void onBattleStarted(BattleStartedEvent.Post event) {
        // Schedule camera change after a short delay to let Pixelmon initialize the
        // camera
        scheduler.schedule(() -> {
            Minecraft.getInstance().execute(() -> {
                // Set camera to forward-facing battle view
                // radius: distance from target (1-30)
                // theta: vertical angle (0.1=overhead, 1.5=forward facing, 1.8=ground level)
                // phi: horizontal rotation in radians (3.14 = behind player facing enemy)
                ClientCameraHandler.setCameraAngle(6.0f, 1.4f, 3.14f);
            });
        }, 500, TimeUnit.MILLISECONDS);
    }
}
