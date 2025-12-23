package com.skyzap.battlecamera.client;

import com.skyzap.battlecamera.BattleCameraMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    public static void init(IEventBus modEventBus) {
        BattleCameraMod.LOGGER.info("Client setup initialized");
        // Client-side setup complete
        // Camera changes are handled via network packets from server
    }
}
