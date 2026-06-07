package com.barium.client.optimization;

import java.util.concurrent.atomic.AtomicLong;

public final class FrameTimeTracker {
    private static final AtomicLong AVERAGE_FRAME_NANOS = new AtomicLong(16_000_000L);

    private FrameTimeTracker() {
    }

    public static long beginFrame() {
        return System.nanoTime();
    }

    public static void endFrame(long startedAtNanos) {
        long frameTime = Math.max(0L, System.nanoTime() - startedAtNanos);
        long previous;
        long next;
        do {
            previous = AVERAGE_FRAME_NANOS.get();
            next = ((previous * 7L) + frameTime) >> 3;
        } while (!AVERAGE_FRAME_NANOS.compareAndSet(previous, next));
    }

    public static long getAverageFrameNanos() {
        return AVERAGE_FRAME_NANOS.get();
    }
}
