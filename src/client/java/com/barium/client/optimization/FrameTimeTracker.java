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
		AVERAGE_FRAME_NANOS.updateAndGet(previous -> ((previous * 7L) + frameTime) / 8L);
	}

	public static long getAverageFrameNanos() {
		return AVERAGE_FRAME_NANOS.get();
	}
}
