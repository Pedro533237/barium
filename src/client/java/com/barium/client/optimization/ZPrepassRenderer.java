package com.barium.client.optimization;

import com.barium.BariumMod;
import com.barium.config.BariumConfig;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utilitário de Z-prepass para renderizadores opacos custom.
 */
public final class ZPrepassRenderer {

    @FunctionalInterface
    public interface OpaqueGeometryRenderer {
        void render();
    }

    private static final List<OpaqueGeometryRenderer> OPAQUE_RENDERERS = new CopyOnWriteArrayList<>();
    private static final AtomicLong FRAMES_EXECUTED = new AtomicLong();

    private ZPrepassRenderer() {
    }

    public static void initialize() {
        BariumMod.LOGGER.info("ZPrepassRenderer initialized.");
    }

    public static void registerOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.add(renderer);
    }

    public static void unregisterOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.remove(renderer);
    }

    public static boolean isActive() {
        return BariumConfig.C.ENABLE_Z_PREPASS && !OPAQUE_RENDERERS.isEmpty();
    }

    public static long getFramesExecuted() {
        return FRAMES_EXECUTED.get();
    }

    public static int getRegisteredRendererCount() {
        return OPAQUE_RENDERERS.size();
    }

    public static void runPrepassForRegisteredRenderers() {
        if (!isActive()) {
            return;
        }

        beginDepthPrepass();
        renderRegisteredGeometry();

        beginMainColorPass();
        renderRegisteredGeometry();

        restoreDefaultState();
        FRAMES_EXECUTED.incrementAndGet();
    }

    public static void beginDepthPrepass() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(true);

        if (BariumConfig.C.ENABLE_VERTEX_CULLING) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
        }
    }

    public static void beginMainColorPass() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        if (BariumConfig.C.ENABLE_VERTEX_CULLING) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
        }
    }

    public static void restoreDefaultState() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    private static void renderRegisteredGeometry() {
        for (OpaqueGeometryRenderer renderer : OPAQUE_RENDERERS) {
            try {
                renderer.render();
            } catch (Throwable throwable) {
                BariumMod.LOGGER.error("Erro durante execução do Z-prepass", throwable);
            }
        }
    }
}
