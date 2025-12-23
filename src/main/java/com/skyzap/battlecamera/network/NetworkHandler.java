package com.skyzap.battlecamera.network;

import com.skyzap.battlecamera.BattleCameraMod;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NetworkHandler::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(BattleCameraMod.MODID)
                .versioned(PROTOCOL_VERSION);

        // Register the camera angle packet (server -> client)
        registrar.playToClient(
                SetCameraAnglePacket.TYPE,
                SetCameraAnglePacket.STREAM_CODEC,
                SetCameraAnglePacket::handleOnClient
        );
    }

    /**
     * Send a packet to a specific player
     */
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    /**
     * Helper to create ResourceLocation for this mod
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(BattleCameraMod.MODID, path);
    }
}

