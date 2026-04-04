package com.barium.client.optimization;

import com.barium.BariumMod;
import com.barium.config.BariumConfig;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Utilitário de Z-prepass para renderizadores opacos custom.
 *
 * Não depende de Fabric WorldRenderEvents: o renderer custom chama
 * runPrepassForRegisteredRenderers() no momento apropriado do pipeline.
 */
public final class ZPrepassRenderer {

    @FunctionalInterface
    public interface OpaqueGeometryRenderer {
        void render();
    }

    private static final List<OpaqueGeometryRenderer> OPAQUE_RENDERERS = new CopyOnWriteArrayList<>();

    private ZPrepassRenderer() {
    }

    public static void initialize() {
        BariumMod.LOGGER.info("ZPrepassRenderer ready. Waiting for custom opaque renderers registration.");
    }

    public static void registerOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.add(renderer);
    }

    public static void unregisterOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.remove(renderer);
    }

    public static void runPrepassForRegisteredRenderers() {
        if (!BariumConfig.C.ENABLE_Z_PREPASS || OPAQUE_RENDERERS.isEmpty()) {
            return;
        }

        beginDepthPrepass();
        renderRegisteredGeometry();

        beginMainColorPass();
        renderRegisteredGeometry();

        restoreDefaultState();
    }

    public static void beginDepthPrepass() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(true);
    }

    public static void beginMainColorPass() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(false);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    public static void restoreDefaultState() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
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
