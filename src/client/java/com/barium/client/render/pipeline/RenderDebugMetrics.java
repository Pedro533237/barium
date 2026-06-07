package com.barium.client.render.pipeline;

import java.util.concurrent.atomic.AtomicInteger;

public final class RenderDebugMetrics {
    private static final AtomicInteger CULLED_FACES = new AtomicInteger();
    private static final AtomicInteger CULLED_CHUNKS = new AtomicInteger();

    private RenderDebugMetrics() {
    }

    public static void resetFrame() {
        CULLED_FACES.set(0);
        CULLED_CHUNKS.set(0);
    }

    public static void addCulledFace() {
        CULLED_FACES.incrementAndGet();
    }

    public static void addCulledChunk() {
        CULLED_CHUNKS.incrementAndGet();
    }

    public static int getCulledFaces() {
        return CULLED_FACES.get();
    }

    public static int getCulledChunks() {
        return CULLED_CHUNKS.get();
    }
}
