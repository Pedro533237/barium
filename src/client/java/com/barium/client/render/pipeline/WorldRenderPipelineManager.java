package com.barium.client.render.pipeline;

import com.barium.BariumMod;
import com.barium.config.BariumConfig;
import net.minecraft.util.profiler.Profilers;

/**
 * Orquestrador de pipeline de render do mundo no fim do WorldRenderer.render.
 *
 * Importante: isto NÃO substitui totalmente o renderer vanilla/Sodium,
 * mas centraliza os estágios custom e permite evoluir para um pipeline completo.
 */
public final class WorldRenderPipelineManager {

    private WorldRenderPipelineManager() {
    }

    public static void initialize() {
        ZPrepassRenderer.initialize();
        VertexPullingManager.initialize();
        BariumMod.LOGGER.info("WorldRenderPipelineManager initialized.");
    }

    public static void runTailPipelines() {
        if (BariumConfig.C.ENABLE_Z_PREPASS) {
            Profilers.get().push("barium_z_prepass");
            ZPrepassRenderer.runPrepassForRegisteredRenderers();
            Profilers.get().pop();
        }

        if (BariumConfig.C.ENABLE_VERTEX_PULLING) {
            Profilers.get().push("barium_vertex_pulling");
            VertexPullingManager.run();
            Profilers.get().pop();
        }
    }
}
