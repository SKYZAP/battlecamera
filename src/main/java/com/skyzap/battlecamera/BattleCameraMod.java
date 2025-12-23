package com.skyzap.battlecamera;

import com.skyzap.battlecamera.client.ClientSetup;
import com.skyzap.battlecamera.network.NetworkHandler;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BattleCameraMod.MODID)
public class BattleCameraMod {
    public static final String MODID = "battlecamera";
    public static final Logger LOGGER = LoggerFactory.getLogger(BattleCameraMod.class);

    public BattleCameraMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("BattleCameraMod initializing...");

        // Register network packets (needed on both sides)
        NetworkHandler.register(modEventBus);

        // Register server-side battle event listener on Pixelmon's event bus
        Pixelmon.EVENT_BUS.register(new BattleEventListener());

        // Register client-side code only on client
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(modEventBus);
        }
    }
}
