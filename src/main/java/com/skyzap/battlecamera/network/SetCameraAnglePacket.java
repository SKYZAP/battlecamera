package com.skyzap.battlecamera.network;

import com.skyzap.battlecamera.client.ClientCameraHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from server to client to set the battle camera angle.
 * 
 * The camera uses spherical coordinates:
 * - radius: Distance from target (1.0 to 30.0)
 * - theta: Vertical angle in radians (0.1 to 1.8, where lower = more overhead view)
 * - phi: Horizontal angle in radians (rotation around the target)
 */
public record SetCameraAnglePacket(float radius, float theta, float phi) implements CustomPacketPayload {

    public static final ResourceLocation ID = NetworkHandler.id("set_camera_angle");
    public static final CustomPacketPayload.Type<SetCameraAnglePacket> TYPE = 
            new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, SetCameraAnglePacket> STREAM_CODEC = 
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, SetCameraAnglePacket::radius,
                    ByteBufCodecs.FLOAT, SetCameraAnglePacket::theta,
                    ByteBufCodecs.FLOAT, SetCameraAnglePacket::phi,
                    SetCameraAnglePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Handle this packet on the client side.
     */
    public static void handleOnClient(SetCameraAnglePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientCameraHandler.setCameraAngle(packet.radius(), packet.theta(), packet.phi());
        });
    }
}

