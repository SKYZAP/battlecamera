package com.skyzap.battlecamera.client;

import com.pixelmonmod.pixelmon.client.ClientProxy;
import com.pixelmonmod.pixelmon.client.camera.CameraEntity;
import com.pixelmonmod.pixelmon.client.camera.movement.CameraMovement;
import com.skyzap.battlecamera.BattleCameraMod;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side handler for camera manipulation.
 * Uses reflection to access Pixelmon's camera internals.
 */
@OnlyIn(Dist.CLIENT)
public class ClientCameraHandler {

    private static Field radiusField;
    private static Field thetaField;
    private static Field phiField;
    private static boolean reflectionInitialized = false;
    private static boolean reflectionFailed = false;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Initialize reflection fields for PlayerControlledMovement.
     */
    private static void initReflection() {
        if (reflectionInitialized || reflectionFailed) {
            return;
        }

        try {
            Class<?> movementClass = Class.forName(
                    "com.pixelmonmod.pixelmon.client.camera.movement.PlayerControlledMovement");

            radiusField = movementClass.getDeclaredField("radius");
            radiusField.setAccessible(true);

            thetaField = movementClass.getDeclaredField("theta");
            thetaField.setAccessible(true);

            phiField = movementClass.getDeclaredField("phi");
            phiField.setAccessible(true);

            reflectionInitialized = true;

        } catch (ClassNotFoundException | NoSuchFieldException e) {
            BattleCameraMod.LOGGER.error("Failed to initialize camera reflection", e);
            reflectionFailed = true;
        }
    }

    /**
     * Set the camera angle for the current battle camera.
     * Will retry if camera isn't ready yet.
     *
     * @param radius Distance from target (1.0 to 30.0)
     * @param theta  Vertical angle in radians (0.1=overhead, 1.5=forward,
     *               1.8=ground)
     * @param phi    Horizontal rotation in radians
     */
    public static void setCameraAngle(float radius, float theta, float phi) {
        setCameraAngleWithRetry(radius, theta, phi, 0, 5);
    }

    private static void setCameraAngleWithRetry(float radius, float theta, float phi, int attempt, int maxAttempts) {
        boolean success = trySetCameraAngle(radius, theta, phi);

        if (!success && attempt < maxAttempts - 1) {
            scheduler.schedule(() -> {
                Minecraft.getInstance().execute(() -> {
                    setCameraAngleWithRetry(radius, theta, phi, attempt + 1, maxAttempts);
                });
            }, 300, TimeUnit.MILLISECONDS);
        }
    }

    private static boolean trySetCameraAngle(float radius, float theta, float phi) {
        initReflection();

        if (reflectionFailed) {
            return false;
        }

        CameraEntity camera = ClientProxy.camera;
        if (camera == null) {
            return false;
        }

        CameraMovement movement = camera.getMovement();
        if (movement == null) {
            return false;
        }

        String movementClassName = movement.getClass().getSimpleName();
        if (!movementClassName.equals("PlayerControlledMovement")) {
            return false;
        }

        try {
            float clampedRadius = Math.max(1.0f, Math.min(30.0f, radius));
            float clampedTheta = Math.max(0.1f, Math.min(1.8f, theta));

            radiusField.setFloat(movement, clampedRadius);
            thetaField.setFloat(movement, clampedTheta);
            phiField.setFloat(movement, phi);

            movement.updatePosition();
            return true;

        } catch (IllegalAccessException e) {
            BattleCameraMod.LOGGER.error("Failed to set camera fields", e);
            return false;
        }
    }

    /**
     * Set camera to a preset angle.
     */
    public static void setCameraPreset(CameraPreset preset) {
        switch (preset) {
            case OVERHEAD -> setCameraAngle(10.0f, 0.3f, 0.0f);
            case CLOSE_UP -> setCameraAngle(4.0f, 1.3f, 3.14f);
            case WIDE_ANGLE -> setCameraAngle(12.0f, 1.2f, 3.14f);
            case SIDE_VIEW -> setCameraAngle(8.0f, 1.3f, 1.57f);
            case FORWARD_BATTLE -> setCameraAngle(6.0f, 1.4f, 3.14f);
            case DEFAULT -> setCameraAngle(4.0f, 1.0f, 0.0f);
        }
    }

    public enum CameraPreset {
        DEFAULT,
        OVERHEAD,
        CLOSE_UP,
        WIDE_ANGLE,
        SIDE_VIEW,
        FORWARD_BATTLE
    }
}
