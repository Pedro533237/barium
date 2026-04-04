package com.barium.client.optimization;

import com.barium.BariumMod;
import com.barium.config.BariumConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Pipeline simples de Z-Prepass para geometrias opacas registradas pelo mod.
 * <p>
 * Importante: este pipeline NÃO tenta substituir o renderer vanilla/Sodium.
 * Ele só executa para renderizadores customizados que registrarem callbacks aqui.
 */
public final class ZPrepassRenderer {

    @FunctionalInterface
    public interface OpaqueGeometryRenderer {
        void render(WorldRenderContext context);
    }

    private static final List<OpaqueGeometryRenderer> OPAQUE_RENDERERS = new CopyOnWriteArrayList<>();

    private ZPrepassRenderer() {
    }

    public static void initialize() {
        WorldRenderEvents.AFTER_ENTITIES.register(ZPrepassRenderer::renderTwoPassOpaqueGeometry);
    }

    public static void registerOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.add(renderer);
    }

    public static void unregisterOpaqueRenderer(OpaqueGeometryRenderer renderer) {
        OPAQUE_RENDERERS.remove(renderer);
    }

    private static void renderTwoPassOpaqueGeometry(WorldRenderContext context) {
        if (!BariumConfig.C.ENABLE_Z_PREPASS || OPAQUE_RENDERERS.isEmpty()) {
            return;
        }

        runDepthOnlyPass(context);
        runMainColorPass(context);
        restoreDefaultState();
    }

    private static void runDepthOnlyPass(WorldRenderContext context) {
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LESS);
        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);

        for (OpaqueGeometryRenderer renderer : OPAQUE_RENDERERS) {
            try {
                renderer.render(context);
            } catch (Throwable throwable) {
                BariumMod.LOGGER.error("Erro durante depth prepass de geometria opaca", throwable);
            }
        }
    }

    private static void runMainColorPass(WorldRenderContext context) {
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        for (OpaqueGeometryRenderer renderer : OPAQUE_RENDERERS) {
            try {
                renderer.render(context);
            } catch (Throwable throwable) {
                BariumMod.LOGGER.error("Erro durante render principal de geometria opaca", throwable);
            }
        }
    }

    private static void restoreDefaultState() {
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
    }
}
