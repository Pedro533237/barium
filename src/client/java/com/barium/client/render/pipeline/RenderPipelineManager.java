package com.barium.client.render.pipeline;

import com.barium.config.BariumConfig;

/**
 * Pipeline simplificada com suporte a Z-prepass opcional.
 * A troca de estado GL fica centralizada aqui para facilitar integração com Blaze3D.
 */
public final class RenderPipelineManager {

    private static boolean depthPrepassActive;

    private RenderPipelineManager() {
    }

    public static void beginFrame() {
        RenderDebugMetrics.resetFrame();
    }

    public static void beginDepthPrepassIfEnabled() {
        if (!BariumConfig.C.ENABLE_Z_PREPASS) {
            return;
        }

        // Hook central para depth-only pass.
        depthPrepassActive = true;
    }

    public static void endDepthPrepassIfEnabled() {
        if (!depthPrepassActive) {
            return;
        }

        depthPrepassActive = false;
    }

    public static boolean isDepthPrepassActive() {
        return depthPrepassActive;
    }
}
