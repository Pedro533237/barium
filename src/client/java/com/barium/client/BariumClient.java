package com.barium.client;

import com.barium.BariumMod;
import com.barium.client.chunk.ClientChunkManager;
import com.barium.client.config.BariumConfigScreen;
import com.barium.client.optimization.ParticleOptimizer;
import com.barium.client.util.ChunkRenderManager;
import com.barium.client.util.ChunkVisibilityManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
public class BariumClient implements ClientModInitializer {

    private static BariumClient instance;

    private final ChunkRenderManager chunkRenderManager = ChunkRenderManager.getInstance();

    public static final ExecutorService RENDER_THREAD_POOL = Executors.newSingleThreadExecutor(new ThreadFactory() {
        private final AtomicInteger threadId = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Barium Render Thread #" + threadId.incrementAndGet());
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    });

    @Override
    public void onInitializeClient() {
        instance = this;
        BariumMod.LOGGER.info("Initializing Barium Client...");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) {
                ChunkVisibilityManager.getInstance().clear();
                this.chunkRenderManager.clear();
                ClientChunkManager.getInstance().clear();
                ParticleOptimizer.resetParticleCount();
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isGraphicsOptionsScreen(screen)) {
                return;
            }

            int buttonWidth = 150;
            int buttonHeight = 20;
            int x = screen.width / 2 - 155;
            int y = screen.height - 27;

            ScreenEvents.getButtons(screen).add(ButtonWidget.builder(Text.translatable("title.barium.config"), button ->
                            client.setScreen(BariumConfigScreen.create(screen)))
                    .dimensions(x, y, buttonWidth, buttonHeight)
                    .build());
        });

        BariumMod.LOGGER.info("Barium Client Initialized.");
    }

    private static boolean isGraphicsOptionsScreen(Screen screen) {
        if (screen instanceof VideoOptionsScreen) {
            return true;
        }

        String className = screen.getClass().getName().toLowerCase(Locale.ROOT);

        boolean isSodiumScreen = className.contains("sodium") && className.contains("screen");
        boolean isVulkanModScreen = className.contains("vulkanmod") && className.contains("screen");
        boolean isVideoOrGraphics = className.contains("video") || className.contains("graphics") || className.contains("option");

        return (isSodiumScreen || isVulkanModScreen) && isVideoOrGraphics;
    }

    private static int currentFps = 60;
    private static long lastFpsUpdate = 0;
    private static int frameCount = 0;

    public static int getCurrentFps() {
        return currentFps;
    }

    public static void setCurrentFps(int fps) {
        currentFps = fps;
    }

    public static void updateFps() {
        frameCount++;
        long now = System.currentTimeMillis();
        if (now - lastFpsUpdate >= 1000) {
            currentFps = frameCount;
            frameCount = 0;
            lastFpsUpdate = now;
        }
    }
}
