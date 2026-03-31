package com.barium.client.optimization;

import com.barium.config.BariumConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public final class HandRenderOptimizer {
    private static int frameCounter = 0;

    private HandRenderOptimizer() {
    }

    public static boolean shouldSkipHandRender() {
        if (!BariumConfig.C.ENABLE_HAND_RENDER_THROTTLING) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null || client.currentScreen != null) {
            return false;
        }

        if (player.isUsingItem() || player.isBlocking() || player.isRiding()) {
            return false;
        }

        int skipFrames = Math.max(1, BariumConfig.C.HAND_RENDER_SKIP_FRAMES);
        frameCounter = (frameCounter + 1) % (skipFrames + 1);
        return frameCounter != 0;
    }
}
