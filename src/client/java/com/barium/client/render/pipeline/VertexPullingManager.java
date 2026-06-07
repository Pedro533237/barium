package com.barium.client.render.pipeline;

import com.barium.BariumMod;
import com.barium.config.BariumConfig;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pipeline experimental de vertex pulling para renderizadores custom.
 *
 * Cada face pode ser representada por 1 inteiro packed (x,y,z) e expandida no vertex shader via gl_VertexID.
 */
public final class VertexPullingManager {

    @FunctionalInterface
    public interface PackedFaceRenderer {
        void render();
    }

    private static final List<PackedFaceRenderer> RENDERERS = new CopyOnWriteArrayList<>();
    private static final AtomicLong FRAMES_EXECUTED = new AtomicLong();

    private VertexPullingManager() {
    }

    public static void initialize() {
        BariumMod.LOGGER.info("VertexPullingManager initialized.");
    }

    public static void registerRenderer(PackedFaceRenderer renderer) {
        RENDERERS.add(renderer);
    }

    public static void unregisterRenderer(PackedFaceRenderer renderer) {
        RENDERERS.remove(renderer);
    }

    public static boolean isActive() {
        return BariumConfig.C.ENABLE_VERTEX_PULLING && !RENDERERS.isEmpty();
    }

    public static long getFramesExecuted() {
        return FRAMES_EXECUTED.get();
    }

    public static int getRendererCount() {
        return RENDERERS.size();
    }

    public static void run() {
        if (!isActive()) {
            return;
        }

        for (PackedFaceRenderer renderer : RENDERERS) {
            try {
                renderer.render();
            } catch (Throwable throwable) {
                BariumMod.LOGGER.error("Erro durante vertex pulling", throwable);
            }
        }

        FRAMES_EXECUTED.incrementAndGet();
    }

    /**
     * Empacota coordenadas de face (0..1023) em 1 uint/int (10 bits por eixo).
     */
    public static int packFacePosition(int x, int y, int z) {
        return (x & 1023) | ((y & 1023) << 10) | ((z & 1023) << 20);
    }
}
